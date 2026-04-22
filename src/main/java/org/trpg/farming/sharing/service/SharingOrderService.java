package org.trpg.farming.sharing.service;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trpg.farming.sharing.common.BusinessException;
import org.trpg.farming.sharing.common.PageResult;
import org.trpg.farming.sharing.constant.SharingConstants;
import org.trpg.farming.sharing.dto.request.CreateOrderRequest;
import org.trpg.farming.sharing.dto.request.ShipOrderRequest;
import org.trpg.farming.sharing.dto.response.SharingOrderView;
import org.trpg.farming.sharing.entity.ShareListing;
import org.trpg.farming.sharing.entity.ShareProduct;
import org.trpg.farming.sharing.entity.SharingOrder;
import org.trpg.farming.sharing.repository.ShareListingRepository;
import org.trpg.farming.sharing.repository.SharingOrderRepository;

/**
 * 负责共享订单的创建、履约与取消。
 */
@Service
public class SharingOrderService {

    private final SharingOrderRepository sharingOrderRepository;
    private final ShareListingRepository shareListingRepository;
    private final SharingProductService sharingProductService;
    private final SharingAccountService sharingAccountService;

    public SharingOrderService(SharingOrderRepository sharingOrderRepository,
                               ShareListingRepository shareListingRepository,
                               SharingProductService sharingProductService,
                               SharingAccountService sharingAccountService) {
        this.sharingOrderRepository = sharingOrderRepository;
        this.shareListingRepository = shareListingRepository;
        this.sharingProductService = sharingProductService;
        this.sharingAccountService = sharingAccountService;
    }

    /**
     * 在一个事务里完成下单、扣币和扣库存。
     */
    @Transactional
    public SharingOrderView createOrder(CreateOrderRequest request) {
        validateCreateOrder(request);
        ShareListing listing = sharingProductService.lockListingForOrder(request.getListingId());
        ShareProduct product = sharingProductService.getProductEntity(listing.getShareProductId());

        if (request.getBuyerUserId().equals(product.getOwnerUserId())) {
            throw new BusinessException("You cannot redeem your own listing.");
        }
        if (listing.getStock() < request.getQuantity()) {
            throw new BusinessException("Listing stock is insufficient.");
        }

        BigDecimal coinAmount = listing.getCoinPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        SharingOrder order = new SharingOrder();
        order.setOrderNo(generateOrderNo());
        order.setListingId(listing.getId());
        // 保留订单快照，避免后续编辑条目时覆盖历史订单信息。
        order.setListingTitleSnapshot(listing.getTitle());
        order.setProductNameSnapshot(product.getProductName());
        order.setBuyerUserId(request.getBuyerUserId());
        order.setSellerUserId(product.getOwnerUserId());
        order.setQuantity(request.getQuantity());
        order.setCoinAmount(coinAmount);
        order.setStatus(SharingConstants.ORDER_PAID);
        order.setShippingStatus(SharingConstants.SHIPPING_WAIT_SHIP);
        order.setCreatedAt(LocalDateTime.now());

        // 订单保存、扣币和扣库存必须在同一事务里一起成功或一起失败。
        sharingOrderRepository.save(order);
        sharingAccountService.debit(
                request.getBuyerUserId(),
                coinAmount,
                SharingConstants.BIZ_TYPE_ORDER_DEDUCT,
                order.getId(),
                "Shared redemption order deduction");
        listing.setStock(listing.getStock() - request.getQuantity());
        if (listing.getStock() == 0) {
            listing.setStatus(SharingConstants.LISTING_OFF_SHELF);
        }
        shareListingRepository.save(listing);

        return toOrderView(order);
    }

    @Transactional(readOnly = true)
    public PageResult<SharingOrderView> listOrders(Long userId, String role, String status, int page, int pageSize) {
        Page<SharingOrder> result = sharingOrderRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                if ("SELLER".equalsIgnoreCase(role)) {
                    predicates.add(cb.equal(root.get("sellerUserId"), userId));
                } else {
                    predicates.add(cb.equal(root.get("buyerUserId"), userId));
                }
            }
            if (hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, PageRequest.of(Math.max(page - 1, 0), pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<SharingOrderView> list = result.getContent().stream().map(this::toOrderView).toList();
        return new PageResult<>(list, result.getTotalElements(), page, pageSize);
    }

    @Transactional(readOnly = true)
    public SharingOrderView getOrder(Long id) {
        return toOrderView(getOrderEntity(id));
    }

    /**
     * 把已支付订单推进到已发货状态。
     */
    @Transactional
    public SharingOrderView ship(Long id, ShipOrderRequest request) {
        if (request == null || !hasText(request.getShippingNo())) {
            throw new BusinessException("shippingNo is required.");
        }
        SharingOrder order = getOrderEntity(id);
        if (!SharingConstants.ORDER_PAID.equals(order.getStatus())) {
            throw new BusinessException("Only PAID orders can be shipped.");
        }
        order.setStatus(SharingConstants.ORDER_SHIPPED);
        order.setShippingStatus(SharingConstants.SHIPPING_SHIPPED);
        order.setShippingNo(request.getShippingNo().trim());
        sharingOrderRepository.save(order);
        return toOrderView(order);
    }

    /**
     * 买家确认收货后，将订单标记为完成。
     */
    @Transactional
    public SharingOrderView complete(Long id) {
        SharingOrder order = getOrderEntity(id);
        if (!SharingConstants.ORDER_SHIPPED.equals(order.getStatus())) {
            throw new BusinessException("Only SHIPPED orders can be completed.");
        }
        order.setStatus(SharingConstants.ORDER_COMPLETED);
        order.setShippingStatus(SharingConstants.SHIPPING_RECEIVED);
        order.setCompletedAt(LocalDateTime.now());
        sharingOrderRepository.save(order);
        return toOrderView(order);
    }

    /**
     * 取消未发货订单，并回滚库存和买家共享币。
     */
    @Transactional
    public SharingOrderView cancel(Long id) {
        SharingOrder order = getOrderEntity(id);
        if (!SharingConstants.ORDER_PAID.equals(order.getStatus())) {
            throw new BusinessException("Only unshipped PAID orders can be cancelled.");
        }
        order.setStatus(SharingConstants.ORDER_CANCELLED);
        sharingOrderRepository.save(order);

        ShareListing listing = sharingProductService.getListingEntity(order.getListingId());
        listing.setStock(listing.getStock() + order.getQuantity());
        // 已过期条目即使回补库存，也不恢复成可兑换状态。
        if (!SharingConstants.LISTING_EXPIRED.equals(listing.getStatus())) {
            listing.setStatus(SharingConstants.LISTING_PUBLISHED);
        }
        shareListingRepository.save(listing);

        sharingAccountService.credit(
                order.getBuyerUserId(),
                order.getCoinAmount(),
                SharingConstants.BIZ_TYPE_ORDER_REFUND,
                order.getId(),
                "Shared redemption refund");
        return toOrderView(order);
    }

    @Transactional(readOnly = true)
    public SharingOrder getOrderEntity(Long id) {
        return sharingOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Shared order not found, id=" + id));
    }

    public SharingOrderView toOrderView(SharingOrder order) {
        SharingOrderView view = new SharingOrderView();
        view.setId(order.getId());
        view.setOrderNo(order.getOrderNo());
        view.setListingId(order.getListingId());
        view.setListingTitleSnapshot(order.getListingTitleSnapshot());
        view.setProductNameSnapshot(order.getProductNameSnapshot());
        view.setBuyerUserId(order.getBuyerUserId());
        view.setSellerUserId(order.getSellerUserId());
        view.setQuantity(order.getQuantity());
        view.setCoinAmount(order.getCoinAmount());
        view.setStatus(order.getStatus());
        view.setShippingStatus(order.getShippingStatus());
        view.setShippingNo(order.getShippingNo());
        view.setCreatedAt(order.getCreatedAt());
        view.setCompletedAt(order.getCompletedAt());
        return view;
    }

    private void validateCreateOrder(CreateOrderRequest request) {
        if (request.getListingId() == null || request.getBuyerUserId() == null) {
            throw new BusinessException("listingId and buyerUserId are required.");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BusinessException("quantity must be greater than 0.");
        }
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SO" + timePart + random;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

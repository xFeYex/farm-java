package org.trpg.farming.sharing.service;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trpg.farming.sharing.common.BusinessException;
import org.trpg.farming.sharing.common.PageResult;
import org.trpg.farming.sharing.constant.SharingConstants;
import org.trpg.farming.sharing.dto.request.CreateListingRequest;
import org.trpg.farming.sharing.dto.request.HarvestBatchConfirmRequest;
import org.trpg.farming.sharing.dto.request.UpdateListingRequest;
import org.trpg.farming.sharing.dto.response.ShareListingView;
import org.trpg.farming.sharing.dto.response.ShareProductView;
import org.trpg.farming.sharing.entity.ShareListing;
import org.trpg.farming.sharing.entity.ShareProduct;
import org.trpg.farming.sharing.repository.ShareListingRepository;
import org.trpg.farming.sharing.repository.ShareProductRepository;
import tools.jackson.databind.ObjectMapper;

/**
 * 负责共享商品与上架条目的核心业务规则。
 */
@Service
public class SharingProductService {

    private final ShareProductRepository shareProductRepository;
    private final ShareListingRepository shareListingRepository;
    private final ObjectMapper objectMapper;

    public SharingProductService(ShareProductRepository shareProductRepository,
                                 ShareListingRepository shareListingRepository,
                                 ObjectMapper objectMapper) {
        this.shareProductRepository = shareProductRepository;
        this.shareListingRepository = shareListingRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 把上游收获事件写入共享商品池。
     * 同一个批次重复投递时保持幂等。
     */
    @Transactional
    public ShareProductView confirmHarvest(HarvestBatchConfirmRequest request) {
        validateHarvestRequest(request);
        ShareProduct existed = shareProductRepository.findBySourceHarvestBatchId(request.getBatchId()).orElse(null);
        if (existed != null) {
            return toProductView(existed);
        }

        ShareProduct product = new ShareProduct();
        // 保留收获字段，供后续上架与订单溯源使用。
        product.setSourceHarvestBatchId(request.getBatchId());
        product.setOwnerUserId(request.getOwnerUserId());
        product.setProductName(request.getProductName());
        product.setOriginFacilityId(request.getFacilityId());
        product.setQuantity(request.getQuantity());
        product.setUnit(request.getUnit());
        product.setQualityLevel(request.getQualityLevel());
        product.setTraceCode(request.getTraceCode());
        product.setTraceSnapshotJson(toJson(request));
        product.setStatus(SharingConstants.PRODUCT_READY);
        product.setCreatedAt(LocalDateTime.now());

        shareProductRepository.save(product);
        return toProductView(product);
    }

    @Transactional(readOnly = true)
    public PageResult<ShareProductView> listProducts(String status, int page, int pageSize) {
        Page<ShareProduct> result = shareProductRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, PageRequest.of(Math.max(page - 1, 0), pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<ShareProductView> list = result.getContent().stream().map(this::toProductView).toList();
        return new PageResult<>(list, result.getTotalElements(), page, pageSize);
    }

    @Transactional(readOnly = true)
    public ShareProductView getProduct(Long id) {
        return toProductView(getProductEntity(id));
    }

    /**
     * 只有 READY 状态的商品才能进入兑换广场。
     */
    @Transactional
    public ShareListingView createListing(CreateListingRequest request) {
        validateCreateListing(request);
        ShareProduct product = getProductEntity(request.getShareProductId());
        if (!SharingConstants.PRODUCT_READY.equals(product.getStatus())) {
            throw new BusinessException("Only READY products can be listed.");
        }

        ShareListing listing = new ShareListing();
        listing.setShareProductId(product.getId());
        listing.setTitle(request.getTitle().trim());
        listing.setCoinPrice(request.getCoinPrice());
        listing.setStock(request.getStock());
        listing.setStatus(SharingConstants.LISTING_PUBLISHED);
        listing.setPublishedAt(LocalDateTime.now());
        listing.setExpiredAt(request.getExpiredAt());
        listing.setCreatedAt(LocalDateTime.now());

        shareListingRepository.save(listing);
        return toListingView(listing, product);
    }

    /**
     * 允许编辑标题、价格、库存和过期时间。
     * 当库存改成 0 时，条目会自动转为下架状态。
     */
    @Transactional
    public ShareListingView updateListing(Long id, UpdateListingRequest request) {
        ShareListing listing = getListingEntity(id);
        if (SharingConstants.LISTING_OFF_SHELF.equals(listing.getStatus())) {
            throw new BusinessException("Off-shelf listings cannot be edited directly.");
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            listing.setTitle(request.getTitle().trim());
        }
        if (request.getCoinPrice() != null) {
            if (request.getCoinPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("coinPrice must be greater than 0.");
            }
            listing.setCoinPrice(request.getCoinPrice());
        }
        if (request.getStock() != null) {
            if (request.getStock() < 0) {
                throw new BusinessException("stock cannot be less than 0.");
            }
            listing.setStock(request.getStock());
            if (request.getStock() == 0) {
                listing.setStatus(SharingConstants.LISTING_OFF_SHELF);
            }
        }
        if (request.getExpiredAt() != null) {
            listing.setExpiredAt(request.getExpiredAt());
        }
        shareListingRepository.save(listing);
        return toListingView(listing, getProductEntity(listing.getShareProductId()));
    }

    @Transactional(readOnly = true)
    public PageResult<ShareListingView> listListings(String status, int page, int pageSize) {
        Page<ShareListing> result = shareListingRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, PageRequest.of(Math.max(page - 1, 0), pageSize, Sort.by(Sort.Direction.DESC, "publishedAt")));

        List<ShareListingView> list = result.getContent().stream()
                .map(item -> toListingView(item, getProductEntity(item.getShareProductId())))
                .toList();
        return new PageResult<>(list, result.getTotalElements(), page, pageSize);
    }

    @Transactional(readOnly = true)
    public ShareListingView getListing(Long id) {
        ShareListing listing = getListingEntity(id);
        return toListingView(listing, getProductEntity(listing.getShareProductId()));
    }

    @Transactional
    public ShareListingView offShelf(Long id) {
        ShareListing listing = getListingEntity(id);
        listing.setStatus(SharingConstants.LISTING_OFF_SHELF);
        shareListingRepository.save(listing);
        return toListingView(listing, getProductEntity(listing.getShareProductId()));
    }

    /**
     * 下单前校验条目状态，避免对失效条目继续创建订单。
     */
    @Transactional
    public ShareListing lockListingForOrder(Long listingId) {
        ShareListing listing = getListingEntity(listingId);
        if (listing.getExpiredAt() != null && listing.getExpiredAt().isBefore(LocalDateTime.now())) {
            listing.setStatus(SharingConstants.LISTING_EXPIRED);
            shareListingRepository.save(listing);
            throw new BusinessException("The listing has already expired.");
        }
        if (!SharingConstants.LISTING_PUBLISHED.equals(listing.getStatus())) {
            throw new BusinessException("Only PUBLISHED listings can be redeemed.");
        }
        return listing;
    }

    @Transactional(readOnly = true)
    public ShareProduct getProductEntity(Long id) {
        return shareProductRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Shared product not found, id=" + id));
    }

    @Transactional(readOnly = true)
    public ShareListing getListingEntity(Long id) {
        return shareListingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Shared listing not found, id=" + id));
    }

    public ShareProductView toProductView(ShareProduct product) {
        ShareProductView view = new ShareProductView();
        view.setId(product.getId());
        view.setSourceHarvestBatchId(product.getSourceHarvestBatchId());
        view.setOwnerUserId(product.getOwnerUserId());
        view.setProductName(product.getProductName());
        view.setOriginFacilityId(product.getOriginFacilityId());
        view.setQuantity(product.getQuantity());
        view.setUnit(product.getUnit());
        view.setQualityLevel(product.getQualityLevel());
        view.setTraceCode(product.getTraceCode());
        view.setTraceSnapshotJson(product.getTraceSnapshotJson());
        view.setStatus(product.getStatus());
        view.setCreatedAt(product.getCreatedAt());
        return view;
    }

    public ShareListingView toListingView(ShareListing listing, ShareProduct product) {
        ShareListingView view = new ShareListingView();
        view.setId(listing.getId());
        view.setShareProductId(listing.getShareProductId());
        view.setTitle(listing.getTitle());
        view.setCoinPrice(listing.getCoinPrice());
        view.setStock(listing.getStock());
        view.setStatus(listing.getStatus());
        view.setPublishedAt(listing.getPublishedAt());
        view.setExpiredAt(listing.getExpiredAt());
        view.setOwnerUserId(product.getOwnerUserId());
        view.setProductName(product.getProductName());
        view.setTraceCode(product.getTraceCode());
        view.setQualityLevel(product.getQualityLevel());
        return view;
    }

    private void validateHarvestRequest(HarvestBatchConfirmRequest request) {
        if (request.getEventName() == null || !"HarvestBatchConfirmed".equals(request.getEventName().trim())) {
            throw new BusinessException("eventName must be HarvestBatchConfirmed.");
        }
        if (request.getBatchId() == null || request.getFacilityId() == null || request.getOwnerUserId() == null) {
            throw new BusinessException("batchId, facilityId and ownerUserId are required.");
        }
        if (!hasText(request.getProductName())
                || request.getQuantity() == null
                || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("productName and quantity are invalid.");
        }
        if (!hasText(request.getUnit()) || !hasText(request.getTraceCode())) {
            throw new BusinessException("unit and traceCode are required.");
        }
    }

    private void validateCreateListing(CreateListingRequest request) {
        if (request.getShareProductId() == null) {
            throw new BusinessException("shareProductId is required.");
        }
        if (!hasText(request.getTitle())) {
            throw new BusinessException("title is required.");
        }
        if (request.getCoinPrice() == null || request.getCoinPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("coinPrice must be greater than 0.");
        }
        if (request.getStock() == null || request.getStock() <= 0) {
            throw new BusinessException("stock must be greater than 0.");
        }
    }

    private String toJson(HarvestBatchConfirmRequest request) {
        return objectMapper.writeValueAsString(request);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

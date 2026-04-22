package org.trpg.farming.production.service.Imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.trpg.farming.production.common.BizException;
import org.trpg.farming.production.dto.HarvestCreateReq;
import org.trpg.farming.production.dto.HarvestCreateResponse;
import org.trpg.farming.production.dto.HarvestListReq;
import org.trpg.farming.production.dto.HarvestPageResponse;
import org.trpg.farming.production.po.ProductionHarvest;
import org.trpg.farming.production.po.Subscription;
import org.trpg.farming.production.repository.HarvestRepository;
import org.trpg.farming.production.repository.SubscriptionRepository;
import org.trpg.farming.production.service.HarvestService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HarvestServiceImpl implements HarvestService {

    private final SubscriptionRepository subscriptionRepository;
    private final HarvestRepository harvestRepository;

    @Override
    public HarvestCreateResponse createHarvest(Long resourceId, HarvestCreateReq request) {
        validateCreateRequest(request);

        Subscription subscription = validateSubscriptionAccess(resourceId, request.getUserId());
        ProductionHarvest harvest = buildHarvest(resourceId, subscription.getId(), request);

        int affectedRows = harvestRepository.insert(harvest);
        if (affectedRows != 1) {
            throw new BizException("收获记录保存失败了，请稍后再试一次");
        }

        return HarvestCreateResponse.from(harvest);
    }

    @Override
    public HarvestPageResponse listHarvests(Long resourceId, HarvestListReq request) {
        HarvestListReq normalizedRequest = normalizeListRequest(request);

        Subscription subscription = validateSubscriptionAccess(resourceId, normalizedRequest.getUserId());
        long total = harvestRepository.countByResourceIdAndSubscriptionId(resourceId, subscription.getId());
        if (total <= 0) {
            return HarvestPageResponse.of(0, List.of());
        }

        int offset = (normalizedRequest.getPage() - 1) * normalizedRequest.getPageSize();
        List<ProductionHarvest> harvests = harvestRepository.pageQueryByResourceIdAndSubscriptionId(
                resourceId,
                subscription.getId(),
                offset,
                normalizedRequest.getPageSize());
        return HarvestPageResponse.of(total, harvests);
    }

    private void validateCreateRequest(HarvestCreateReq request) {
        // 这里先把最基本的字段挡住，避免存进一条没有产品名和数量的空记录。
        if (request == null) {
            throw new BizException("请求体不能为空");
        }
        if (request.getUserId() == null) {
            throw new BizException("userId 不能为空");
        }
        if (!StringUtils.hasText(request.getProductName())) {
            throw new BizException("productName 不能为空");
        }
        if (request.getProductName().trim().length() > 100) {
            throw new BizException("productName 不要超过 100 个字");
        }
        if (!StringUtils.hasText(request.getCategory())) {
            throw new BizException("category 不能为空");
        }
        if (!StringUtils.hasText(request.getUnit())) {
            throw new BizException("unit 不能为空");
        }
        if (request.getHarvestQuantity() == null) {
            throw new BizException("harvestQuantity 不能为空");
        }
        if (request.getHarvestQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("harvestQuantity 需要大于 0");
        }
        if (request.getHarvestDate() == null) {
            throw new BizException("harvestDate 不能为空");
        }
        if (StringUtils.hasText(request.getRemark()) && request.getRemark().trim().length() > 500) {
            throw new BizException("remark 不要超过 500 个字");
        }
    }

    /**
     * 收获记录列表查询只需要当前用户身份和基础分页参数。
     */
    private HarvestListReq normalizeListRequest(HarvestListReq request) {
        if (request == null) {
            throw new BizException("请求参数不能为空");
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BizException("userId 非法");
        }

        int page = request.getPage() == null ? 1 : request.getPage();
        int pageSize = request.getPageSize() == null ? 10 : request.getPageSize();
        if (page <= 0) {
            throw new BizException("page 必须大于 0");
        }
        if (pageSize <= 0) {
            throw new BizException("pageSize 必须大于 0");
        }
        if (pageSize > 100) {
            throw new BizException("pageSize 不能超过 100");
        }

        request.setPage(page);
        request.setPageSize(pageSize);
        return request;
    }

    private Subscription validateSubscriptionAccess(Long resourceId, Long userId) {
        Subscription subscription = subscriptionRepository.findActiveByResourceId(resourceId);
        if (subscription == null) {
            throw new BizException("当前资源没有有效订阅，暂时不能登记收获记录");
        }
        if (subscription.getTenantUserId() == null || !subscription.getTenantUserId().equals(userId)) {
            throw new BizException("只有当前订阅用户本人才能登记收获记录");
        }
        return subscription;
    }

    private ProductionHarvest buildHarvest(Long resourceId, Long subscriptionId, HarvestCreateReq request) {
        // 这里把系统字段一起补齐，后面如果要做列表或详情接口，数据会更整齐一点。
        LocalDateTime now = LocalDateTime.now();

        ProductionHarvest harvest = new ProductionHarvest();
        harvest.setResourceId(resourceId);
        harvest.setSubscriptionId(subscriptionId);
        harvest.setUserId(request.getUserId());
        harvest.setProductName(request.getProductName().trim());
        harvest.setCategory(request.getCategory().trim());
        harvest.setHarvestQuantity(request.getHarvestQuantity());
        harvest.setUnit(request.getUnit().trim());
        harvest.setHarvestDate(request.getHarvestDate());
        harvest.setRemark(normalizeRemark(request.getRemark()));
        harvest.setStatus("RECORDED");
        harvest.setCreatedAt(now);
        harvest.setUpdatedAt(now);
        return harvest;
    }

    private String normalizeRemark(String remark) {
        if (!StringUtils.hasText(remark)) {
            return null;
        }
        return remark.trim();
    }
}

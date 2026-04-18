package org.trpg.farming.production.service.Imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.trpg.farming.production.common.BizException;
import org.trpg.farming.production.dto.HarvestCreateReq;
import org.trpg.farming.production.dto.HarvestCreateResponse;
import org.trpg.farming.production.entity.ProductionHarvest;
import org.trpg.farming.production.entity.Subscription;
import org.trpg.farming.production.repository.HarvestRepository;
import org.trpg.farming.production.repository.SubscriptionRepository;
import org.trpg.farming.production.service.HarvestService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

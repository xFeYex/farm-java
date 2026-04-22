package org.trpg.farming.production.service.Imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.trpg.farming.production.common.BizException;
import org.trpg.farming.production.dto.PlantingPlanCreateReq;
import org.trpg.farming.production.dto.PlantingPlanCreateResponse;
import org.trpg.farming.production.dto.PlantingPlanListReq;
import org.trpg.farming.production.dto.PlantingPlanPageResponse;
import org.trpg.farming.production.po.PlantingPlan;
import org.trpg.farming.production.po.Subscription;
import org.trpg.farming.production.repository.PlantingPlanRepository;
import org.trpg.farming.production.repository.SubscriptionRepository;
import org.trpg.farming.production.service.PlantingPlanService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantingPlanServiceImpl implements PlantingPlanService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlantingPlanRepository plantingPlanRepository;

    @Override
    public PlantingPlanCreateResponse createPlan(Long resourceId, PlantingPlanCreateReq request) {
        validateCreateRequest(request);

        Subscription subscription = validateSubscriptionAccess(resourceId, request.getUserId());
        PlantingPlan plantingPlan = buildPlantingPlan(resourceId, subscription.getId(), request);

        int affectedRows = plantingPlanRepository.insert(plantingPlan);
        if (affectedRows != 1) {
            throw new BizException("种植计划保存失败了，请稍后再试一次");
        }

        return PlantingPlanCreateResponse.from(plantingPlan);
    }

    @Override
    public PlantingPlanPageResponse listPlans(Long resourceId, PlantingPlanListReq request) {
        PlantingPlanListReq normalizedRequest = normalizeListRequest(request);

        Subscription subscription = validateSubscriptionAccess(resourceId, normalizedRequest.getUserId());
        long total = plantingPlanRepository.countByResourceIdAndSubscriptionId(resourceId, subscription.getId());
        if (total <= 0) {
            return PlantingPlanPageResponse.of(0, List.of());
        }

        int offset = (normalizedRequest.getPage() - 1) * normalizedRequest.getPageSize();
        List<PlantingPlan> plans = plantingPlanRepository.pageQueryByResourceIdAndSubscriptionId(
                resourceId,
                subscription.getId(),
                offset,
                normalizedRequest.getPageSize());
        return PlantingPlanPageResponse.of(total, plans);
    }

    private void validateCreateRequest(PlantingPlanCreateReq request) {
        // 这里做最基础的参数校验，先保证接口不会收进一条空计划。
        if (request == null) {
            throw new BizException("请求体不能为空");
        }
        if (request.getUserId() == null) {
            throw new BizException("userId 不能为空");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            throw new BizException("计划标题不能为空");
        }
        if (request.getTitle().trim().length() > 100) {
            throw new BizException("计划标题不要超过 100 个字");
        }
        if (!StringUtils.hasText(request.getPlanContent())) {
            throw new BizException("计划内容不能为空");
        }
        if (request.getPlanDate() == null) {
            throw new BizException("计划日期不能为空");
        }
    }

    /**
     * 计划列表查询只需要当前用户身份和基础分页参数。
     */
    private PlantingPlanListReq normalizeListRequest(PlantingPlanListReq request) {
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
            throw new BizException("当前资源没有有效订阅，暂时不能创建种植计划");
        }
        if (subscription.getTenantUserId() == null || !subscription.getTenantUserId().equals(userId)) {
            throw new BizException("只有当前订阅用户本人才能创建种植计划");
        }
        return subscription;
    }

    private PlantingPlan buildPlantingPlan(Long resourceId, Long subscriptionId, PlantingPlanCreateReq request) {
        // 按规格把创建时间、状态这些系统字段一起补齐，后面查数据会更整齐。
        LocalDateTime now = LocalDateTime.now();

        PlantingPlan plantingPlan = new PlantingPlan();
        plantingPlan.setResourceId(resourceId);
        plantingPlan.setSubscriptionId(subscriptionId);
        plantingPlan.setUserId(request.getUserId());
        plantingPlan.setTitle(request.getTitle().trim());
        plantingPlan.setPlanContent(request.getPlanContent().trim());
        plantingPlan.setPlanDate(request.getPlanDate());
        plantingPlan.setStatus("ACTIVE");
        plantingPlan.setCreatedAt(now);
        plantingPlan.setUpdatedAt(now);
        return plantingPlan;
    }
}

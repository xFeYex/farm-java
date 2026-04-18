package org.trpg.farming.production.service.Imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.trpg.farming.production.common.BizException;
import org.trpg.farming.production.dto.EnvironmentViewResponse;
import org.trpg.farming.production.dto.ProductionDashboardResponse;
import org.trpg.farming.production.po.CameraView;
import org.trpg.farming.production.po.EnvironmentSnapshot;
import org.trpg.farming.production.po.OrchestrationConfig;
import org.trpg.farming.production.po.Subscription;
import org.trpg.farming.production.repository.CameraViewRepository;
import org.trpg.farming.production.repository.EnvironmentSnapshotRepository;
import org.trpg.farming.production.repository.OrchestrationConfigRepository;
import org.trpg.farming.production.repository.SubscriptionRepository;
import org.trpg.farming.production.service.ProductionViewService;

@Service
@RequiredArgsConstructor
public class ProductionViewServiceImpl implements ProductionViewService {

    private final SubscriptionRepository subscriptionRepository;
    private final OrchestrationConfigRepository orchestrationConfigRepository;
    private final EnvironmentSnapshotRepository environmentSnapshotRepository;
    private final CameraViewRepository cameraViewRepository;

    @Override
    public ProductionDashboardResponse getDashboard(Long resourceId, Long userId) {

        // 1. 校验当前用户是否对该资源有有效订阅
        Subscription subscription = subscriptionRepository.findActiveByResourceId(resourceId);

        if (subscription == null || !userId.equals(subscription.getTenantUserId())) {
            return ProductionDashboardResponse.unavailable(resourceId, "当前订阅已失效，智慧生产模块不可用");
        }

        // 2. 查询编排配置
        OrchestrationConfig orchestrationConfig =
                orchestrationConfigRepository.findByResourceId(resourceId);

        // 3. 查询最新环境快照
        EnvironmentSnapshot environmentSnapshot =
                environmentSnapshotRepository.findLatestByResourceId(resourceId);

        // 4. 查询摄像头信息
        CameraView cameraView =
                cameraViewRepository.findByResourceId(resourceId);

        // 5. 组装 dashboard 返回
        return ProductionDashboardResponse.available(
                resourceId,
                orchestrationConfig,
                environmentSnapshot,
                cameraView
        );
    }

    @Override
    public EnvironmentViewResponse getEnvironmentView(Long resourceId, Long userId) {
        // 第三个流程是一个明确查询动作，这里校验失败就直接提示不能访问。
        validateSubscriptionAccess(resourceId, userId);

        EnvironmentSnapshot latestSnapshot =
                environmentSnapshotRepository.findLatestByResourceId(resourceId);

        if (latestSnapshot == null) {
            return EnvironmentViewResponse.empty(resourceId, "当前还没有环境快照数据");
        }

        return EnvironmentViewResponse.fromSnapshot(resourceId, latestSnapshot);
    }

    /**
     * 单独接口复用的访问校验逻辑。
     * 只有订阅有效并且当前用户就是订阅人才允许查看监测数据。
     */
    private void validateSubscriptionAccess(Long resourceId, Long userId) {
        Subscription subscription = subscriptionRepository.findActiveByResourceId(resourceId);
        if (subscription == null) {
            throw new BizException("当前订阅无效，暂时不能查看环境监测数据");
        }
        if (subscription.getTenantUserId() == null || !subscription.getTenantUserId().equals(userId)) {
            throw new BizException("只有当前订阅用户本人才能查看环境监测数据");
        }
    }
}

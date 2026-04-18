package org.trpg.farming.lease.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.trpg.farming.lease.common.BizException;
import org.trpg.farming.lease.dao.ResourceRepository;
import org.trpg.farming.lease.dao.LeaseSubscriptionRepository;
import org.trpg.farming.lease.dto.MySubscriptionListItemResponse;
import org.trpg.farming.lease.dto.MySubscriptionListReq;
import org.trpg.farming.lease.dto.MySubscriptionPageResponse;
import org.trpg.farming.lease.dto.SubscriptionCreateReq;
import org.trpg.farming.lease.dto.SubscriptionCreateResponse;
import org.trpg.farming.lease.dto.SubscriptionCancelReq;
import org.trpg.farming.lease.dto.SubscriptionCancelResponse;
import org.trpg.farming.lease.dto.SubscriptionRenewReq;
import org.trpg.farming.lease.dto.SubscriptionRenewResponse;
import org.trpg.farming.lease.po.Resource;
import org.trpg.farming.lease.po.Subscription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final String RESOURCE_STATUS_ON_SHELF = "ON_SHELF";
    private static final String SUBSCRIPTION_STATUS_ACTIVE = "ACTIVE";
    private static final String SUBSCRIPTION_STATUS_CANCELLED = "CANCELLED";
    private static final String SUBSCRIPTION_STATUS_EXPIRED = "EXPIRED";

    private final ResourceRepository resourceRepository;
    private final LeaseSubscriptionRepository subscriptionRepository;

    /**
     * 创建订阅是租赁域最关键的一条链路。
     * 当前严格按文档里的最小规则校验：资源存在、已上架、不能租自己的资源、满足最短租期、不能有其他有效订阅。
     */
    public SubscriptionCreateResponse createSubscription(SubscriptionCreateReq request) {
        validateCreateRequest(request);

        Resource resource = loadResource(request.getResourceId());
        validateResourceForSubscription(resource, request);
        ensureNoActiveSubscription(resource.getId());

        Subscription subscription = buildSubscription(request, resource);
        int affectedRows = subscriptionRepository.insert(subscription);
        if (affectedRows != 1) {
            throw new BizException("订阅资源失败了，请稍后再试");
        }

        return SubscriptionCreateResponse.from(subscription);
    }

    /**
     * 续订后仍然是原订阅记录，只延长 endDate，不新建第二条订阅。
     */
    public SubscriptionRenewResponse renewSubscription(Long id, SubscriptionRenewReq request) {
        validateRenewRequest(id, request);

        Subscription subscription = loadSubscription(id);
        validateRenewPermission(subscription, request);

        LocalDate renewedEndDate = subscription.getEndDate().plusMonths(request.getLeaseMonths());
        subscription.setEndDate(renewedEndDate);
        subscription.setUpdatedAt(LocalDateTime.now());

        int affectedRows = subscriptionRepository.updateById(subscription);
        if (affectedRows != 1) {
            throw new BizException("续订失败了，请稍后再试");
        }
        return SubscriptionRenewResponse.from(subscription);
    }

    /**
     * 退订不会删除记录，而是把当前订阅状态改成 CANCELLED，并记录取消时间。
     */
    public SubscriptionCancelResponse cancelSubscription(Long id, SubscriptionCancelReq request) {
        validateCancelRequest(id, request);

        Subscription subscription = loadSubscription(id);
        validateCancelPermission(subscription, request);

        subscription.setStatus(SUBSCRIPTION_STATUS_CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());

        int affectedRows = subscriptionRepository.updateById(subscription);
        if (affectedRows != 1) {
            throw new BizException("退订失败了，请稍后再试");
        }
        return SubscriptionCancelResponse.from(subscription);
    }

    /**
     * 到期自动失效是后台任务调用的能力。
     * 这里只做最小批量更新：把 endDate 早于今天、且状态仍是 ACTIVE 的订阅改成 EXPIRED。
     */
    public int expireSubscriptions() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        return subscriptionRepository.expireActiveSubscriptions(today, now);
    }

    /**
     * “我的订阅”列表按租赁人维度分页返回，并附带资源标题，方便页面直接展示。
     */
    public MySubscriptionPageResponse listMySubscriptions(MySubscriptionListReq request) {
        MySubscriptionListReq normalizedRequest = normalizeMySubscriptionListRequest(request);

        long total = subscriptionRepository.countByTenantUserId(normalizedRequest.getUserId());
        if (total <= 0) {
            return MySubscriptionPageResponse.of(0, List.of());
        }

        int offset = (normalizedRequest.getPage() - 1) * normalizedRequest.getPageSize();
        List<MySubscriptionListItemResponse> list = subscriptionRepository.pageQueryByTenantUserId(
                normalizedRequest.getUserId(),
                offset,
                normalizedRequest.getPageSize());
        return MySubscriptionPageResponse.of(total, list);
    }

    /**
     * 先把请求里的基础空值和边界卡住，避免把明显非法的数据带进业务判断。
     */
    private void validateCreateRequest(SubscriptionCreateReq request) {
        if (request == null) {
            throw new BizException("请求体不能为空");
        }
        if (request.getResourceId() == null || request.getResourceId() <= 0) {
            throw new BizException("resourceId 非法");
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BizException("userId 非法");
        }
        if (request.getStartDate() == null) {
            throw new BizException("startDate 不能为空");
        }
        if (request.getLeaseMonths() == null || request.getLeaseMonths() <= 0) {
            throw new BizException("leaseMonths 必须大于 0");
        }
    }

    /**
     * “我的订阅”查询阶段只校验租赁人 ID 和基础分页参数。
     */
    private MySubscriptionListReq normalizeMySubscriptionListRequest(MySubscriptionListReq request) {
        if (request == null) {
            throw new BizException("查询参数不能为空");
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

    /**
     * 续订阶段的请求很轻，只需要订阅 ID、当前操作人和延长月数。
     */
    private void validateRenewRequest(Long id, SubscriptionRenewReq request) {
        if (id == null || id <= 0) {
            throw new BizException("订阅 ID 非法");
        }
        if (request == null) {
            throw new BizException("请求体不能为空");
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BizException("userId 非法");
        }
        if (request.getLeaseMonths() == null || request.getLeaseMonths() <= 0) {
            throw new BizException("leaseMonths 必须大于 0");
        }
    }

    /**
     * 退订请求只需要订阅 ID 和当前操作人，用来做最小权限校验。
     */
    private void validateCancelRequest(Long id, SubscriptionCancelReq request) {
        if (id == null || id <= 0) {
            throw new BizException("订阅 ID 非法");
        }
        if (request == null) {
            throw new BizException("请求体不能为空");
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BizException("userId 非法");
        }
    }

    /**
     * 订阅前必须先确认资源真实存在。
     */
    private Resource loadResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId);
        if (resource == null) {
            throw new BizException("资源不存在");
        }
        return resource;
    }

    /**
     * 续订前先把原订阅取出来，后续权限和状态校验都基于这条记录。
     */
    private Subscription loadSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id);
        if (subscription == null) {
            throw new BizException("订阅不存在");
        }
        return subscription;
    }

    /**
     * 这里集中处理和资源本身有关的订阅规则。
     */
    private void validateResourceForSubscription(Resource resource, SubscriptionCreateReq request) {
        if (!RESOURCE_STATUS_ON_SHELF.equals(resource.getStatus())) {
            throw new BizException("只有 ON_SHELF 的资源才能被订阅");
        }
        if (resource.getOwnerUserId() != null && resource.getOwnerUserId().equals(request.getUserId())) {
            throw new BizException("用户不能订阅自己发布的资源");
        }
        if (resource.getMinLeaseMonths() != null && request.getLeaseMonths() < resource.getMinLeaseMonths()) {
            throw new BizException("租期不能小于资源要求的最短租期");
        }
    }

    /**
     * 只有订阅人本人，且订阅状态仍是 ACTIVE，才允许继续往后续期。
     */
    private void validateRenewPermission(Subscription subscription, SubscriptionRenewReq request) {
        if (subscription.getTenantUserId() == null
                || !subscription.getTenantUserId().equals(request.getUserId())) {
            throw new BizException("只有订阅人本人才能续订");
        }
        if (!SUBSCRIPTION_STATUS_ACTIVE.equals(subscription.getStatus())) {
            throw new BizException("只有 ACTIVE 订阅能续订");
        }
        if (subscription.getEndDate() == null) {
            throw new BizException("订阅结束日期缺失，暂时不能续订");
        }
    }

    /**
     * 退订规则和续订类似，也要求本人操作且订阅当前仍然是 ACTIVE。
     */
    private void validateCancelPermission(Subscription subscription, SubscriptionCancelReq request) {
        if (subscription.getTenantUserId() == null
                || !subscription.getTenantUserId().equals(request.getUserId())) {
            throw new BizException("只有订阅人本人才能退订");
        }
        if (!SUBSCRIPTION_STATUS_ACTIVE.equals(subscription.getStatus())) {
            throw new BizException("只有 ACTIVE 订阅能退订");
        }
    }

    /**
     * MVP 阶段按“是否已有 ACTIVE 订阅”判断资源能否再次被租。
     */
    private void ensureNoActiveSubscription(Long resourceId) {
        Subscription activeSubscription = subscriptionRepository.findActiveByResourceId(resourceId);
        if (activeSubscription != null) {
            throw new BizException("该资源当前已有有效订阅，暂时不能重复订阅");
        }
    }

    /**
     * 结束日期按“开始日期 + 租期月数 - 1 天”计算，和文档示例保持一致。
     */
    private Subscription buildSubscription(SubscriptionCreateReq request, Resource resource) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate endDate = request.getStartDate().plusMonths(request.getLeaseMonths()).minusDays(1);

        Subscription subscription = new Subscription();
        subscription.setResourceId(resource.getId());
        subscription.setTenantUserId(request.getUserId());
        subscription.setStartDate(request.getStartDate());
        subscription.setEndDate(endDate);
        subscription.setStatus(SUBSCRIPTION_STATUS_ACTIVE);
        subscription.setCancelledAt(null);
        subscription.setCreatedAt(now);
        subscription.setUpdatedAt(now);
        return subscription;
    }
}

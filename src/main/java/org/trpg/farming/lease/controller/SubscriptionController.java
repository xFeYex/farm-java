package org.trpg.farming.lease.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.lease.common.BizException;
import org.trpg.farming.lease.common.Result;
import org.trpg.farming.lease.dto.SubscriptionCancelReq;
import org.trpg.farming.lease.dto.SubscriptionCancelResponse;
import org.trpg.farming.lease.dto.SubscriptionCreateReq;
import org.trpg.farming.lease.dto.SubscriptionCreateResponse;
import org.trpg.farming.lease.dto.SubscriptionRenewReq;
import org.trpg.farming.lease.dto.SubscriptionRenewResponse;
import org.trpg.farming.lease.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * 订阅资源。
     * 当前先只打通“创建订阅”这一条主流程，续订和退订后续再分步骤补齐。
     */
    @PostMapping
    public Result<SubscriptionCreateResponse> create(@RequestBody SubscriptionCreateReq request) {
        try {
            log.info("创建订阅, resourceId={}, userId={}", request.getResourceId(), request.getUserId());
            SubscriptionCreateResponse response = subscriptionService.createSubscription(request);
            log.info("创建订阅成功, subscriptionId={}, resourceId={}", response.getId(), response.getResourceId());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("创建订阅失败, resourceId={}, userId={}, message={}",
                    request.getResourceId(), request.getUserId(), ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("创建订阅异常, resourceId={}, userId={}",
                    request.getResourceId(), request.getUserId(), ex);
            return Result.fail(500, "创建订阅失败");
        }
    }

    /**
     * 续订资源。
     * 当前按“原订阅续期”的规则处理，不会新建第二条订阅记录。
     */
    @PostMapping("/{id}/renew")
    public Result<SubscriptionRenewResponse> renew(
            @PathVariable Long id,
            @RequestBody SubscriptionRenewReq request) {
        try {
            log.info("续订订阅, subscriptionId={}, userId={}, leaseMonths={}",
                    id, request.getUserId(), request.getLeaseMonths());
            SubscriptionRenewResponse response = subscriptionService.renewSubscription(id, request);
            log.info("续订订阅成功, subscriptionId={}, endDate={}", response.getId(), response.getEndDate());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("续订订阅失败, subscriptionId={}, userId={}, message={}",
                    id, request.getUserId(), ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("续订订阅异常, subscriptionId={}, userId={}", id, request.getUserId(), ex);
            return Result.fail(500, "续订订阅失败");
        }
    }

    /**
     * 退订资源。
     * 当前按软关闭处理，只更新状态和取消时间，不删除历史记录。
     */
    @PostMapping("/{id}/cancel")
    public Result<SubscriptionCancelResponse> cancel(
            @PathVariable Long id,
            @RequestBody SubscriptionCancelReq request) {
        try {
            log.info("退订订阅, subscriptionId={}, userId={}", id, request.getUserId());
            SubscriptionCancelResponse response = subscriptionService.cancelSubscription(id, request);
            log.info("退订订阅成功, subscriptionId={}, status={}", response.getId(), response.getStatus());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("退订订阅失败, subscriptionId={}, userId={}, message={}",
                    id, request.getUserId(), ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("退订订阅异常, subscriptionId={}, userId={}", id, request.getUserId(), ex);
            return Result.fail(500, "退订订阅失败");
        }
    }
}

package org.trpg.farming.lease.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.lease.common.BizException;
import org.trpg.farming.lease.common.Result;
import org.trpg.farming.lease.dto.MyResourceListReq;
import org.trpg.farming.lease.dto.MySubscriptionListReq;
import org.trpg.farming.lease.dto.MySubscriptionPageResponse;
import org.trpg.farming.lease.dto.ResourcePageResponse;
import org.trpg.farming.lease.service.ResourceService;
import org.trpg.farming.lease.service.SubscriptionService;

@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
@Slf4j
public class MyLeaseController {

    private final ResourceService resourceService;
    private final SubscriptionService subscriptionService;

    /**
     * 我的发布列表。
     * 当前按发布者 ID + 分页查询资源，不额外叠加类型筛选，先满足页面主流程。
     */
    @GetMapping("/resources")
    public Result<ResourcePageResponse> listMyResources(MyResourceListReq request) {
        try {
            log.info("查询我的发布列表, userId={}, page={}, pageSize={}",
                    request.getUserId(), request.getPage(), request.getPageSize());
            ResourcePageResponse response = resourceService.listMyResources(request);
            log.info("查询我的发布列表成功, userId={}, total={}", request.getUserId(), response.getTotal());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("查询我的发布列表失败, userId={}, message={}", request.getUserId(), ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("查询我的发布列表异常, userId={}", request.getUserId(), ex);
            return Result.fail(500, "查询我的发布列表失败");
        }
    }

    /**
     * 我的订阅列表。
     * 当前按租赁人 ID + 分页查询订阅，并带出资源标题和资源状态。
     */
    @GetMapping("/subscriptions")
    public Result<MySubscriptionPageResponse> listMySubscriptions(MySubscriptionListReq request) {
        try {
            log.info("查询我的订阅列表, userId={}, page={}, pageSize={}",
                    request.getUserId(), request.getPage(), request.getPageSize());
            MySubscriptionPageResponse response = subscriptionService.listMySubscriptions(request);
            log.info("查询我的订阅列表成功, userId={}, total={}", request.getUserId(), response.getTotal());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("查询我的订阅列表失败, userId={}, message={}", request.getUserId(), ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("查询我的订阅列表异常, userId={}", request.getUserId(), ex);
            return Result.fail(500, "查询我的订阅列表失败");
        }
    }
}

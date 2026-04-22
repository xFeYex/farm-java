package org.trpg.farming.sharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.sharing.common.ApiResponse;
import org.trpg.farming.sharing.dto.request.CreateOrderRequest;
import org.trpg.farming.sharing.dto.request.ShipOrderRequest;
import org.trpg.farming.sharing.service.SharingOrderService;

/**
 * 共享订单创建与履约相关的 HTTP 接口。
 */
@RestController
@RequestMapping("/api/sharing/orders")
public class SharingOrderController {

    private final SharingOrderService sharingOrderService;

    public SharingOrderController(SharingOrderService sharingOrderService) {
        this.sharingOrderService = sharingOrderService;
    }

    /**
     * 创建订单，库存和共享币的变动交给服务层统一处理。
     */
    @PostMapping
    public ApiResponse<?> createOrder(@RequestBody CreateOrderRequest request) {
        return ApiResponse.success("Order created successfully", sharingOrderService.createOrder(request));
    }

    @GetMapping
    public ApiResponse<?> listOrders(@RequestParam(required = false) Long userId,
                                     @RequestParam(defaultValue = "BUYER") String role,
                                     @RequestParam(required = false) String status,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(sharingOrderService.listOrders(userId, role, status, page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getOrder(@PathVariable Long id) {
        return ApiResponse.success(sharingOrderService.getOrder(id));
    }

    /**
     * 将已支付订单推进到已发货状态。
     */
    @PostMapping("/{id}/ship")
    public ApiResponse<?> ship(@PathVariable Long id, @RequestBody ShipOrderRequest request) {
        return ApiResponse.success("Order shipped successfully", sharingOrderService.ship(id, request));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<?> complete(@PathVariable Long id) {
        return ApiResponse.success("Order completed successfully", sharingOrderService.complete(id));
    }

    /**
     * 取消未发货订单，并触发服务层里的回滚流程。
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse<?> cancel(@PathVariable Long id) {
        return ApiResponse.success("Order cancelled successfully", sharingOrderService.cancel(id));
    }
}

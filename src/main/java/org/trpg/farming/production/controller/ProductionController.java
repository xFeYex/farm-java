package org.trpg.farming.production.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.production.common.Result;
import org.trpg.farming.production.dto.EnvironmentViewResponse;
import org.trpg.farming.production.dto.ProductionDashboardResponse;
import org.trpg.farming.production.service.ProductionViewService;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionViewService productionViewService;

    /**
     * 智慧生产主页。
     * 示例：
     * GET /api/production/dashboard/101?userId=2001
     */
    @GetMapping("/dashboard/{resourceId}")
    public Result<ProductionDashboardResponse> getDashboard(
            @PathVariable Long resourceId,
            @RequestParam Long userId) {

        ProductionDashboardResponse response = productionViewService.getDashboard(resourceId, userId);
        return Result.success(response);
    }

    /**
     * 查看最新环境监测数据。
     * 这里只查最新一条快照，比较符合“状态监测”流程的核心目标。
     * 示例：
     * GET /api/production/environment/101?userId=2001
     */
    @GetMapping("/environment/{resourceId}")
    public Result<EnvironmentViewResponse> getEnvironmentView(
            @PathVariable Long resourceId,
            @RequestParam Long userId) {

        EnvironmentViewResponse response = productionViewService.getEnvironmentView(resourceId, userId);
        return Result.success(response);
    }
}

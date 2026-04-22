package org.trpg.farming.production.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.production.common.BizException;
import org.trpg.farming.production.common.Result;
import org.trpg.farming.production.dto.EnvironmentViewResponse;
import org.trpg.farming.production.dto.ProductionDashboardResponse;
import org.trpg.farming.production.service.ProductionViewService;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
@Slf4j
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

        log.info("开始查询智慧生产主页数据, resourceId={}, userId={}", resourceId, userId);
        try {
            ProductionDashboardResponse response = productionViewService.getDashboard(resourceId, userId);
            log.info("查询智慧生产主页数据成功, resourceId={}, userId={}, available={}",
                    resourceId, userId, response.getAvailable());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("查询智慧生产主页数据失败, resourceId={}, userId={}, message={}",
                    resourceId, userId, ex.getMessage(), ex);
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("查询智慧生产主页数据发生系统异常, resourceId={}, userId={}",
                    resourceId, userId, ex);
            return Result.fail(500, "查询智慧生产主页数据失败: " + ex.getMessage());
        }
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

        log.info("开始查询环境监测数据, resourceId={}, userId={}", resourceId, userId);
        try {
            EnvironmentViewResponse response = productionViewService.getEnvironmentView(resourceId, userId);
            log.info("查询环境监测数据成功, resourceId={}, userId={}, snapshotTime={}",
                    resourceId, userId, response.getSnapshotTime());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("查询环境监测数据失败, resourceId={}, userId={}, message={}",
                    resourceId, userId, ex.getMessage(), ex);
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("查询环境监测数据发生系统异常, resourceId={}, userId={}",
                    resourceId, userId, ex);
            return Result.fail(500, "查询环境监测数据失败: " + ex.getMessage());
        }
    }
}

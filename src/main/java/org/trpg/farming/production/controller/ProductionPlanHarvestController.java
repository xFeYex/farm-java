package org.trpg.farming.production.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.production.common.BizException;
import org.trpg.farming.production.dto.HarvestCreateReq;
import org.trpg.farming.production.dto.HarvestCreateResponse;
import org.trpg.farming.production.dto.HarvestListReq;
import org.trpg.farming.production.dto.HarvestPageResponse;
import org.trpg.farming.production.common.Result;
import org.trpg.farming.production.dto.PlantingPlanCreateReq;
import org.trpg.farming.production.dto.PlantingPlanCreateResponse;
import org.trpg.farming.production.dto.PlantingPlanListReq;
import org.trpg.farming.production.dto.PlantingPlanPageResponse;
import org.trpg.farming.production.service.HarvestService;
import org.trpg.farming.production.service.PlantingPlanService;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
@Slf4j
public class ProductionPlanHarvestController {

    private final PlantingPlanService plantingPlanService;
    private final HarvestService harvestService;

    /**
     * 新增种植计划。
     * 示例：
     * POST /api/production/plan/101
     */
    /**
     * 查询种植计划列表。
     * 按当前资源和当前有效订阅范围返回计划数据，便于前端页面直接落地真实列表。
     */
    @GetMapping("/plan/{resourceId}")
    public Result<PlantingPlanPageResponse> listPlans(
            @PathVariable Long resourceId,
            PlantingPlanListReq request) {

        log.info("寮€濮嬫煡璇㈢妞嶈鍒掑垪琛? resourceId={}, userId={}, page={}, pageSize={}",
                resourceId, request.getUserId(), request.getPage(), request.getPageSize());
        try {
            PlantingPlanPageResponse response = plantingPlanService.listPlans(resourceId, request);
            log.info("鏌ヨ绉嶆璁″垝鍒楄〃鎴愬姛, resourceId={}, total={}", resourceId, response.getTotal());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("鏌ヨ绉嶆璁″垝鍒楄〃澶辫触, resourceId={}, message={}", resourceId, ex.getMessage(), ex);
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("鏌ヨ绉嶆璁″垝鍒楄〃鍙戠敓绯荤粺寮傚父, resourceId={}", resourceId, ex);
            return Result.fail(500, "鏌ヨ绉嶆璁″垝鍒楄〃澶辫触: " + ex.getMessage());
        }
    }

    @PostMapping("/plan/{resourceId}")
    public Result<PlantingPlanCreateResponse> createPlan(
            @PathVariable Long resourceId,
            @RequestBody PlantingPlanCreateReq request) {

        log.info("开始新增种植计划, resourceId={}, request={}", resourceId, request);
        try {
            PlantingPlanCreateResponse response = plantingPlanService.createPlan(resourceId, request);
            log.info("新增种植计划成功, resourceId={}, planId={}, status={}",
                    resourceId, response.getPlanId(), response.getStatus());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("新增种植计划失败, resourceId={}, request={}, message={}",
                    resourceId, request, ex.getMessage(), ex);
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("新增种植计划发生系统异常, resourceId={}, request={}",
                    resourceId, request, ex);
            return Result.fail(500, "新增种植计划失败: " + ex.getMessage());
        }
    }

    /**
     * 新增收获记录。
     * 这里先只做“登记入库”，不给共享模块加耦合逻辑。
     * 示例：
     * POST /api/production/harvest/101
     */
    /**
     * 查询收获记录列表。
     * 按当前资源和当前有效订阅范围返回收获记录，避免页面再用 localStorage 临时兜底。
     */
    @GetMapping("/harvest/{resourceId}")
    public Result<HarvestPageResponse> listHarvests(
            @PathVariable Long resourceId,
            HarvestListReq request) {

        log.info("寮€濮嬫煡璇㈡敹鑾疯褰曞垪琛? resourceId={}, userId={}, page={}, pageSize={}",
                resourceId, request.getUserId(), request.getPage(), request.getPageSize());
        try {
            HarvestPageResponse response = harvestService.listHarvests(resourceId, request);
            log.info("鏌ヨ鏀惰幏璁板綍鍒楄〃鎴愬姛, resourceId={}, total={}", resourceId, response.getTotal());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("鏌ヨ鏀惰幏璁板綍鍒楄〃澶辫触, resourceId={}, message={}", resourceId, ex.getMessage(), ex);
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("鏌ヨ鏀惰幏璁板綍鍒楄〃鍙戠敓绯荤粺寮傚父, resourceId={}", resourceId, ex);
            return Result.fail(500, "鏌ヨ鏀惰幏璁板綍鍒楄〃澶辫触: " + ex.getMessage());
        }
    }

    @PostMapping("/harvest/{resourceId}")
    public Result<HarvestCreateResponse> createHarvest(
            @PathVariable Long resourceId,
            @RequestBody HarvestCreateReq request) {

        log.info("开始新增收获记录, resourceId={}, request={}", resourceId, request);
        try {
            HarvestCreateResponse response = harvestService.createHarvest(resourceId, request);
            log.info("新增收获记录成功, resourceId={}, harvestId={}, status={}",
                    resourceId, response.getHarvestId(), response.getStatus());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("新增收获记录失败, resourceId={}, request={}, message={}",
                    resourceId, request, ex.getMessage(), ex);
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("新增收获记录发生系统异常, resourceId={}, request={}",
                    resourceId, request, ex);
            return Result.fail(500, "新增收获记录失败: " + ex.getMessage());
        }
    }
}

package org.trpg.farming.production.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.production.dto.HarvestCreateReq;
import org.trpg.farming.production.dto.HarvestCreateResponse;
import org.trpg.farming.production.common.Result;
import org.trpg.farming.production.dto.PlantingPlanCreateReq;
import org.trpg.farming.production.dto.PlantingPlanCreateResponse;
import org.trpg.farming.production.service.HarvestService;
import org.trpg.farming.production.service.PlantingPlanService;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionPlanHarvestController {

    private final PlantingPlanService plantingPlanService;
    private final HarvestService harvestService;

    /**
     * 新增种植计划。
     * 示例：
     * POST /api/production/plan/101
     */
    @PostMapping("/plan/{resourceId}")
    public Result<PlantingPlanCreateResponse> createPlan(
            @PathVariable Long resourceId,
            @RequestBody PlantingPlanCreateReq request) {

        PlantingPlanCreateResponse response = plantingPlanService.createPlan(resourceId, request);
        return Result.success(response);
    }

    /**
     * 新增收获记录。
     * 这里先只做“登记入库”，不给共享模块加耦合逻辑。
     * 示例：
     * POST /api/production/harvest/101
     */
    @PostMapping("/harvest/{resourceId}")
    public Result<HarvestCreateResponse> createHarvest(
            @PathVariable Long resourceId,
            @RequestBody HarvestCreateReq request) {

        HarvestCreateResponse response = harvestService.createHarvest(resourceId, request);
        return Result.success(response);
    }
}

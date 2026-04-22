package org.trpg.farming.production.service;

import org.trpg.farming.production.dto.PlantingPlanCreateReq;
import org.trpg.farming.production.dto.PlantingPlanCreateResponse;
import org.trpg.farming.production.dto.PlantingPlanListReq;
import org.trpg.farming.production.dto.PlantingPlanPageResponse;

public interface PlantingPlanService {

    /**
     * 制定种植计划的第一条主流程。
     */
    PlantingPlanCreateResponse createPlan(Long resourceId, PlantingPlanCreateReq request);

    /**
     * 查询当前资源下、当前有效订阅范围内的种植计划列表。
     */
    PlantingPlanPageResponse listPlans(Long resourceId, PlantingPlanListReq request);
}

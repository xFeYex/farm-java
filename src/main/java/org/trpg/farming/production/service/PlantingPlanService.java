package org.trpg.farming.production.service;

import org.trpg.farming.production.dto.PlantingPlanCreateReq;
import org.trpg.farming.production.dto.PlantingPlanCreateResponse;

public interface PlantingPlanService {

    /**
     * 制定种植计划的第一条主流程。
     */
    PlantingPlanCreateResponse createPlan(Long resourceId, PlantingPlanCreateReq request);
}

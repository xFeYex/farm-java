package org.trpg.farming.production.dto;

import lombok.Data;
import org.trpg.farming.production.po.PlantingPlan;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlantingPlanPageResponse {

    private long total;
    private List<PlantingPlanListItemResponse> list;

    /**
     * 种植计划列表统一返回 total + list，方便前端直接做分页。
     */
    public static PlantingPlanPageResponse of(long total, List<PlantingPlan> plans) {
        PlantingPlanPageResponse response = new PlantingPlanPageResponse();
        response.setTotal(total);

        List<PlantingPlanListItemResponse> items = new ArrayList<>(plans.size());
        for (PlantingPlan plan : plans) {
            items.add(PlantingPlanListItemResponse.from(plan));
        }
        response.setList(items);
        return response;
    }
}

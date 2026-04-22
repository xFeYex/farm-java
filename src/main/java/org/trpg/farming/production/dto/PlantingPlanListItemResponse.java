package org.trpg.farming.production.dto;

import lombok.Data;
import org.trpg.farming.production.po.PlantingPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlantingPlanListItemResponse {

    private Long planId;
    private Long resourceId;
    private String title;
    private String planContent;
    private LocalDate planDate;
    private String status;
    private LocalDateTime createdAt;

    /**
     * 列表项只返回页面展示所需字段，不暴露内部订阅关联字段。
     */
    public static PlantingPlanListItemResponse from(PlantingPlan plan) {
        PlantingPlanListItemResponse response = new PlantingPlanListItemResponse();
        response.setPlanId(plan.getId());
        response.setResourceId(plan.getResourceId());
        response.setTitle(plan.getTitle());
        response.setPlanContent(plan.getPlanContent());
        response.setPlanDate(plan.getPlanDate());
        response.setStatus(plan.getStatus());
        response.setCreatedAt(plan.getCreatedAt());
        return response;
    }
}

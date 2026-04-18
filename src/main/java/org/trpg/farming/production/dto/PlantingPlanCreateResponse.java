package org.trpg.farming.production.dto;

import lombok.Data;
import org.trpg.farming.production.entity.PlantingPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlantingPlanCreateResponse {

    private Long planId;
    private Long resourceId;
    private String title;
    private String planContent;
    private LocalDate planDate;
    private String status;
    private LocalDateTime createdAt;

    public static PlantingPlanCreateResponse from(PlantingPlan plan) {
        PlantingPlanCreateResponse response = new PlantingPlanCreateResponse();
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

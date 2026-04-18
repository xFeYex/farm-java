package org.trpg.farming.production.dto;

import lombok.Data;
import org.trpg.farming.production.entity.ProductionHarvest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HarvestCreateResponse {

    private Long harvestId;
    private Long resourceId;
    private String productName;
    private String category;
    private BigDecimal harvestQuantity;
    private String unit;
    private LocalDate harvestDate;
    private String remark;
    private String status;
    private LocalDateTime createdAt;

    public static HarvestCreateResponse from(ProductionHarvest harvest) {
        HarvestCreateResponse response = new HarvestCreateResponse();
        response.setHarvestId(harvest.getId());
        response.setResourceId(harvest.getResourceId());
        response.setProductName(harvest.getProductName());
        response.setCategory(harvest.getCategory());
        response.setHarvestQuantity(harvest.getHarvestQuantity());
        response.setUnit(harvest.getUnit());
        response.setHarvestDate(harvest.getHarvestDate());
        response.setRemark(harvest.getRemark());
        response.setStatus(harvest.getStatus());
        response.setCreatedAt(harvest.getCreatedAt());
        return response;
    }
}

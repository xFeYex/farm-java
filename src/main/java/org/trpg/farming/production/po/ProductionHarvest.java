package org.trpg.farming.production.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionHarvest {

    private Long id;
    private Long resourceId;
    private Long subscriptionId;
    private Long userId;
    private String productName;
    private String category;
    private BigDecimal harvestQuantity;
    private String unit;
    private LocalDate harvestDate;
    private String remark;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

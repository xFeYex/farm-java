package org.trpg.farming.production.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlantingPlan {

    private Long id;
    private Long resourceId;
    private Long subscriptionId;
    private Long userId;
    private String title;
    private String planContent;
    private LocalDate planDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

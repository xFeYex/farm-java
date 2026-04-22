package org.trpg.farming.production.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Subscription {
    private Long id;
    private Long resourceId;
    private Long tenantUserId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

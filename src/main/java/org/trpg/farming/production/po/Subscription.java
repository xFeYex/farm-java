package org.trpg.farming.production.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Subscription {
    private Long id;
    private Long resourceId;
    private Long tenantUserId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
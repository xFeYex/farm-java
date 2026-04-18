package org.trpg.farming.production.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrchestrationConfig {
    private Long id;
    private Long resourceId;
    private Long subscriptionId;
    private String orchestrationType;
    private String name;
    private String scheduleMode;
    private Integer timesPerDay;
    private Integer durationMinutes;
    private String startTimesJson;
    private String configJson;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
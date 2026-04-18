package org.trpg.farming.production.po;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EnvironmentSnapshot {
    private Long id;
    private Long resourceId;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal lightLux;
    private BigDecimal soilMoisture;
    private BigDecimal airQuality;
    private LocalDateTime snapshotTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
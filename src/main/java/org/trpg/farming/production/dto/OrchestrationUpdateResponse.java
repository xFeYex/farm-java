package org.trpg.farming.production.dto;

import lombok.Data;
import org.trpg.farming.production.entity.OrchestrationConfig;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrchestrationUpdateResponse {

    private Long configId;
    private Long resourceId;
    private Integer timesPerDay;
    private Integer durationMinutes;
    private List<String> startTimes;
    private Boolean sprinklerEnabled;
    private Integer targetHumidity;
    private String status;
    private LocalDateTime updatedAt;

    public static OrchestrationUpdateResponse of(
            OrchestrationConfig config,
            List<String> startTimes,
            Boolean sprinklerEnabled,
            Integer targetHumidity) {

        OrchestrationUpdateResponse response = new OrchestrationUpdateResponse();
        response.setConfigId(config.getId());
        response.setResourceId(config.getResourceId());
        response.setTimesPerDay(config.getTimesPerDay());
        response.setDurationMinutes(config.getDurationMinutes());
        response.setStartTimes(startTimes);
        response.setSprinklerEnabled(sprinklerEnabled);
        response.setTargetHumidity(targetHumidity);
        response.setStatus(config.getStatus());
        response.setUpdatedAt(config.getUpdatedAt());
        return response;
    }
}

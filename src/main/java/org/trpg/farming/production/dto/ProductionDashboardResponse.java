package org.trpg.farming.production.dto;
import lombok.Data;
import org.trpg.farming.production.po.CameraView;
import org.trpg.farming.production.po.EnvironmentSnapshot;
import org.trpg.farming.production.po.OrchestrationConfig;

@Data
public class ProductionDashboardResponse {

    private Boolean available;
    private Long resourceId;
    private String message;

    private OrchestrationDTO orchestration;
    private EnvironmentDTO environment;
    private CameraDTO camera;

    public static ProductionDashboardResponse unavailable(Long resourceId, String message) {
        ProductionDashboardResponse resp = new ProductionDashboardResponse();
        resp.setAvailable(false);
        resp.setResourceId(resourceId);
        resp.setMessage(message);
        return resp;
    }

    public static ProductionDashboardResponse available(Long resourceId,
                                                        OrchestrationConfig config,
                                                        EnvironmentSnapshot env,
                                                        CameraView cameraView) {
        ProductionDashboardResponse resp = new ProductionDashboardResponse();
        resp.setAvailable(true);
        resp.setResourceId(resourceId);

        if (config != null) {
            OrchestrationDTO dto = new OrchestrationDTO();
            dto.setName(config.getName());
            dto.setTimesPerDay(config.getTimesPerDay());
            dto.setDurationMinutes(config.getDurationMinutes());
            dto.setStartTimesJson(config.getStartTimesJson());
            dto.setStatus(config.getStatus());
            resp.setOrchestration(dto);
        }

        if (env != null) {
            EnvironmentDTO dto = new EnvironmentDTO();
            dto.setTemperature(env.getTemperature());
            dto.setHumidity(env.getHumidity());
            dto.setLightLux(env.getLightLux());
            dto.setSoilMoisture(env.getSoilMoisture());
            dto.setAirQuality(env.getAirQuality());
            dto.setSnapshotTime(env.getSnapshotTime());
            resp.setEnvironment(dto);
        }

        if (cameraView != null) {
            CameraDTO dto = new CameraDTO();
            dto.setCameraName(cameraView.getCameraName());
            dto.setCameraStatus(cameraView.getCameraStatus());
            dto.setLiveStreamUrl(cameraView.getLiveStreamUrl());
            dto.setLastCoverImageUrl(cameraView.getLastCoverImageUrl());
            dto.setLastOnlineAt(cameraView.getLastOnlineAt());
            resp.setCamera(dto);
        }

        return resp;
    }

    @Data
    public static class OrchestrationDTO {
        private String name;
        private Integer timesPerDay;
        private Integer durationMinutes;
        private String startTimesJson;
        private String status;
    }

    @Data
    public static class EnvironmentDTO {
        private java.math.BigDecimal temperature;
        private java.math.BigDecimal humidity;
        private java.math.BigDecimal lightLux;
        private java.math.BigDecimal soilMoisture;
        private java.math.BigDecimal airQuality;
        private java.time.LocalDateTime snapshotTime;
    }

    @Data
    public static class CameraDTO {
        private String cameraName;
        private String cameraStatus;
        private String liveStreamUrl;
        private String lastCoverImageUrl;
        private java.time.LocalDateTime lastOnlineAt;
    }
}
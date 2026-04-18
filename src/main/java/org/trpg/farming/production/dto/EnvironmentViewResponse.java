package org.trpg.farming.production.dto;

import lombok.Data;
import org.trpg.farming.production.po.EnvironmentSnapshot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EnvironmentViewResponse {

    private Long resourceId;
    private String message;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal lightLux;
    private BigDecimal soilMoisture;
    private BigDecimal airQuality;
    private LocalDateTime snapshotTime;

    /**
     * 有监测数据时，把快照内容整理成接口响应。
     */
    public static EnvironmentViewResponse fromSnapshot(Long resourceId, EnvironmentSnapshot snapshot) {
        EnvironmentViewResponse response = new EnvironmentViewResponse();
        response.setResourceId(resourceId);
        response.setMessage("获取环境数据成功");
        response.setTemperature(snapshot.getTemperature());
        response.setHumidity(snapshot.getHumidity());
        response.setLightLux(snapshot.getLightLux());
        response.setSoilMoisture(snapshot.getSoilMoisture());
        response.setAirQuality(snapshot.getAirQuality());
        response.setSnapshotTime(snapshot.getSnapshotTime());
        return response;
    }

    /**
     * 如果当前还没有传感器上报数据，就返回一个空结果给前端做提示。
     */
    public static EnvironmentViewResponse empty(Long resourceId, String message) {
        EnvironmentViewResponse response = new EnvironmentViewResponse();
        response.setResourceId(resourceId);
        response.setMessage(message);
        return response;
    }
}

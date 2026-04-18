package org.trpg.farming.production.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrchestrationUpdateReq {

    /**
     * 当前操作人，后面会拿来和订阅用户做比对。
     */
    private Long userId;

    /**
     * 每天执行多少次，例如 3 次。
     */
    private Integer timesPerDay;

    /**
     * 每次执行多久，单位是分钟。
     */
    private Integer durationMinutes;

    /**
     * 每次启动的时间点，格式统一用 HH:mm。
     */
    private List<String> startTimes;

    /**
     * 这里先当作假设备开关来处理，不接真实硬件。
     */
    private Boolean sprinklerEnabled;

    /**
     * 目标湿度，方便前端展示一个可调参数。
     */
    private Integer targetHumidity;
}

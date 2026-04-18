package org.trpg.farming.production.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PlantingPlanCreateReq {

    /**
     * 谁在提交这条计划，后面要拿它和订阅人做一次比对。
     */
    private Long userId;

    /**
     * 标题尽量短一点，前端列表展示会更清楚。
     */
    private String title;

    /**
     * 这里就是“像写日志一样”的正文内容。
     */
    private String planContent;

    /**
     * 计划准备在哪一天执行。
     */
    private LocalDate planDate;
}

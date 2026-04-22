package org.trpg.farming.production.dto;

import lombok.Data;

@Data
public class PlantingPlanListReq {

    /**
     * 当前访问生产页的订阅用户。
     */
    private Long userId;

    /**
     * 页码，默认从 1 开始。
     */
    private Integer page;

    /**
     * 每页条数。
     */
    private Integer pageSize;
}

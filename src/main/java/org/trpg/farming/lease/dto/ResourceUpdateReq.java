package org.trpg.farming.lease.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResourceUpdateReq {

    /**
     * 请求侧统一用 userId 表示当前操作用户。
     */
    private Long userId;

    /**
     * 资源标题。
     */
    private String title;

    /**
     * 资源类型，例如温室、大棚、农田。
     */
    private String resourceType;

    /**
     * 资源面积。
     */
    private BigDecimal area;

    /**
     * 位置描述。
     */
    private String locationDesc;

    /**
     * 月租金。
     */
    private BigDecimal pricePerMonth;

    /**
     * 最短租赁月数。
     */
    private Integer minLeaseMonths;

    /**
     * 资源说明，可为空。
     */
    private String description;
}

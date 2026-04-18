package org.trpg.farming.lease.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResourceCreateReq {

    /**
     * 请求侧统一用 userId 表示当前操作用户。
     */
    private Long userId;

    private String title;
    private String resourceType;
    private BigDecimal area;
    private String locationDesc;
    private BigDecimal pricePerMonth;
    private Integer minLeaseMonths;
    private String description;
}

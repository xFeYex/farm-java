package org.trpg.farming.lease.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriptionCreateReq {

    /**
     * 当前要订阅哪一条资源。
     */
    private Long resourceId;

    /**
     * 请求侧统一用 userId 表示当前操作用户。
     */
    private Long userId;

    /**
     * 订阅起始日期。
     */
    private LocalDate startDate;

    /**
     * 租期按月传入，服务层会据此自动计算结束日期。
     */
    private Integer leaseMonths;
}

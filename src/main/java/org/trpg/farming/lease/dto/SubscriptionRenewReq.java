package org.trpg.farming.lease.dto;

import lombok.Data;

@Data
public class SubscriptionRenewReq {

    /**
     * 请求侧统一用 userId 表示当前操作用户。
     */
    private Long userId;

    /**
     * 续订时只需要告诉系统继续延长多少个月。
     */
    private Integer leaseMonths;
}

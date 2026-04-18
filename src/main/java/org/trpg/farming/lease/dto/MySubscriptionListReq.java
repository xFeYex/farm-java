package org.trpg.farming.lease.dto;

import lombok.Data;

@Data
public class MySubscriptionListReq {

    /**
     * 请求侧统一用 userId 表示当前操作用户。
     */
    private Long userId;

    private Integer page;
    private Integer pageSize;
}

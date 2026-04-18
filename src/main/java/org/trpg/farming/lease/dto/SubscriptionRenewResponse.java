package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Subscription;

import java.time.LocalDate;

@Data
public class SubscriptionRenewResponse {

    private Long id;
    private String status;
    private LocalDate endDate;

    /**
     * 续订接口只返回当前订阅的关键变更结果，避免把详情数据重复塞进来。
     */
    public static SubscriptionRenewResponse from(Subscription subscription) {
        SubscriptionRenewResponse response = new SubscriptionRenewResponse();
        response.setId(subscription.getId());
        response.setStatus(subscription.getStatus());
        response.setEndDate(subscription.getEndDate());
        return response;
    }
}

package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Subscription;

import java.time.LocalDate;

@Data
public class SubscriptionCreateResponse {

    private Long id;
    private Long resourceId;
    private Long tenantUserId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    /**
     * 创建成功后只返回当前流程最关心的订阅结果字段。
     */
    public static SubscriptionCreateResponse from(Subscription subscription) {
        SubscriptionCreateResponse response = new SubscriptionCreateResponse();
        response.setId(subscription.getId());
        response.setResourceId(subscription.getResourceId());
        response.setTenantUserId(subscription.getTenantUserId());
        response.setStartDate(subscription.getStartDate());
        response.setEndDate(subscription.getEndDate());
        response.setStatus(subscription.getStatus());
        return response;
    }
}

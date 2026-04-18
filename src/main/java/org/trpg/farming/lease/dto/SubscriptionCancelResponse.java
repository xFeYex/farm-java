package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Subscription;

@Data
public class SubscriptionCancelResponse {

    private Long id;
    private String status;

    /**
     * 退订后前端只需要知道是哪条订阅被关闭，以及当前关闭后的状态。
     */
    public static SubscriptionCancelResponse from(Subscription subscription) {
        SubscriptionCancelResponse response = new SubscriptionCancelResponse();
        response.setId(subscription.getId());
        response.setStatus(subscription.getStatus());
        return response;
    }
}

package org.trpg.farming.lease.dto;

import lombok.Data;

import java.util.List;

@Data
public class MySubscriptionPageResponse {

    private long total;
    private List<MySubscriptionListItemResponse> list;

    /**
     * “我的订阅”列表直接使用仓储层投影结果，避免重复搬运字段。
     */
    public static MySubscriptionPageResponse of(long total, List<MySubscriptionListItemResponse> list) {
        MySubscriptionPageResponse response = new MySubscriptionPageResponse();
        response.setTotal(total);
        response.setList(list);
        return response;
    }
}

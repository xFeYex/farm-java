package org.trpg.farming.lease.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MySubscriptionListItemResponse {

    private Long id;
    private Long resourceId;
    private String resourceTitle;
    private String resourceStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}

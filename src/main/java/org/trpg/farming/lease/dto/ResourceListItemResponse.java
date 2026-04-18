package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Resource;

import java.math.BigDecimal;

@Data
public class ResourceListItemResponse {

    private Long id;
    private String title;
    private String resourceType;
    private BigDecimal area;
    private String locationDesc;
    private BigDecimal pricePerMonth;
    private Integer minLeaseMonths;
    private String status;

    public static ResourceListItemResponse from(Resource resource) {
        ResourceListItemResponse response = new ResourceListItemResponse();
        response.setId(resource.getId());
        response.setTitle(resource.getTitle());
        response.setResourceType(resource.getResourceType());
        response.setArea(resource.getArea());
        response.setLocationDesc(resource.getLocationDesc());
        response.setPricePerMonth(resource.getPricePerMonth());
        response.setMinLeaseMonths(resource.getMinLeaseMonths());
        response.setStatus(resource.getStatus());
        return response;
    }
}

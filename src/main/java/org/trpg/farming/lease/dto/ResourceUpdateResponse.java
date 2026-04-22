package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Resource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResourceUpdateResponse {

    private Long id;
    private Long ownerUserId;
    private String title;
    private String resourceType;
    private BigDecimal area;
    private String locationDesc;
    private BigDecimal pricePerMonth;
    private Integer minLeaseMonths;
    private String description;
    private String status;
    private LocalDateTime updatedAt;

    /**
     * 编辑资源成功后，直接把当前最新的资源快照返回给前端。
     */
    public static ResourceUpdateResponse from(Resource resource) {
        ResourceUpdateResponse response = new ResourceUpdateResponse();
        response.setId(resource.getId());
        response.setOwnerUserId(resource.getOwnerUserId());
        response.setTitle(resource.getTitle());
        response.setResourceType(resource.getResourceType());
        response.setArea(resource.getArea());
        response.setLocationDesc(resource.getLocationDesc());
        response.setPricePerMonth(resource.getPricePerMonth());
        response.setMinLeaseMonths(resource.getMinLeaseMonths());
        response.setDescription(resource.getDescription());
        response.setStatus(resource.getStatus());
        response.setUpdatedAt(resource.getUpdatedAt());
        return response;
    }
}

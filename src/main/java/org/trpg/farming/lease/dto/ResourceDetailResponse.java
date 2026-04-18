package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Resource;

import java.math.BigDecimal;

@Data
public class ResourceDetailResponse {

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

    /**
     * 详情页当前先直接返回资源主表里的核心字段。
     * 发布者扩展信息、当前用户订阅状态后面再按流程继续补。
     */
    public static ResourceDetailResponse from(Resource resource) {
        ResourceDetailResponse response = new ResourceDetailResponse();
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
        return response;
    }
}

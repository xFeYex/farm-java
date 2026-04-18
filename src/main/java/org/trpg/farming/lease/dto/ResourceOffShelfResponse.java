package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Resource;

@Data
public class ResourceOffShelfResponse {

    private Long id;
    private String status;

    /**
     * 下架后前端只需要知道资源 ID 和最新状态。
     */
    public static ResourceOffShelfResponse from(Resource resource) {
        ResourceOffShelfResponse response = new ResourceOffShelfResponse();
        response.setId(resource.getId());
        response.setStatus(resource.getStatus());
        return response;
    }
}

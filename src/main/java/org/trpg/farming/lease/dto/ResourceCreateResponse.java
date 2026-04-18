package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Resource;

@Data
public class ResourceCreateResponse {

    private Long id;
    private String title;
    private String status;

    public static ResourceCreateResponse from(Resource resource) {
        ResourceCreateResponse response = new ResourceCreateResponse();
        response.setId(resource.getId());
        response.setTitle(resource.getTitle());
        response.setStatus(resource.getStatus());
        return response;
    }
}

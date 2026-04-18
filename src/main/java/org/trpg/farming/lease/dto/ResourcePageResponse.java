package org.trpg.farming.lease.dto;

import lombok.Data;
import org.trpg.farming.lease.po.Resource;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResourcePageResponse {

    private long total;
    private List<ResourceListItemResponse> list;

    public static ResourcePageResponse of(long total, List<Resource> resources) {
        ResourcePageResponse response = new ResourcePageResponse();
        response.setTotal(total);

        List<ResourceListItemResponse> items = new ArrayList<>(resources.size());
        for (Resource resource : resources) {
            items.add(ResourceListItemResponse.from(resource));
        }
        response.setList(items);
        return response;
    }
}

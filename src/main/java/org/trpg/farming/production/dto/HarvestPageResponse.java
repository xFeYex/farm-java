package org.trpg.farming.production.dto;

import lombok.Data;
import org.trpg.farming.production.po.ProductionHarvest;

import java.util.ArrayList;
import java.util.List;

@Data
public class HarvestPageResponse {

    private long total;
    private List<HarvestListItemResponse> list;

    /**
     * 收获记录列表统一返回 total + list，便于前端直接接分页表格或时间线。
     */
    public static HarvestPageResponse of(long total, List<ProductionHarvest> harvests) {
        HarvestPageResponse response = new HarvestPageResponse();
        response.setTotal(total);

        List<HarvestListItemResponse> items = new ArrayList<>(harvests.size());
        for (ProductionHarvest harvest : harvests) {
            items.add(HarvestListItemResponse.from(harvest));
        }
        response.setList(items);
        return response;
    }
}

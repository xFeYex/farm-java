package org.trpg.farming.production.service;

import org.trpg.farming.production.dto.HarvestCreateReq;
import org.trpg.farming.production.dto.HarvestCreateResponse;
import org.trpg.farming.production.dto.HarvestListReq;
import org.trpg.farming.production.dto.HarvestPageResponse;

public interface HarvestService {

    /**
     * 第四个流程：登记一次新的收获记录。
     */
    HarvestCreateResponse createHarvest(Long resourceId, HarvestCreateReq request);

    /**
     * 查询当前资源下、当前有效订阅范围内的收获记录列表。
     */
    HarvestPageResponse listHarvests(Long resourceId, HarvestListReq request);
}

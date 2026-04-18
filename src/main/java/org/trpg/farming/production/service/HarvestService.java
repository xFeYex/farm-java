package org.trpg.farming.production.service;

import org.trpg.farming.production.dto.HarvestCreateReq;
import org.trpg.farming.production.dto.HarvestCreateResponse;

public interface HarvestService {

    /**
     * 第四个流程：登记一次新的收获记录。
     */
    HarvestCreateResponse createHarvest(Long resourceId, HarvestCreateReq request);
}

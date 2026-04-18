package org.trpg.farming.production.service;

import org.trpg.farming.production.dto.OrchestrationUpdateReq;
import org.trpg.farming.production.dto.OrchestrationUpdateResponse;

public interface ProductionOrchestrationService {

    /**
     * 修改简单编排参数。
     */
    OrchestrationUpdateResponse updateSimpleConfig(Long resourceId, OrchestrationUpdateReq request);
}

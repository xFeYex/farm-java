package org.trpg.farming.production.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.production.common.Result;
import org.trpg.farming.production.dto.OrchestrationUpdateReq;
import org.trpg.farming.production.dto.OrchestrationUpdateResponse;
import org.trpg.farming.production.service.ProductionOrchestrationService;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class ProductionOrchestrationController {

    private final ProductionOrchestrationService productionOrchestrationService;

    /**
     * 修改简单编排参数和假设备参数。
     * 示例：
     * PUT /api/production/orchestration/101
     */
    @PutMapping("/orchestration/{resourceId}")
    public Result<OrchestrationUpdateResponse> updateOrchestration(
            @PathVariable Long resourceId,
            @RequestBody OrchestrationUpdateReq request) {

        OrchestrationUpdateResponse response =
                productionOrchestrationService.updateSimpleConfig(resourceId, request);
        return Result.success(response);
    }
}

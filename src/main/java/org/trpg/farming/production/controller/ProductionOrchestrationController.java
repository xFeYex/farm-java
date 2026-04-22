package org.trpg.farming.production.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.production.common.BizException;
import org.trpg.farming.production.common.Result;
import org.trpg.farming.production.dto.OrchestrationUpdateReq;
import org.trpg.farming.production.dto.OrchestrationUpdateResponse;
import org.trpg.farming.production.service.ProductionOrchestrationService;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
@Slf4j
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

        log.info("开始修改编排和设备参数, resourceId={}, request={}", resourceId, request);
        try {
            OrchestrationUpdateResponse response =
                    productionOrchestrationService.updateSimpleConfig(resourceId, request);
            log.info("修改编排和设备参数成功, resourceId={}, configId={}, status={}",
                    resourceId, response.getConfigId(), response.getStatus());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("修改编排和设备参数失败, resourceId={}, request={}, message={}",
                    resourceId, request, ex.getMessage(), ex);
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("修改编排和设备参数发生系统异常, resourceId={}, request={}",
                    resourceId, request, ex);
            return Result.fail(500, "修改编排和设备参数失败: " + ex.getMessage());
        }
    }
}

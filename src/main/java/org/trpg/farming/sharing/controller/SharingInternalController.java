package org.trpg.farming.sharing.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.sharing.common.ApiResponse;
import org.trpg.farming.sharing.dto.request.HarvestBatchConfirmRequest;
import org.trpg.farming.sharing.service.SharingProductService;

/**
 * 接收上游领域事件的内部接口。
 */
@RestController
@RequestMapping("/api/internal/sharing")
public class SharingInternalController {

    private final SharingProductService sharingProductService;

    public SharingInternalController(SharingProductService sharingProductService) {
        this.sharingProductService = sharingProductService;
    }

    /**
     * 把已确认的收获批次转换成共享商品记录。
     */
    @PostMapping("/harvest-batches/confirm")
    public ApiResponse<?> confirmHarvest(@RequestBody HarvestBatchConfirmRequest request) {
        return ApiResponse.success("Harvest batch accepted", sharingProductService.confirmHarvest(request));
    }
}

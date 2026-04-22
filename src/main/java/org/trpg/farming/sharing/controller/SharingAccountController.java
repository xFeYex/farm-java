package org.trpg.farming.sharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.sharing.common.ApiResponse;
import org.trpg.farming.sharing.service.SharingAccountService;

/**
 * 共享币账户和流水的只读查询接口。
 */
@RestController
@RequestMapping("/api/sharing/coin-accounts")
public class SharingAccountController {

    private final SharingAccountService sharingAccountService;

    public SharingAccountController(SharingAccountService sharingAccountService) {
        this.sharingAccountService = sharingAccountService;
    }

    @GetMapping("/{userId}")
    public ApiResponse<?> getAccount(@PathVariable Long userId) {
        return ApiResponse.success(sharingAccountService.getAccount(userId));
    }

    @GetMapping("/{userId}/ledgers")
    public ApiResponse<?> listLedgers(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(sharingAccountService.listLedgers(userId, page, pageSize));
    }
}

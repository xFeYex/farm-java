package org.trpg.farming.sharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.sharing.common.ApiResponse;
import org.trpg.farming.sharing.dto.request.CreateListingRequest;
import org.trpg.farming.sharing.dto.request.UpdateListingRequest;
import org.trpg.farming.sharing.service.SharingProductService;

/**
 * 共享模块中商品池和上架条目的 HTTP 接口。
 */
@RestController
@RequestMapping("/api/sharing")
public class SharingProductController {

    private final SharingProductService sharingProductService;

    public SharingProductController(SharingProductService sharingProductService) {
        this.sharingProductService = sharingProductService;
    }

    /**
     * 查询共享商品池，演示场景下默认只看 READY 状态商品。
     */
    @GetMapping("/products")
    public ApiResponse<?> listProducts(@RequestParam(defaultValue = "READY") String status,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(sharingProductService.listProducts(status, page, pageSize));
    }

    @GetMapping("/products/{id}")
    public ApiResponse<?> getProduct(@PathVariable Long id) {
        return ApiResponse.success(sharingProductService.getProduct(id));
    }

    /**
     * 基于共享商品创建一个可兑换的上架条目。
     */
    @PostMapping("/listings")
    public ApiResponse<?> createListing(@RequestBody CreateListingRequest request) {
        return ApiResponse.success("Listing created successfully", sharingProductService.createListing(request));
    }

    @PutMapping("/listings/{id}")
    public ApiResponse<?> updateListing(@PathVariable Long id, @RequestBody UpdateListingRequest request) {
        return ApiResponse.success("Listing updated successfully", sharingProductService.updateListing(id, request));
    }

    @GetMapping("/listings")
    public ApiResponse<?> listListings(@RequestParam(required = false) String status,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int pageSize) {
        return ApiResponse.success(sharingProductService.listListings(status, page, pageSize));
    }

    @GetMapping("/listings/{id}")
    public ApiResponse<?> getListing(@PathVariable Long id) {
        return ApiResponse.success(sharingProductService.getListing(id));
    }

    /**
     * 下架条目，避免后续再生成新的兑换订单。
     */
    @PostMapping("/listings/{id}/off-shelf")
    public ApiResponse<?> offShelf(@PathVariable Long id) {
        return ApiResponse.success("Listing moved off shelf", sharingProductService.offShelf(id));
    }
}

package org.trpg.farming.lease.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trpg.farming.lease.common.BizException;
import org.trpg.farming.lease.common.Result;
import org.trpg.farming.lease.dto.ResourceCreateReq;
import org.trpg.farming.lease.dto.ResourceCreateResponse;
import org.trpg.farming.lease.dto.ResourceDetailResponse;
import org.trpg.farming.lease.dto.ResourceListReq;
import org.trpg.farming.lease.dto.ResourcePageResponse;
import org.trpg.farming.lease.dto.ResourceOffShelfReq;
import org.trpg.farming.lease.dto.ResourceOffShelfResponse;
import org.trpg.farming.lease.dto.ResourceUpdateReq;
import org.trpg.farming.lease.dto.ResourceUpdateResponse;
import org.trpg.farming.lease.service.ResourceService;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * 发布资源。
     * 这是租赁域第一条主流程，先把“表单提交 -> 保存资源 -> 返回主键和状态”打通。
     */
    @PostMapping
    public Result<ResourceCreateResponse> create(@RequestBody ResourceCreateReq request) {
        try {
            log.info("发布资源, userId={}, title={}", request.getUserId(), request.getTitle());
            ResourceCreateResponse response = resourceService.createResource(request);
            log.info("发布资源成功, resourceId={}, userId={}", response.getId(), request.getUserId());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("发布资源失败, userId={}, title={}, message={}",
                    request.getUserId(), request.getTitle(), ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("发布资源异常, userId={}, title={}", request.getUserId(), request.getTitle(), ex);
            return Result.fail(500, "发布资源失败");
        }
    }

    /**
     * 资源广场列表。
     * 当前阶段只开放上架资源查询，并支持类型筛选和基础分页。
     */
    /**
     * 缂栬緫璧勬簮銆?
     * 褰撳墠鎸夆€滃彂甯冭〃鍗曞啀娆′繚瀛樷€濈殑鏂瑰紡澶勭悊锛岀敱璧勬簮鍙戝竷鑰呮湰浜烘彁浜ゆ渶鏂扮殑鍩虹淇℃伅銆?
     */
    @PutMapping("/{id}")
    public Result<ResourceUpdateResponse> update(
            @PathVariable Long id,
            @RequestBody ResourceUpdateReq request) {
        Long userId = request == null ? null : request.getUserId();
        String title = request == null ? null : request.getTitle();
        try {
            log.info("缂栬緫璧勬簮, resourceId={}, userId={}, title={}", id, userId, title);
            ResourceUpdateResponse response = resourceService.updateResource(id, request);
            log.info("缂栬緫璧勬簮鎴愬姛, resourceId={}, status={}", response.getId(), response.getStatus());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("缂栬緫璧勬簮澶辫触, resourceId={}, userId={}, message={}", id, userId, ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("缂栬緫璧勬簮寮傚父, resourceId={}, userId={}", id, userId, ex);
            return Result.fail(500, "缂栬緫璧勬簮澶辫触");
        }
    }

    @GetMapping
    public Result<ResourcePageResponse> list(ResourceListReq request) {
        try {
            log.info("查询资源广场列表, type={}, status={}, page={}, pageSize={}",
                    request.getType(), request.getStatus(), request.getPage(), request.getPageSize());
            ResourcePageResponse response = resourceService.listResources(request);
            log.info("查询资源广场列表成功, total={}", response.getTotal());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("查询资源广场列表失败, type={}, status={}, message={}",
                    request.getType(), request.getStatus(), ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("查询资源广场列表异常, type={}, status={}", request.getType(), request.getStatus(), ex);
            return Result.fail(500, "查询资源广场列表失败");
        }
    }

    /**
     * 资源详情。
     * 这一步先把“根据资源 ID 查看基础信息”的主链路打通。
     */
    @GetMapping("/{id}")
    public Result<ResourceDetailResponse> detail(@PathVariable Long id) {
        try {
            log.info("查询资源详情, resourceId={}", id);
            ResourceDetailResponse response = resourceService.getResourceDetail(id);
            log.info("查询资源详情成功, resourceId={}", id);
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("查询资源详情失败, resourceId={}, message={}", id, ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("查询资源详情异常, resourceId={}", id, ex);
            return Result.fail(500, "查询资源详情失败");
        }
    }

    /**
     * 下架资源。
     * 当前按软下架处理，只把资源状态改成 OFF_SHELF。
     */
    @PostMapping("/{id}/off-shelf")
    public Result<ResourceOffShelfResponse> offShelf(
            @PathVariable Long id,
            @RequestBody(required = false) ResourceOffShelfReq request) {
        Long userId = request == null ? null : request.getUserId();
        try {
            log.info("下架资源, resourceId={}, userId={}", id, userId);
            ResourceOffShelfResponse response = resourceService.offShelfResource(id, request);
            log.info("下架资源成功, resourceId={}, status={}", response.getId(), response.getStatus());
            return Result.success(response);
        } catch (BizException ex) {
            log.warn("下架资源失败, resourceId={}, userId={}, message={}",
                    id, userId, ex.getMessage());
            return Result.fail(400, ex.getMessage());
        } catch (Exception ex) {
            log.error("下架资源异常, resourceId={}, userId={}", id, userId, ex);
            return Result.fail(500, "下架资源失败");
        }
    }
}

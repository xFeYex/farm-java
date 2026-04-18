package org.trpg.farming.lease.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.trpg.farming.lease.common.BizException;
import org.trpg.farming.lease.dao.ResourceRepository;
import org.trpg.farming.lease.dto.MyResourceListReq;
import org.trpg.farming.lease.dto.ResourceCreateReq;
import org.trpg.farming.lease.dto.ResourceCreateResponse;
import org.trpg.farming.lease.dto.ResourceDetailResponse;
import org.trpg.farming.lease.dto.ResourceListReq;
import org.trpg.farming.lease.dto.ResourcePageResponse;
import org.trpg.farming.lease.dto.ResourceOffShelfReq;
import org.trpg.farming.lease.dto.ResourceOffShelfResponse;
import org.trpg.farming.lease.po.Resource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private static final String RESOURCE_STATUS_ON_SHELF = "ON_SHELF";
    private static final String RESOURCE_STATUS_OFF_SHELF = "OFF_SHELF";

    private final ResourceRepository resourceRepository;

    public ResourceCreateResponse createResource(ResourceCreateReq request) {
        validateCreateRequest(request);

        Resource resource = buildResource(request);
        int affectedRows = resourceRepository.insert(resource);
        if (affectedRows != 1) {
            throw new BizException("发布资源失败了，请稍后再试");
        }

        return ResourceCreateResponse.from(resource);
    }

    /**
     * 详情页先返回资源基础信息。
     * 当前阶段不额外拼发布者资料和“我的订阅状态”，避免把后续流程提前耦合进来。
     */
    public ResourceDetailResponse getResourceDetail(Long id) {
        if (id == null || id <= 0) {
            throw new BizException("资源 ID 非法");
        }

        Resource resource = resourceRepository.findById(id);
        if (resource == null) {
            throw new BizException("资源不存在");
        }
        return ResourceDetailResponse.from(resource);
    }

    /**
     * 下架后资源不再接受新订阅，但不会影响已经存在的有效订阅记录。
     */
    public ResourceOffShelfResponse offShelfResource(Long id, ResourceOffShelfReq request) {
        validateOffShelfRequest(id, request);

        Resource resource = loadResource(id);
        if (resource.getOwnerUserId() == null || !resource.getOwnerUserId().equals(request.getUserId())) {
            throw new BizException("只有发布者本人才能下架资源");
        }
        if (RESOURCE_STATUS_OFF_SHELF.equals(resource.getStatus())) {
            throw new BizException("资源已经是 OFF_SHELF 状态");
        }

        resource.setStatus(RESOURCE_STATUS_OFF_SHELF);
        resource.setUpdatedAt(LocalDateTime.now());

        int affectedRows = resourceRepository.updateById(resource);
        if (affectedRows != 1) {
            throw new BizException("下架资源失败了，请稍后再试");
        }
        return ResourceOffShelfResponse.from(resource);
    }

    /**
     * 广场页只负责“上架资源 + 类型筛选 + 分页”这一条最小主链路。
     */
    public ResourcePageResponse listResources(ResourceListReq request) {
        ResourceListReq normalizedRequest = normalizeListRequest(request);

        boolean filteredByType = normalizedRequest.getType() != null;
        long total = filteredByType
                ? resourceRepository.countOnShelfResourcesByType(
                        normalizedRequest.getStatus(),
                        normalizedRequest.getType())
                : resourceRepository.countOnShelfResources(normalizedRequest.getStatus());
        if (total <= 0) {
            return ResourcePageResponse.of(0, List.of());
        }

        int offset = (normalizedRequest.getPage() - 1) * normalizedRequest.getPageSize();
        List<Resource> resources = filteredByType
                ? resourceRepository.pageQueryOnShelfResourcesByType(
                        normalizedRequest.getStatus(),
                        normalizedRequest.getType(),
                        offset,
                        normalizedRequest.getPageSize())
                : resourceRepository.pageQueryOnShelfResources(
                        normalizedRequest.getStatus(),
                        offset,
                        normalizedRequest.getPageSize());
        return ResourcePageResponse.of(total, resources);
    }

    /**
     * “我的发布”列表保留所有状态资源，重点按发布者本人维度分页查看。
     */
    public ResourcePageResponse listMyResources(MyResourceListReq request) {
        MyResourceListReq normalizedRequest = normalizeMyResourceListRequest(request);

        long total = resourceRepository.countByOwnerUserId(normalizedRequest.getUserId());
        if (total <= 0) {
            return ResourcePageResponse.of(0, List.of());
        }

        int offset = (normalizedRequest.getPage() - 1) * normalizedRequest.getPageSize();
        List<Resource> resources = resourceRepository.pageQueryByOwnerUserId(
                normalizedRequest.getUserId(),
                offset,
                normalizedRequest.getPageSize());
        return ResourcePageResponse.of(total, resources);
    }

    private void validateCreateRequest(ResourceCreateReq request) {
        if (request == null) {
            throw new BizException("请求体不能为空");
        }
        if (request.getUserId() == null) {
            throw new BizException("userId 不能为空");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            throw new BizException("标题不能为空");
        }
        if (!StringUtils.hasText(request.getResourceType())) {
            throw new BizException("资源类型不能为空");
        }
        if (request.getArea() == null || !isPositive(request.getArea())) {
            throw new BizException("面积必须大于 0");
        }
        if (!StringUtils.hasText(request.getLocationDesc())) {
            throw new BizException("位置描述不能为空");
        }
        if (request.getPricePerMonth() == null || !isPositive(request.getPricePerMonth())) {
            throw new BizException("月租金必须大于 0");
        }
        if (request.getMinLeaseMonths() == null || request.getMinLeaseMonths() <= 0) {
            throw new BizException("最短租期必须大于 0");
        }
    }

    /**
     * “我的发布”查询阶段只校验用户身份和基础分页参数。
     */
    private MyResourceListReq normalizeMyResourceListRequest(MyResourceListReq request) {
        if (request == null) {
            throw new BizException("查询参数不能为空");
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BizException("userId 非法");
        }

        int page = request.getPage() == null ? 1 : request.getPage();
        int pageSize = request.getPageSize() == null ? 10 : request.getPageSize();
        if (page <= 0) {
            throw new BizException("page 必须大于 0");
        }
        if (pageSize <= 0) {
            throw new BizException("pageSize 必须大于 0");
        }
        if (pageSize > 100) {
            throw new BizException("pageSize 不能超过 100");
        }

        request.setPage(page);
        request.setPageSize(pageSize);
        return request;
    }

    /**
     * 资源下架阶段只需要资源 ID 和当前操作人的发布者 ID。
     */
    private void validateOffShelfRequest(Long id, ResourceOffShelfReq request) {
        if (id == null || id <= 0) {
            throw new BizException("资源 ID 非法");
        }
        if (request == null) {
            throw new BizException("请求体不能为空");
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BizException("userId 非法");
        }
    }

    private Resource buildResource(ResourceCreateReq request) {
        LocalDateTime now = LocalDateTime.now();

        Resource resource = new Resource();
        resource.setOwnerUserId(request.getUserId());
        resource.setTitle(request.getTitle().trim());
        resource.setResourceType(request.getResourceType().trim());
        resource.setArea(request.getArea());
        resource.setLocationDesc(request.getLocationDesc().trim());
        resource.setPricePerMonth(request.getPricePerMonth());
        resource.setMinLeaseMonths(request.getMinLeaseMonths());
        resource.setDescription(normalizeDescription(request.getDescription()));
        resource.setStatus(RESOURCE_STATUS_ON_SHELF);
        resource.setCreatedAt(now);
        resource.setUpdatedAt(now);
        return resource;
    }

    /**
     * 资源的通用读取入口，避免详情、下架等流程重复写“查不到就报错”。
     */
    private Resource loadResource(Long id) {
        Resource resource = resourceRepository.findById(id);
        if (resource == null) {
            throw new BizException("资源不存在");
        }
        return resource;
    }

    private ResourceListReq normalizeListRequest(ResourceListReq request) {
        ResourceListReq normalized = request == null ? new ResourceListReq() : request;

        int page = normalized.getPage() == null ? 1 : normalized.getPage();
        int pageSize = normalized.getPageSize() == null ? 10 : normalized.getPageSize();

        if (page <= 0) {
            throw new BizException("page 必须大于 0");
        }
        if (pageSize <= 0) {
            throw new BizException("pageSize 必须大于 0");
        }
        if (pageSize > 100) {
            throw new BizException("pageSize 不能超过 100");
        }

        if (StringUtils.hasText(normalized.getStatus())
                && !RESOURCE_STATUS_ON_SHELF.equals(normalized.getStatus().trim())) {
            throw new BizException("资源广场目前只允许查询 ON_SHELF 状态");
        }

        normalized.setPage(page);
        normalized.setPageSize(pageSize);
        normalized.setStatus(RESOURCE_STATUS_ON_SHELF);
        normalized.setType(normalizeType(normalized.getType()));
        return normalized;
    }

    private String normalizeDescription(String description) {
        return StringUtils.hasText(description) ? description.trim() : null;
    }

    private String normalizeType(String type) {
        return StringUtils.hasText(type) ? type.trim() : null;
    }

    private boolean isPositive(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
}

package org.trpg.farming.lease.dto;

import lombok.Data;

@Data
public class ResourceListReq {

    /**
     * 资源广场目前主要按类型筛选，保持和需求文档里的查询参数一致。
     */
    private String type;

    /**
     * 公开广场只允许查询上架资源，这个字段更多是为了兼容接口参数格式。
     */
    private String status;

    private Integer page;
    private Integer pageSize;
}

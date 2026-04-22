package org.trpg.farming.sharing.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 前端展示共享商品时使用的视图对象。
 */
public class ShareProductView {
    // 共享商品主键
    private Long id;

    // 来源收获批次 ID
    private Long sourceHarvestBatchId;

    // 商品所有者用户 ID
    private Long ownerUserId;

    // 商品名称
    private String productName;

    // 来源设施 ID
    private Long originFacilityId;

    // 数量
    private BigDecimal quantity;

    // 单位
    private String unit;

    // 质量等级
    private String qualityLevel;

    // 溯源码
    private String traceCode;

    // 上游事件序列化后的快照 JSON
    private String traceSnapshotJson;

    // 商品状态
    private String status;

    // 记录创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public ShareProductView() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceHarvestBatchId() {
        return sourceHarvestBatchId;
    }

    public void setSourceHarvestBatchId(Long sourceHarvestBatchId) {
        this.sourceHarvestBatchId = sourceHarvestBatchId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getOriginFacilityId() {
        return originFacilityId;
    }

    public void setOriginFacilityId(Long originFacilityId) {
        this.originFacilityId = originFacilityId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(String qualityLevel) {
        this.qualityLevel = qualityLevel;
    }

    public String getTraceCode() {
        return traceCode;
    }

    public void setTraceCode(String traceCode) {
        this.traceCode = traceCode;
    }

    public String getTraceSnapshotJson() {
        return traceSnapshotJson;
    }

    public void setTraceSnapshotJson(String traceSnapshotJson) {
        this.traceSnapshotJson = traceSnapshotJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}

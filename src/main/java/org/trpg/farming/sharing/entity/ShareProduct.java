package org.trpg.farming.sharing.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 共享商品池中的商品实体。
 */
@Entity
@Table(name = "sharing_product")
public class ShareProduct {
    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 来源收获批次 ID，用于保证事件幂等
    @Column(name = "source_harvest_batch_id")
    private Long sourceHarvestBatchId;

    // 商品拥有者用户 ID
    @Column(name = "owner_user_id")
    private Long ownerUserId;

    // 商品名称
    @Column(name = "product_name")
    private String productName;

    // 来源设施 ID
    @Column(name = "origin_facility_id")
    private Long originFacilityId;

    // 商品数量
    @Column(name = "quantity")
    private BigDecimal quantity;

    // 数量单位
    @Column(name = "unit")
    private String unit;

    // 质量等级
    @Column(name = "quality_level")
    private String qualityLevel;

    // 溯源码
    @Column(name = "trace_code")
    private String traceCode;

    // 收获事件快照，便于后续追溯
    @Column(name = "trace_snapshot_json", columnDefinition = "TEXT")
    private String traceSnapshotJson;

    // 商品池状态
    @Column(name = "status")
    private String status;

    // 创建时间
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public ShareProduct() {
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

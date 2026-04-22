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
 * 共享兑换订单实体。
 */
@Entity
@Table(name = "sharing_order")
public class SharingOrder {
    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 业务订单号
    @Column(name = "order_no")
    private String orderNo;

    // 来源上架条目 ID
    @Column(name = "listing_id")
    private Long listingId;

    // 下单时的条目标题快照
    @Column(name = "listing_title_snapshot")
    private String listingTitleSnapshot;

    // 下单时的商品名称快照
    @Column(name = "product_name_snapshot")
    private String productNameSnapshot;

    // 买家用户 ID
    @Column(name = "buyer_user_id")
    private Long buyerUserId;

    // 卖家用户 ID
    @Column(name = "seller_user_id")
    private Long sellerUserId;

    // 购买数量
    @Column(name = "quantity")
    private Integer quantity;

    // 本次订单扣减的共享币总额
    @Column(name = "coin_amount")
    private BigDecimal coinAmount;

    // 订单主状态
    @Column(name = "status")
    private String status;

    // 物流状态
    @Column(name = "shipping_status")
    private String shippingStatus;

    // 物流单号
    @Column(name = "shipping_no")
    private String shippingNo;

    // 创建时间
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // 完成时间
    @Column(name = "completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    public SharingOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public String getListingTitleSnapshot() {
        return listingTitleSnapshot;
    }

    public void setListingTitleSnapshot(String listingTitleSnapshot) {
        this.listingTitleSnapshot = listingTitleSnapshot;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public void setProductNameSnapshot(String productNameSnapshot) {
        this.productNameSnapshot = productNameSnapshot;
    }

    public Long getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Long buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public Long getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(Long sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(BigDecimal coinAmount) {
        this.coinAmount = coinAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingNo) {
        this.shippingNo = shippingNo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

}

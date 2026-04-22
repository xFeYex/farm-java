package org.trpg.farming.sharing.dto.request;

/**
 * 创建兑换订单时的入参。
 */
public class CreateOrderRequest {
    // 被兑换的上架条目 ID
    private Long listingId;

    // 买家用户 ID
    private Long buyerUserId;

    // 本次兑换数量
    private Integer quantity;

    public CreateOrderRequest() {
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public Long getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Long buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}

package org.trpg.farming.sharing.dto.request;

/**
 * 卖家发货时提交的物流信息。
 */
public class ShipOrderRequest {
    // 物流单号
    private String shippingNo;

    public ShipOrderRequest() {
    }

    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingNo) {
        this.shippingNo = shippingNo;
    }

}

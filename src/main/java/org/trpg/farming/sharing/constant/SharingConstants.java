package org.trpg.farming.sharing.constant;

/**
 * 共享模块里统一复用的状态值和业务类型常量。
 */
public final class SharingConstants {

    private SharingConstants() {
    }

    // 共享商品状态
    public static final String PRODUCT_READY = "READY";
    public static final String PRODUCT_DISABLED = "DISABLED";

    // 上架条目状态
    public static final String LISTING_PUBLISHED = "PUBLISHED";
    public static final String LISTING_OFF_SHELF = "OFF_SHELF";
    public static final String LISTING_EXPIRED = "EXPIRED";

    // 订单主状态
    public static final String ORDER_CREATED = "CREATED";
    public static final String ORDER_PAID = "PAID";
    public static final String ORDER_SHIPPED = "SHIPPED";
    public static final String ORDER_COMPLETED = "COMPLETED";
    public static final String ORDER_CANCELLED = "CANCELLED";

    // 物流状态
    public static final String SHIPPING_WAIT_SHIP = "WAIT_SHIP";
    public static final String SHIPPING_SHIPPED = "SHIPPED";
    public static final String SHIPPING_RECEIVED = "RECEIVED";

    // 账户状态
    public static final String ACCOUNT_ENABLED = "ENABLED";

    // 流水变动方向
    public static final String CHANGE_TYPE_CREDIT = "CREDIT";
    public static final String CHANGE_TYPE_DEBIT = "DEBIT";

    // 流水业务类型
    public static final String BIZ_TYPE_INIT_GRANT = "INIT_GRANT";
    public static final String BIZ_TYPE_ORDER_DEDUCT = "ORDER_DEDUCT";
    public static final String BIZ_TYPE_ORDER_REFUND = "ORDER_REFUND";
}

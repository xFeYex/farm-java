package org.trpg.farming.production.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HarvestCreateReq {

    /**
     * 当前是谁在登记收获，后面要拿它做订阅身份校验。
     */
    private Long userId;

    /**
     * 收获的农产品名称，比如番茄、黄瓜这些。
     */
    private String productName;

    /**
     * 简单记录分类，先按字符串存，后面要不要做枚举再说。
     */
    private String category;

    /**
     * 收获数量这里用 BigDecimal，后面像 12.5 斤这种数据也能存得下。
     */
    private BigDecimal harvestQuantity;

    /**
     * 数量单位，比如斤、公斤、箱。
     */
    private String unit;

    /**
     * 实际收获日期。
     */
    private LocalDate harvestDate;

    /**
     * 备注可以写成熟度、品相这些补充信息。
     */
    private String remark;
}

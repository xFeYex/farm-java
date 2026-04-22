package org.trpg.farming.sharing.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建共享上架条目时的入参。
 */
public class CreateListingRequest {
    // 来源共享商品 ID
    private Long shareProductId;

    // 上架展示标题
    private String title;

    // 单件兑换所需共享币
    private BigDecimal coinPrice;

    // 可兑换库存
    private Integer stock;

    // 条目过期时间，为空时表示由前端或业务层给默认值
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;

    public CreateListingRequest() {
    }

    public Long getShareProductId() {
        return shareProductId;
    }

    public void setShareProductId(Long shareProductId) {
        this.shareProductId = shareProductId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getCoinPrice() {
        return coinPrice;
    }

    public void setCoinPrice(BigDecimal coinPrice) {
        this.coinPrice = coinPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

}

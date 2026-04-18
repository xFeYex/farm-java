package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.trpg.farming.production.po.ProductionHarvest;

@Mapper
public interface HarvestRepository {

    /**
     * 第四条流程先把“新增收获记录”这一步打通，列表和详情可以后面再补。
     */
    @Insert("""
            INSERT INTO production_harvest (
                resource_id,
                subscription_id,
                user_id,
                product_name,
                category,
                harvest_quantity,
                unit,
                harvest_date,
                remark,
                status,
                created_at,
                updated_at
            ) VALUES (
                #{resourceId},
                #{subscriptionId},
                #{userId},
                #{productName},
                #{category},
                #{harvestQuantity},
                #{unit},
                #{harvestDate},
                #{remark},
                #{status},
                #{createdAt},
                #{updatedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(ProductionHarvest harvest);
}

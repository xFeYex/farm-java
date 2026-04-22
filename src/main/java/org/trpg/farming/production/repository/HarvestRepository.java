package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.trpg.farming.production.po.ProductionHarvest;

import java.util.List;

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

    /**
     * 收获记录列表按资源和当前有效订阅范围统计，避免串出历史租户的数据。
     */
    @Select("""
            SELECT COUNT(1)
            FROM production_harvest
            WHERE resource_id = #{resourceId}
              AND subscription_id = #{subscriptionId}
            """)
    long countByResourceIdAndSubscriptionId(
            @Param("resourceId") Long resourceId,
            @Param("subscriptionId") Long subscriptionId);

    /**
     * 列表查询复用收获记录字段映射，便于后续继续补详情或导出接口。
     */
    @Results(id = "productionHarvestResultMap", value = {
            @Result(property = "resourceId", column = "resource_id"),
            @Result(property = "subscriptionId", column = "subscription_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "productName", column = "product_name"),
            @Result(property = "harvestQuantity", column = "harvest_quantity"),
            @Result(property = "harvestDate", column = "harvest_date"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            SELECT
                id,
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
            FROM production_harvest
            WHERE resource_id = #{resourceId}
              AND subscription_id = #{subscriptionId}
            ORDER BY harvest_date DESC, created_at DESC, id DESC
            LIMIT #{offset}, #{pageSize}
            """)
    List<ProductionHarvest> pageQueryByResourceIdAndSubscriptionId(
            @Param("resourceId") Long resourceId,
            @Param("subscriptionId") Long subscriptionId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);
}

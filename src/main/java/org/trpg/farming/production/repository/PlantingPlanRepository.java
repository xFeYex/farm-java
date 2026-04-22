package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.trpg.farming.production.po.PlantingPlan;

import java.util.List;

@Mapper
public interface PlantingPlanRepository {

    /**
     * 先把创建计划这条主流程打通，别的查询和修改后面再慢慢补。
     */
    @Insert("""
            INSERT INTO planting_plan (
                resource_id,
                subscription_id,
                user_id,
                title,
                plan_content,
                plan_date,
                status,
                created_at,
                updated_at
            ) VALUES (
                #{resourceId},
                #{subscriptionId},
                #{userId},
                #{title},
                #{planContent},
                #{planDate},
                #{status},
                #{createdAt},
                #{updatedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(PlantingPlan plantingPlan);

    /**
     * 计划列表按资源和当前有效订阅范围统计，避免把历史订阅的数据混到一起。
     */
    @Select("""
            SELECT COUNT(1)
            FROM planting_plan
            WHERE resource_id = #{resourceId}
              AND subscription_id = #{subscriptionId}
            """)
    long countByResourceIdAndSubscriptionId(
            @Param("resourceId") Long resourceId,
            @Param("subscriptionId") Long subscriptionId);

    /**
     * 列表查询复用一套字段映射，后续如果补详情接口也可以直接沿用。
     */
    @Results(id = "plantingPlanResultMap", value = {
            @Result(property = "resourceId", column = "resource_id"),
            @Result(property = "subscriptionId", column = "subscription_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "planContent", column = "plan_content"),
            @Result(property = "planDate", column = "plan_date"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            SELECT
                id,
                resource_id,
                subscription_id,
                user_id,
                title,
                plan_content,
                plan_date,
                status,
                created_at,
                updated_at
            FROM planting_plan
            WHERE resource_id = #{resourceId}
              AND subscription_id = #{subscriptionId}
            ORDER BY plan_date DESC, created_at DESC, id DESC
            LIMIT #{offset}, #{pageSize}
            """)
    List<PlantingPlan> pageQueryByResourceIdAndSubscriptionId(
            @Param("resourceId") Long resourceId,
            @Param("subscriptionId") Long subscriptionId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);
}

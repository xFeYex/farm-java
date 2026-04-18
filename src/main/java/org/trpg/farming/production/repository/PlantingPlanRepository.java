package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.trpg.farming.production.po.PlantingPlan;

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
}

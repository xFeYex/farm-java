package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.trpg.farming.production.entity.OrchestrationConfig;

@Mapper
public interface OrchestrationConfigRepository {

    /**
     * 根据资源ID查询当前编排配置
     */
    @Results(id = "orchestrationConfigResultMap", value = {
            @Result(property = "resourceId", column = "resource_id"),
            @Result(property = "subscriptionId", column = "subscription_id"),
            @Result(property = "orchestrationType", column = "orchestration_type"),
            @Result(property = "scheduleMode", column = "schedule_mode"),
            @Result(property = "timesPerDay", column = "times_per_day"),
            @Result(property = "durationMinutes", column = "duration_minutes"),
            @Result(property = "startTimesJson", column = "start_times_json"),
            @Result(property = "configJson", column = "config_json"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            SELECT
                id,
                resource_id,
                subscription_id,
                orchestration_type,
                name,
                schedule_mode,
                times_per_day,
                duration_minutes,
                start_times_json,
                config_json,
                status,
                created_at,
                updated_at
            FROM production_orchestration_config
            WHERE resource_id = #{resourceId}
            ORDER BY id DESC
            LIMIT 1
            """)
    OrchestrationConfig findByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 第一次还没有配置记录时，允许补一条默认配置。
     */
    @Insert("""
            INSERT INTO production_orchestration_config (
                resource_id,
                subscription_id,
                orchestration_type,
                name,
                schedule_mode,
                times_per_day,
                duration_minutes,
                start_times_json,
                config_json,
                status,
                created_at,
                updated_at
            ) VALUES (
                #{resourceId},
                #{subscriptionId},
                #{orchestrationType},
                #{name},
                #{scheduleMode},
                #{timesPerDay},
                #{durationMinutes},
                #{startTimesJson},
                #{configJson},
                #{status},
                #{createdAt},
                #{updatedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(OrchestrationConfig config);

    /**
     * 只更新白名单字段，避免把整条配置随手覆盖掉。
     */
    @Update("""
            UPDATE production_orchestration_config
            SET subscription_id = #{subscriptionId},
                orchestration_type = #{orchestrationType},
                name = #{name},
                schedule_mode = #{scheduleMode},
                times_per_day = #{timesPerDay},
                duration_minutes = #{durationMinutes},
                start_times_json = #{startTimesJson},
                config_json = #{configJson},
                status = #{status},
                updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    int updateById(OrchestrationConfig config);
}

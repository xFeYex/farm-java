package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.trpg.farming.production.po.EnvironmentSnapshot;

@Mapper
public interface EnvironmentSnapshotRepository {

    /**
     * 查询最新环境快照
     */
    @Results(id = "environmentSnapshotResultMap", value = {
            @Result(property = "resourceId", column = "resource_id"),
            @Result(property = "lightLux", column = "light_lux"),
            @Result(property = "soilMoisture", column = "soil_moisture"),
            @Result(property = "airQuality", column = "air_quality"),
            @Result(property = "snapshotTime", column = "snapshot_time"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            SELECT
                id,
                resource_id,
                temperature,
                humidity,
                light_lux,
                soil_moisture,
                air_quality,
                snapshot_time,
                created_at,
                updated_at
            FROM production_environment_snapshot
            WHERE resource_id = #{resourceId}
            ORDER BY snapshot_time DESC
            LIMIT 1
            """)
    EnvironmentSnapshot findLatestByResourceId(@Param("resourceId") Long resourceId);
}

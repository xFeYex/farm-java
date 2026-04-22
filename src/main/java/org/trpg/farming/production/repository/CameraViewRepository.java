package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.trpg.farming.production.po.CameraView;

@Mapper
public interface CameraViewRepository {

    /**
     * 查询摄像头信息
     */
    @Results(id = "cameraViewResultMap", value = {
            @Result(property = "resourceId", column = "resource_id"),
            @Result(property = "cameraCode", column = "camera_code"),
            @Result(property = "cameraName", column = "camera_name"),
            @Result(property = "cameraStatus", column = "camera_status"),
            @Result(property = "liveStreamUrl", column = "live_stream_url"),
            @Result(property = "lastCoverImageUrl", column = "last_cover_image_url"),
            @Result(property = "lastOnlineAt", column = "last_online_at"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            SELECT
                id,
                resource_id,
                camera_code,
                camera_name,
                camera_status,
                live_stream_url,
                last_cover_image_url,
                last_online_at,
                created_at,
                updated_at
            FROM production_camera_view
            WHERE resource_id = #{resourceId}
            LIMIT 1
            """)
    CameraView findByResourceId(@Param("resourceId") Long resourceId);
}

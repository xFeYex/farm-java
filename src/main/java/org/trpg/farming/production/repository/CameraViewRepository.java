package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.trpg.farming.production.po.CameraView;

@Mapper
public interface CameraViewRepository {

    /**
     * 查询摄像头信息
     */
    CameraView findByResourceId(@Param("resourceId") Long resourceId);
}
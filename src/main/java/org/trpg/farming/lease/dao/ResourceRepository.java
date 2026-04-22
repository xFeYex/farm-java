package org.trpg.farming.lease.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.trpg.farming.lease.po.Resource;

import java.util.List;

@Mapper
public interface ResourceRepository {

    /**
     * 发布资源时只写入当前流程必需字段，生成后的主键会回填到 resource.id。
     */
    @Insert("""
            INSERT INTO resource (
                owner_user_id,
                title,
                resource_type,
                area,
                location_desc,
                price_per_month,
                min_lease_months,
                description,
                status,
                created_at,
                updated_at
            ) VALUES (
                #{ownerUserId},
                #{title},
                #{resourceType},
                #{area},
                #{locationDesc},
                #{pricePerMonth},
                #{minLeaseMonths},
                #{description},
                #{status},
                #{createdAt},
                #{updatedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Resource resource);

    /**
     * 资源详情页按 ID 读取单条资源，当前直接复用统一的字段映射。
     */
    @Results(id = "resourceResultMap", value = {
            @Result(property = "ownerUserId", column = "owner_user_id"),
            @Result(property = "resourceType", column = "resource_type"),
            @Result(property = "locationDesc", column = "location_desc"),
            @Result(property = "pricePerMonth", column = "price_per_month"),
            @Result(property = "minLeaseMonths", column = "min_lease_months"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            SELECT
                id,
                owner_user_id,
                title,
                resource_type,
                area,
                location_desc,
                price_per_month,
                min_lease_months,
                description,
                status,
                created_at,
                updated_at
            FROM resource
            WHERE id = #{id}
            LIMIT 1
            """)
    Resource findById(@Param("id") Long id);

    /**
     * 资源广场只统计上架资源，再按类型做可选筛选。
     */
    @Select("""
            SELECT COUNT(1)
            FROM resource
            WHERE status = #{status}
            """)
    long countOnShelfResources(@Param("status") String status);

    /**
     * 按类型筛选时单独走一条显式 SQL，避免在注解里混入动态脚本标签。
     */
    @Select("""
            SELECT COUNT(1)
            FROM resource
            WHERE status = #{status}
              AND resource_type = #{type}
            """)
    long countOnShelfResourcesByType(@Param("status") String status, @Param("type") String type);

    /**
     * 分页查询资源广场数据，卡片展示字段都从这张主表直接返回。
     */
    @ResultMap("resourceResultMap")
    @Select("""
            SELECT
                id,
                owner_user_id,
                title,
                resource_type,
                area,
                location_desc,
                price_per_month,
                min_lease_months,
                description,
                status,
                created_at,
                updated_at
            FROM resource
            WHERE status = #{status}
            ORDER BY created_at DESC, id DESC
            LIMIT #{offset}, #{pageSize}
            """)
    List<Resource> pageQueryOnShelfResources(
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 有类型筛选时走单独分页 SQL，保持注解 SQL 结构清晰。
     */
    @ResultMap("resourceResultMap")
    @Select("""
            SELECT
                id,
                owner_user_id,
                title,
                resource_type,
                area,
                location_desc,
                price_per_month,
                min_lease_months,
                description,
                status,
                created_at,
                updated_at
            FROM resource
            WHERE status = #{status}
              AND resource_type = #{type}
            ORDER BY created_at DESC, id DESC
            LIMIT #{offset}, #{pageSize}
            """)
    List<Resource> pageQueryOnShelfResourcesByType(
            @Param("status") String status,
            @Param("type") String type,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 下架阶段只更新资源状态和更新时间，不去碰其他业务字段。
     */
    @Update("""
            UPDATE resource
            SET status = #{status},
                updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    int updateById(Resource resource);

    /**
     * 缂栬緫璧勬簮鏃舵洿鏂版爣棰樸€佺被鍨嬨€侀潰绉€佷綅缃€佷环鏍煎拰璧勬簮璇存槑绛夊彲缂栬緫瀛楁銆?
     */
    @Update("""
            UPDATE resource
            SET title = #{title},
                resource_type = #{resourceType},
                area = #{area},
                location_desc = #{locationDesc},
                price_per_month = #{pricePerMonth},
                min_lease_months = #{minLeaseMonths},
                description = #{description},
                updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    int updateEditableFieldsById(Resource resource);

    /**
     * “我的发布”列表只统计当前发布者自己名下的资源。
     */
    @Select("""
            SELECT COUNT(1)
            FROM resource
            WHERE owner_user_id = #{ownerUserId}
            """)
    long countByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

    /**
     * “我的发布”分页列表，保留资源状态，方便前端展示上架/下架状态。
     */
    @ResultMap("resourceResultMap")
    @Select("""
            SELECT
                id,
                owner_user_id,
                title,
                resource_type,
                area,
                location_desc,
                price_per_month,
                min_lease_months,
                description,
                status,
                created_at,
                updated_at
            FROM resource
            WHERE owner_user_id = #{ownerUserId}
            ORDER BY created_at DESC, id DESC
            LIMIT #{offset}, #{pageSize}
            """)
    List<Resource> pageQueryByOwnerUserId(
            @Param("ownerUserId") Long ownerUserId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);
}

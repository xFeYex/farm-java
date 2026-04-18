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
import org.trpg.farming.lease.po.Subscription;
import org.trpg.farming.lease.dto.MySubscriptionListItemResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LeaseSubscriptionRepository {

    /*
     * 当前规则是同一资源同一时间只允许一个 ACTIVE 订阅。
     * 这里先按“是否存在 ACTIVE 记录”做最小校验。
     */
    @Results(id = "subscriptionResultMap", value = {
            @Result(property = "resourceId", column = "resource_id"),
            @Result(property = "tenantUserId", column = "tenant_user_id"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "cancelledAt", column = "cancelled_at"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            SELECT
                id,
                resource_id,
                tenant_user_id,
                start_date,
                end_date,
                status,
                cancelled_at,
                created_at,
                updated_at
            FROM subscription
            WHERE resource_id = #{resourceId}
              AND status = 'ACTIVE'
            ORDER BY id DESC
            LIMIT 1
            """)
    Subscription findActiveByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 续订、退订、详情等后续流程都需要先按订阅 ID 读取原记录。
     */
    @ResultMap("subscriptionResultMap")
    @Select("""
            SELECT
                id,
                resource_id,
                tenant_user_id,
                start_date,
                end_date,
                status,
                cancelled_at,
                created_at,
                updated_at
            FROM subscription
            WHERE id = #{id}
            LIMIT 1
            """)
    Subscription findById(@Param("id") Long id);

    /**
     * 创建订阅时写入开始日期、结束日期和当前状态。
     */
    @Insert("""
            INSERT INTO subscription (
                resource_id,
                tenant_user_id,
                start_date,
                end_date,
                status,
                cancelled_at,
                created_at,
                updated_at
            ) VALUES (
                #{resourceId},
                #{tenantUserId},
                #{startDate},
                #{endDate},
                #{status},
                #{cancelledAt},
                #{createdAt},
                #{updatedAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Subscription subscription);

    /**
     * 续订和退订都复用这条更新语句，只更新当前流程会改动的字段。
     */
    @Update("""
            UPDATE subscription
            SET end_date = #{endDate},
                status = #{status},
                cancelled_at = #{cancelledAt},
                updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    int updateById(Subscription subscription);

    /**
     * 每天定时把已经过期、但状态仍停留在 ACTIVE 的订阅批量改成 EXPIRED。
     */
    @Update("""
            UPDATE subscription
            SET status = 'EXPIRED',
                updated_at = #{updatedAt}
            WHERE status = 'ACTIVE'
              AND end_date < #{today}
            """)
    int expireActiveSubscriptions(
            @Param("today") LocalDate today,
            @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * “我的订阅”列表只统计当前租赁用户自己的订阅记录。
     */
    @Select("""
            SELECT COUNT(1)
            FROM subscription
            WHERE tenant_user_id = #{tenantUserId}
            """)
    long countByTenantUserId(@Param("tenantUserId") Long tenantUserId);

    /**
     * “我的订阅”列表顺手带出资源标题和资源状态，前端就不需要逐条再查资源详情。
     */
    @Results(id = "mySubscriptionListItemResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "resourceId", column = "resource_id"),
            @Result(property = "resourceTitle", column = "resource_title"),
            @Result(property = "resourceStatus", column = "resource_status"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "status", column = "status")
    })
    @Select("""
            SELECT
                s.id,
                s.resource_id,
                r.title AS resource_title,
                r.status AS resource_status,
                s.start_date,
                s.end_date,
                s.status
            FROM subscription s
            LEFT JOIN resource r ON r.id = s.resource_id
            WHERE s.tenant_user_id = #{tenantUserId}
            ORDER BY s.created_at DESC, s.id DESC
            LIMIT #{offset}, #{pageSize}
            """)
    List<MySubscriptionListItemResponse> pageQueryByTenantUserId(
            @Param("tenantUserId") Long tenantUserId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);
}

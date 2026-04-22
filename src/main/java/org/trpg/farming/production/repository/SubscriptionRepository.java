package org.trpg.farming.production.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.trpg.farming.production.po.Subscription;

@Mapper
public interface SubscriptionRepository {

    /**
     * 查某个资源当前有没有还在生效的订阅。
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
}

package org.trpg.farming.sharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.trpg.farming.sharing.entity.SharingOrder;

/**
 * 共享订单的数据访问接口。
 */
public interface SharingOrderRepository extends JpaRepository<SharingOrder, Long>, JpaSpecificationExecutor<SharingOrder> {
}

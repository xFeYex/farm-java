package org.trpg.farming.sharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.trpg.farming.sharing.entity.SharingCoinLedger;

/**
 * 共享币流水的数据访问接口。
 */
public interface SharingCoinLedgerRepository extends JpaRepository<SharingCoinLedger, Long>, JpaSpecificationExecutor<SharingCoinLedger> {
}

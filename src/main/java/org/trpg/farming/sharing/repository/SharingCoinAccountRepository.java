package org.trpg.farming.sharing.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.trpg.farming.sharing.entity.SharingCoinAccount;

/**
 * 共享币账户的数据访问接口。
 */
public interface SharingCoinAccountRepository extends JpaRepository<SharingCoinAccount, Long>, JpaSpecificationExecutor<SharingCoinAccount> {

    /**
     * 按用户 ID 查询账户，便于按需创建账户。
     */
    Optional<SharingCoinAccount> findByUserId(Long userId);
}

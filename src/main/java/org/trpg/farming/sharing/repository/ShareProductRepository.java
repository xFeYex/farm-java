package org.trpg.farming.sharing.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.trpg.farming.sharing.entity.ShareProduct;

/**
 * 共享商品实体的数据访问接口。
 */
public interface ShareProductRepository extends JpaRepository<ShareProduct, Long>, JpaSpecificationExecutor<ShareProduct> {

    /**
     * 按来源收获批次查询商品，用于保证收获事件幂等。
     */
    Optional<ShareProduct> findBySourceHarvestBatchId(Long sourceHarvestBatchId);
}

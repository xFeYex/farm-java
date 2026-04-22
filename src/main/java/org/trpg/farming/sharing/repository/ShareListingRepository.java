package org.trpg.farming.sharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.trpg.farming.sharing.entity.ShareListing;

/**
 * 共享上架条目的数据访问接口。
 */
public interface ShareListingRepository extends JpaRepository<ShareListing, Long>, JpaSpecificationExecutor<ShareListing> {
}

package org.trpg.farming.sharing.service;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trpg.farming.sharing.common.BusinessException;
import org.trpg.farming.sharing.common.PageResult;
import org.trpg.farming.sharing.constant.SharingConstants;
import org.trpg.farming.sharing.dto.response.CoinAccountView;
import org.trpg.farming.sharing.entity.SharingCoinAccount;
import org.trpg.farming.sharing.entity.SharingCoinLedger;
import org.trpg.farming.sharing.repository.SharingCoinAccountRepository;
import org.trpg.farming.sharing.repository.SharingCoinLedgerRepository;

/**
 * 负责共享币余额和流水记录。
 */
@Service
public class SharingAccountService {

    private final SharingCoinAccountRepository sharingCoinAccountRepository;
    private final SharingCoinLedgerRepository sharingCoinLedgerRepository;

    public SharingAccountService(SharingCoinAccountRepository sharingCoinAccountRepository,
                                 SharingCoinLedgerRepository sharingCoinLedgerRepository) {
        this.sharingCoinAccountRepository = sharingCoinAccountRepository;
        this.sharingCoinLedgerRepository = sharingCoinLedgerRepository;
    }

    @Transactional
    public CoinAccountView getAccount(Long userId) {
        return toView(getOrCreateZeroAccount(userId));
    }

    @Transactional(readOnly = true)
    public PageResult<SharingCoinLedger> listLedgers(Long userId, int page, int pageSize) {
        var result = sharingCoinLedgerRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, PageRequest.of(Math.max(page - 1, 0), pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));
        return new PageResult<>(result.getContent(), result.getTotalElements(), page, pageSize);
    }

    /**
     * 为演示用户按需创建一个零余额账户。
     */
    @Transactional
    public SharingCoinAccount getOrCreateZeroAccount(Long userId) {
        SharingCoinAccount account = sharingCoinAccountRepository.findByUserId(userId).orElse(null);
        if (account != null) {
            return account;
        }
        SharingCoinAccount newAccount = new SharingCoinAccount();
        newAccount.setUserId(userId);
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setStatus(SharingConstants.ACCOUNT_ENABLED);
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setUpdatedAt(LocalDateTime.now());
        return sharingCoinAccountRepository.save(newAccount);
    }

    /**
     * 扣减余额，并在同一事务里写入扣减流水。
     */
    @Transactional
    public SharingCoinAccount debit(Long userId, BigDecimal amount, String bizType, Long bizId, String remark) {
        SharingCoinAccount account = getOrCreateZeroAccount(userId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Shared-coin balance is insufficient.");
        }
        account.setBalance(account.getBalance().subtract(amount));
        account.setUpdatedAt(LocalDateTime.now());
        sharingCoinAccountRepository.save(account);

        SharingCoinLedger ledger = new SharingCoinLedger();
        ledger.setUserId(userId);
        ledger.setChangeType(SharingConstants.CHANGE_TYPE_DEBIT);
        ledger.setChangeAmount(amount);
        ledger.setBalanceAfter(account.getBalance());
        ledger.setBizType(bizType);
        ledger.setBizId(bizId);
        ledger.setRemark(remark);
        ledger.setCreatedAt(LocalDateTime.now());
        sharingCoinLedgerRepository.save(ledger);
        return account;
    }

    /**
     * 增加余额，并在同一事务里写入入账流水。
     */
    @Transactional
    public SharingCoinAccount credit(Long userId, BigDecimal amount, String bizType, Long bizId, String remark) {
        SharingCoinAccount account = getOrCreateZeroAccount(userId);
        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedAt(LocalDateTime.now());
        sharingCoinAccountRepository.save(account);

        SharingCoinLedger ledger = new SharingCoinLedger();
        ledger.setUserId(userId);
        ledger.setChangeType(SharingConstants.CHANGE_TYPE_CREDIT);
        ledger.setChangeAmount(amount);
        ledger.setBalanceAfter(account.getBalance());
        ledger.setBizType(bizType);
        ledger.setBizId(bizId);
        ledger.setRemark(remark);
        ledger.setCreatedAt(LocalDateTime.now());
        sharingCoinLedgerRepository.save(ledger);
        return account;
    }

    private CoinAccountView toView(SharingCoinAccount account) {
        CoinAccountView view = new CoinAccountView();
        view.setId(account.getId());
        view.setUserId(account.getUserId());
        view.setBalance(account.getBalance());
        view.setStatus(account.getStatus());
        view.setCreatedAt(account.getCreatedAt());
        view.setUpdatedAt(account.getUpdatedAt());
        return view;
    }
}

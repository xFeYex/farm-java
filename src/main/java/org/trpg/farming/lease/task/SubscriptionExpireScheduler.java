package org.trpg.farming.lease.task;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.trpg.farming.lease.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class SubscriptionExpireScheduler {

    private final SubscriptionService subscriptionService;

    /**
     * 每天凌晨执行一次过期扫描。
     * 这里先按最小 MVP 方案处理，不做分布式锁和更细的调度配置。
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void expireSubscriptionsDaily() {
        subscriptionService.expireSubscriptions();
    }
}

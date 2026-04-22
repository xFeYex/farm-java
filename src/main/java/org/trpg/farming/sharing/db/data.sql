INSERT IGNORE INTO sharing_coin_account (id, user_id, balance, status, created_at, updated_at) VALUES
(1, 40001, 1200.00, 'ENABLED', '2026-06-01 09:00:00', '2026-06-01 09:00:00'),
(2, 50001, 800.00, 'ENABLED', '2026-06-01 09:00:00', '2026-06-01 09:00:00'),
(3, 60001, 300.00, 'ENABLED', '2026-06-01 09:00:00', '2026-06-01 09:00:00');

INSERT INTO sharing_coin_ledger (user_id, change_type, change_amount, balance_after, biz_type, biz_id, remark, created_at)
SELECT 40001, 'CREDIT', 1200.00, 1200.00, 'INIT_GRANT', NULL, '初始化共享币', '2026-06-01 09:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM sharing_coin_ledger WHERE user_id = 40001 AND biz_type = 'INIT_GRANT'
);

INSERT INTO sharing_coin_ledger (user_id, change_type, change_amount, balance_after, biz_type, biz_id, remark, created_at)
SELECT 50001, 'CREDIT', 800.00, 800.00, 'INIT_GRANT', NULL, '初始化共享币', '2026-06-01 09:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM sharing_coin_ledger WHERE user_id = 50001 AND biz_type = 'INIT_GRANT'
);

INSERT INTO sharing_coin_ledger (user_id, change_type, change_amount, balance_after, biz_type, biz_id, remark, created_at)
SELECT 60001, 'CREDIT', 300.00, 300.00, 'INIT_GRANT', NULL, '初始化共享币', '2026-06-01 09:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM sharing_coin_ledger WHERE user_id = 60001 AND biz_type = 'INIT_GRANT'
);

INSERT INTO sharing_product (id, source_harvest_batch_id, owner_user_id, product_name, origin_facility_id, quantity, unit, quality_level, trace_code, trace_snapshot_json, status, created_at)
SELECT
    1,
    50001,
    40001,
    '番茄',
    30001,
    480.00,
    'kg',
    'A',
    'TRACE-20260601-001',
    '{"eventName":"HarvestBatchConfirmed","batchId":50001,"facilityId":30001,"ownerUserId":40001,"productName":"番茄","quantity":480,"unit":"kg","qualityLevel":"A","harvestedAt":"2026-06-01 10:00:00","traceCode":"TRACE-20260601-001"}',
    'READY',
    '2026-06-01 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM sharing_product WHERE source_harvest_batch_id = 50001);

INSERT INTO sharing_product (id, source_harvest_batch_id, owner_user_id, product_name, origin_facility_id, quantity, unit, quality_level, trace_code, trace_snapshot_json, status, created_at)
SELECT
    2,
    50002,
    40001,
    '黄瓜',
    30002,
    260.00,
    'kg',
    'B',
    'TRACE-20260602-001',
    '{"eventName":"HarvestBatchConfirmed","batchId":50002,"facilityId":30002,"ownerUserId":40001,"productName":"黄瓜","quantity":260,"unit":"kg","qualityLevel":"B","harvestedAt":"2026-06-02 09:00:00","traceCode":"TRACE-20260602-001"}',
    'READY',
    '2026-06-02 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM sharing_product WHERE source_harvest_batch_id = 50002);

INSERT INTO sharing_listing (id, share_product_id, title, coin_price, stock, status, published_at, expired_at, created_at)
SELECT
    1,
    1,
    '大棚A番茄 500g/份',
    10.00,
    30,
    'PUBLISHED',
    '2026-06-02 12:00:00',
    '2026-12-31 23:59:59',
    '2026-06-02 12:00:00'
WHERE NOT EXISTS (SELECT 1 FROM sharing_listing WHERE id = 1);

INSERT INTO sharing_listing (id, share_product_id, title, coin_price, stock, status, published_at, expired_at, created_at)
SELECT
    2,
    2,
    '黄瓜轻食装 1kg/份',
    16.00,
    15,
    'PUBLISHED',
    '2026-06-03 12:00:00',
    '2026-12-31 23:59:59',
    '2026-06-03 12:00:00'
WHERE NOT EXISTS (SELECT 1 FROM sharing_listing WHERE id = 2);

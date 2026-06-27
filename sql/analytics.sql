-- 胜率（Win Rate）
-- 胜率 = 盈利交易数 / 总交易数
SELECT
    CAST(SUM(CASE WHEN pnl > 0 THEN 1 ELSE 0 END) AS FLOAT)
    / COUNT(*) AS win_rate
FROM trades;

-- 平均 R（Avg R）
-- 平均R = 所有R的平均值
SELECT
    AVG(r_multiple) AS avg_r
FROM trades;

-- 总盈利（Total PnL）
SELECT
    COUNT(*) AS total_trades,

    SUM(CASE WHEN pnl > 0 THEN 1 ELSE 0 END) * 1.0 / COUNT(*) AS win_rate,

    AVG(r_multiple) AS avg_r,

    SUM(pnl) AS total_pnl

FROM trades;

-- 三大指标一起查
SELECT
    date(entry_time / 1000, 'unixepoch', 'localtime') AS day,

    COUNT(*) AS trades,
    SUM(pnl) AS pnl

FROM trades
GROUP BY day
ORDER BY day;
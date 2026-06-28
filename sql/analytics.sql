-- SQL 分析模板库，基本就能比较全面地评价一个交易策略的收益能力和风险水平
-- 总收益（Net Profit）：最终赚了多少钱。
-- 胜率（Win Rate）：盈利交易占比。
-- 平均 R（Average R）：每笔交易的质量。
-- 最大回撤（Maximum Drawdown）：历史上最大的资金回落。
-- 最长连亏（Max Losing Streak）：连续亏损最多有多少笔。
-- Profit Factor ：每亏1美元，能赚多少美元回来
-- 这些指标能完整回答：
-- 指标                     对应问题
-- Net Profit              我长期赚钱吗？
-- Average R               我交易质量高吗？
-- Profit Factor           我靠运气吗？
-- Max Drawdown            我能承受最坏情况吗？
-- Losing Streak           最近是不是状态崩了？
-- Daily Trades            我是不是过度交易？

-- 胜率（Win Rate）
SELECT
    COUNT(*) AS total_trades,
    SUM(CASE WHEN pnl > 0 THEN 1 ELSE 0 END) AS wins,
    SUM(CASE WHEN pnl <= 0 THEN 1 ELSE 0 END) AS losses,
    ROUND(
        100.0 * SUM(CASE WHEN pnl > 0 THEN 1 ELSE 0 END) / COUNT(*),
        2
    ) AS win_rate
FROM trades
WHERE status = 'CLOSED';

-- 平均R / 总R / Expectancy
SELECT
    COUNT(*) AS trades,
    ROUND(SUM(r_multiple) / 10000.0, 4) AS total_r,
    ROUND(AVG(r_multiple) / 10000.0, 4) AS avg_r,
    ROUND(MAX(r_multiple) / 10000.0, 4) AS best_r,
    ROUND(MIN(r_multiple) / 10000.0, 4) AS worst_r
FROM trades
WHERE status = 'CLOSED';

-- 盈亏统计（PnL）
SELECT
    ROUND(SUM(pnl) / 10000.0, 4) AS net_profit,
    ROUND(AVG(pnl) / 10000.0, 4) AS avg_trade,
    ROUND(MAX(pnl) / 10000.0, 4) AS best_trade,
    ROUND(MIN(pnl) / 10000.0, 4) AS worst_trade
FROM trades
WHERE status = 'CLOSED';

-- 最大回撤（核心）
-- 思路：累计权益曲线 + 峰值回撤
WITH equity AS (
    SELECT
        id,
        exit_time,
        pnl,
        SUM(pnl) OVER (ORDER BY exit_time) AS equity_curve
    FROM trades
    WHERE status = 'CLOSED'
),
drawdown AS (
    SELECT
        *,
        MAX(equity_curve) OVER (ORDER BY exit_time) AS peak_equity,
        equity_curve - MAX(equity_curve) OVER (ORDER BY exit_time) AS dd
    FROM equity
)
SELECT
    ROUND(MIN(dd) / 10000.0, 4) AS max_drawdown
FROM drawdown;

-- 连胜 / 连亏（非常关键）
WITH labeled AS (
    SELECT
        *,
        CASE WHEN pnl > 0 THEN 1 ELSE 0 END AS is_win
    FROM trades
    WHERE status = 'CLOSED'
),
grp AS (
    SELECT
        *,
        ROW_NUMBER() OVER (ORDER BY exit_time)
        - ROW_NUMBER() OVER (PARTITION BY is_win ORDER BY exit_time) AS grp_id
    FROM labeled
)
SELECT
    is_win,
    COUNT(*) AS streak_length
FROM grp
GROUP BY grp_id, is_win
ORDER BY streak_length DESC;

-- 日内表现（按天 PnL）
SELECT
    DATE(exit_time / 1000, 'unixepoch', 'localtime') AS trade_date,
    COUNT(*) AS trades,
    ROUND(SUM(pnl) / 10000.0, 4) AS daily_pnl,
    ROUND(SUM(r_multiple) / 10000.0, 4) AS daily_r,
    ROUND(AVG(pnl) / 10000.0, 4) AS avg_trade
FROM trades
WHERE status = 'CLOSED'
GROUP BY DATE(exit_time / 1000, 'unixepoch', 'localtime')
ORDER BY trade_date;

-- 每天胜率
SELECT
    DATE(exit_time / 1000, 'unixepoch', 'localtime') AS trade_date,
    COUNT(*) AS trades,
    SUM(CASE WHEN pnl > 0 THEN 1 ELSE 0 END) AS wins,
    ROUND(
        100.0 * SUM(CASE WHEN pnl > 0 THEN 1 ELSE 0 END) / COUNT(*),
        2
    ) AS win_rate
FROM trades
WHERE status = 'CLOSED'
GROUP BY DATE(exit_time / 1000, 'unixepoch', 'localtime')
ORDER BY trade_date;

-- 交易频率（防过度交易）
SELECT
    DATE(exit_time / 1000, 'unixepoch', 'localtime') AS day,
    COUNT(*) AS trades
FROM trades
WHERE status = 'CLOSED'
GROUP BY DATE(exit_time / 1000, 'unixepoch', 'localtime')
ORDER BY day;

-- 质量评分（简单系统）
SELECT
    ROUND(AVG(r_multiple) / 10000.0, 4) AS avg_r,
    SUM(CASE WHEN r_multiple >= 20000 THEN 1 ELSE 0 END) AS big_wins,
    SUM(CASE WHEN r_multiple <= -10000 THEN 1 ELSE 0 END) AS big_losses
FROM trades
WHERE status = 'CLOSED';

-- Profit Factor（盈利因子）
-- Profit Factor = 总盈利 ÷ 总亏损
SELECT
    ROUND(SUM(CASE WHEN pnl > 0 THEN pnl ELSE 0 END) / 10000.0, 4) AS gross_profit,

    ROUND(ABS(SUM(CASE WHEN pnl < 0 THEN pnl ELSE 0 END)) / 10000.0, 4) AS gross_loss,

    ROUND(
        SUM(CASE WHEN pnl > 0 THEN pnl ELSE 0 END)
        /
        NULLIF(ABS(SUM(CASE WHEN pnl < 0 THEN pnl ELSE 0 END)), 0),
        2
    ) AS profit_factor
FROM trades
WHERE status = 'CLOSED';

-- 单笔交易收益率
-- (平仓价 - 开仓价) / 开仓价
SELECT
    id,
    symbol,

    ROUND(entry_price / 10000.0, 4) AS entry_price,
    ROUND(exit_price / 10000.0, 4) AS exit_price,

    ROUND(
        (exit_price - entry_price) * 1.0 / entry_price,
        6
    ) AS return_pct

FROM trades
WHERE status = 'CLOSED';
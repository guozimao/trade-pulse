/**
 * Trade（交易记录实体类）
 *
 * 用于记录一笔完整的交易，包括：
 * - 开仓信息（entry）
 * - 平仓信息（exit）
 * - 盈亏计算（pnl / R multiple）
 * - 策略标签（strategy）
 *
 * 该类主要用于交易统计系统（TradeJournal / Analytics Engine），
 * 不涉及下单逻辑，仅用于数据存储与分析。
 */
public class Trade {

    /**
     * 交易标的，例如：AAPL / NVDA / TSLA
     */
    public String symbol;

    /**
     * 开仓时间（时间戳，毫秒）
     */
    public long entryTime;

    /**
     * 开仓价格
     */
    public double entryPrice;

    /**
     * 止损价格
     * */
    public double stopPrice;

    /**
     * 平仓时间（时间戳，毫秒）
     */
    public long exitTime;

    /**
     * 平仓价格
     */
    public double exitPrice;

    /**
     * 交易数量（股数 / 合约数）
     */
    public int qty;

    /**
     * 盈亏（PnL）
     * 通常计算方式：
     * exitPrice - entryPrice（未考虑手续费）
     */
    public double pnl;

    /**
     * R 倍数（风险回报比）
     * 常用于衡量策略表现：
     * R = 盈亏 / 初始风险
     */
    public double rMultiple;

    /**
     * 策略标签
     * 例如：
     * - breakout
     * - VWAP pullback
     * - momentum
     */
    public String strategy;

    public String status;


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(long entryTime) {
        this.entryTime = entryTime;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public long getExitTime() {
        return exitTime;
    }

    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    public double getExitPrice() {
        return exitPrice;
    }

    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPnl() {
        return pnl;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public double getrMultiple() {
        return rMultiple;
    }

    public void setrMultiple(double rMultiple) {
        this.rMultiple = rMultiple;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public double getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(double stopPrice) {
        this.stopPrice = stopPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
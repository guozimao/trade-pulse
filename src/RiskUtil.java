public class RiskUtil {
    public static double calculateR(double pnl, double entryPrice, double exitPrice, double stopLossPrice) {

        double risk = Math.abs(entryPrice - stopLossPrice);

        if (risk == 0) return 0;

        return pnl / risk;
    }
}

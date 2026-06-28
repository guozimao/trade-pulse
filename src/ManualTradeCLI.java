import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class ManualTradeCLI {
    private static final Scanner sc = new Scanner(System.in);

    public static void start() {

        while (true) {

            System.out.println("\n===== MANUAL TRADE MENU =====");
            System.out.println("1. Add trade");
            System.out.println("2. Close trade manually");
            System.out.println("3. Add Position manually");
            System.out.println("4. Reduce Position manually");
            System.out.println("5. update stopPrice manually");
            System.out.print("Select: ");

            int choice = Integer.parseInt(sc.nextLine());

            try {
                if (choice == 1) addTrade();
                if (choice == 2) closeTrade();
                if (choice == 3) positionAdd();
                if (choice == 4) positionReduce();
                if (choice == 5) updateStopPrice();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void addTrade() throws Exception {

        Trade t = new Trade();

        System.out.print("Symbol: ");
        t.symbol = sc.nextLine();

        Trade trade = TradeRepository.getOpenTrade(t.symbol);

        if(trade != null){
            System.out.print(t.symbol + " is open in DB");
            return;
        }

        System.out.print("Entry Price: ");
        t.entryPrice = Double.parseDouble(sc.nextLine());

        System.out.print("Qty: ");
        t.qty = Integer.parseInt(sc.nextLine());

        System.out.print("Stop Price: ");
        t.stopPrice = Double.parseDouble(sc.nextLine());

        System.out.println("Entry Time (timestamp): ");
        System.out.println("1. now");
        System.out.println("2. manual (yyyy-MM-dd HH:mm:ss)");

        int mode = Integer.parseInt(sc.nextLine());

        if (mode == 1) {
            t.entryTime = System.currentTimeMillis();
        } else {
            String input = sc.nextLine();

            DateTimeFormatter fmt =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime ldt = LocalDateTime.parse(input, fmt);

            t.entryTime = ldt
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }

        t.strategy = "MANUAL";
        t.status = "OPEN";

        TradeRepository.save(t);

        System.out.println("✅ Trade inserted");
    }

    private static void closeTrade() throws Exception {

        System.out.print("Symbol: ");
        String symbol = sc.nextLine();

        Trade trade = TradeRepository.getOpenTrade(symbol);

        if(trade == null){
            System.out.print(symbol + " nothing in DB");
            return;
        }

        System.out.print("Exit Price: ");
        double exitPrice = Double.parseDouble(sc.nextLine());

        System.out.println("exit Time (timestamp): ");
        System.out.println("1. now");
        System.out.println("2. manual (yyyy-MM-dd HH:mm:ss)");

        int mode = Integer.parseInt(sc.nextLine());

        long exitTime = 0;
        if (mode == 1) {
            exitTime = System.currentTimeMillis();
        } else {
            String input = sc.nextLine();

            DateTimeFormatter fmt =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime ldt = LocalDateTime.parse(input, fmt);

            exitTime = ldt
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }

        int qty = trade.qty;

        double pnl = (exitPrice - trade.entryPrice) * qty;

        pnl = pnl + trade.pnl;

        double rMultiple = RiskUtil.calculateR(pnl, trade.entryPrice, trade.exitPrice, trade.stopPrice);

        TradeRepository.closeTrade(exitPrice, pnl, exitTime, rMultiple, trade.symbol);

        System.out.println("✅ Manual close saved");
    }

    public static void positionAdd() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Symbol: ");
        String symbol = sc.nextLine();

        Trade trade = TradeRepository.getOpenTrade(symbol);

        if(trade == null){
            System.out.print(symbol + " nothing in DB");
            return;
        }

        System.out.print("Qty: ");
        int qty = Integer.parseInt(sc.nextLine());

        System.out.print("Price: ");
        double price = Double.parseDouble(sc.nextLine());

        int newQty = trade.qty + qty;

        double avgPrice =
                (trade.entryPrice * trade.qty + price * qty)
                        / newQty;

        TradeRepository.addPosition(symbol, newQty, avgPrice);

        System.out.println("✅ Manual position Add");
    }

    public static void positionReduce() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Symbol: ");
        String symbol = sc.nextLine();

        Trade trade = TradeRepository.getOpenTrade(symbol);

        if(trade == null){
            System.out.print(symbol + " nothing in DB");
            return;
        }

        System.out.print("Qty: ");
        int qty = Integer.parseInt(sc.nextLine());

        if (qty >= trade.qty) {
            System.out.print(symbol + "!!! Qty >= Qty in DB");
            return;
        }

        System.out.print("Price: ");
        double price = Double.parseDouble(sc.nextLine());

        // 已实现PnL
        double newPnl = (price - trade.entryPrice) * qty;

        newPnl = newPnl + trade.pnl;

        int newQty = trade.qty - qty;

        TradeRepository.reducePosition(symbol, newQty, newPnl);

        System.out.println("✅ Manual position Reduce");
    }

    public static void updateStopPrice() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Symbol: ");
        String symbol = sc.nextLine();

        Trade trade = TradeRepository.getOpenTrade(symbol);

        if(trade == null){
            System.out.print(symbol + " nothing in DB");
            return;
        }

        System.out.print("StopPrice: ");
        double stopPrice = Double.parseDouble(sc.nextLine());

        TradeRepository.updateStopPrice(symbol, stopPrice);

        System.out.println("✅ Manual position Reduce");
    }

}

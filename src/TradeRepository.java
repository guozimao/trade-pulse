import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TradeRepository {
    public static void save(Trade t) throws Exception {

        Connection conn = DB.connect();

        String sql = """
            INSERT INTO trades
            (symbol, entry_time, entry_price, stop_price, qty, strategy, status, pnl)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, t.symbol);
        ps.setLong(2, t.entryTime);
        ps.setInt(3, (int) Math.round(t.entryPrice * 10000));
        ps.setInt(4, (int) Math.round(t.stopPrice * 10000));
        ps.setInt(5, t.qty);
        ps.setString(6, t.strategy);
        ps.setString(7, t.status);
        ps.setInt(8, 0);

        ps.executeUpdate();
        conn.close();
    }

    public static void closeTrade(double exitPrice, double pnl, long exitTime, double rMultiple, String symbol) throws Exception {

        String sql = """
            UPDATE trades
            SET exit_price = ?,
                pnl = ?,
                r_multiple = ?,
                exit_time = ?,
                status = 'CLOSED'
            WHERE symbol = ? AND status = 'OPEN'
        """;

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) Math.round(exitPrice * 10000));
            ps.setInt(2, (int) Math.round(pnl * 10000));
            ps.setInt(3, (int) Math.round(rMultiple * 10000));
            ps.setLong(4, exitTime);
            ps.setString(5, symbol);

            ps.executeUpdate();

            System.out.println("📉 CLOSED: " + symbol + " exit=" + exitPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Trade getOpenTrade(String symbol) {

        String sql = """
            SELECT *
            FROM trades
            WHERE symbol = ? AND status = 'OPEN'
            ORDER BY id DESC
            LIMIT 1
        """;

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, symbol);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Trade trade = new Trade();

                trade.setSymbol(rs.getString("symbol"));

                trade.setEntryTime(rs.getLong("entry_time"));

                trade.setEntryPrice(rs.getInt("entry_price") / 10000.0);

                trade.setStopPrice(rs.getInt("stop_price") / 10000.0);

                trade.setQty(rs.getInt("qty"));

                trade.setStatus(rs.getString("status"));

                trade.setExitPrice(rs.getInt("exit_price") / 10000.0);

                trade.setPnl(rs.getInt("pnl") / 10000.0);

                trade.setrMultiple(rs.getInt("r_multiple") / 10000.0);

                trade.setExitTime(rs.getLong("exit_time"));

                return trade;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void addPosition(String symbol, int newQty, double avgPrice) {
        String sql = """
            UPDATE trades
            SET entry_price = ?,
                qty = ?
            WHERE symbol = ? AND status = 'OPEN'
        """;

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) Math.round(avgPrice * 10000));
            ps.setInt(2, newQty);
            ps.setString(3, symbol);

            ps.executeUpdate();

            System.out.println("📉 ADD Position: " + symbol + " avgPrice=" + avgPrice + " newQty=" + newQty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reducePosition(String symbol, int newQty, double newPnl) {
        String sql = """
            UPDATE trades
            SET qty = ?,
                pnl = ?
            WHERE symbol = ? AND status = 'OPEN'
        """;

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newQty);
            ps.setInt(2, (int) Math.round(newPnl * 10000));
            ps.setString(3, symbol);

            ps.executeUpdate();

            System.out.println("📉 ADD Position: " + symbol + " newQty=" + newQty + " newPnl=" + newPnl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateStopPrice(String symbol, double stopPrice) {
        String sql = """
            UPDATE trades
            SET stop_price = ?
            WHERE symbol = ? AND status = 'OPEN'
        """;

        try (Connection conn = DB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) Math.round(stopPrice * 10000));
            ps.setString(2, symbol);

            ps.executeUpdate();

            System.out.println("📉 Update StopPrice: " + symbol + " stopPrice=" + stopPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

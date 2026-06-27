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
        ps.setDouble(3, t.entryPrice);
        ps.setDouble(4,t.stopPrice);
        ps.setInt(5, t.qty);
        ps.setString(6, t.strategy);
        ps.setString(7, t.status);
        ps.setDouble(8, 0);

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

            ps.setDouble(1, exitPrice);
            ps.setDouble(2, pnl);
            ps.setDouble(3, rMultiple);
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

                trade.setEntryPrice(rs.getDouble("entry_price"));

                trade.setStopPrice(rs.getDouble("stop_price"));

                trade.setQty(rs.getInt("qty"));

                trade.setStatus(rs.getString("status"));

                trade.setExitPrice(rs.getDouble("exit_price"));

                trade.setPnl(rs.getDouble("pnl"));

                trade.setrMultiple(rs.getDouble("r_multiple"));

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

            ps.setDouble(1, avgPrice);
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
            ps.setDouble(2, newPnl);
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

            ps.setDouble(1, stopPrice);
            ps.setString(2, symbol);

            ps.executeUpdate();

            System.out.println("📉 Update StopPrice: " + symbol + " stopPrice=" + stopPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

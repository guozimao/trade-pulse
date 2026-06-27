import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Analytics {
    public static void printSummary() throws Exception {

        Connection conn = DriverManager.getConnection("jdbc:sqlite:data/trade.db");

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM trades");

        int total = 0;
        int win = 0;

        double totalR = 0;

        while (rs.next()) {

            double pnl = rs.getDouble("pnl");
            double r = rs.getDouble("r_multiple");

            total++;
            totalR += r;

            if (pnl > 0) {
                win++;
            }
        }

        double winRate = total == 0 ? 0 : (win * 100.0 / total);
        double avgR = total == 0 ? 0 : totalR / total;

        System.out.println("===== STRATEGY REPORT =====");
        System.out.println("Trades: " + total);
        System.out.println("Win rate: " + winRate + "%");
        System.out.println("Avg R: " + avgR);
    }
}

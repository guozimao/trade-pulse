import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    private static final String URL = "jdbc:sqlite:data/trade.db";

    public static Connection connect() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void init() throws Exception {
        Connection conn = connect();

        String sql = """
            CREATE TABLE IF NOT EXISTS trades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                symbol TEXT,
                entry_time INTEGER,
                entry_price INTEGER,
                stop_price INTEGER,
                exit_time INTEGER,
                exit_price INTEGER,
                qty INTEGER,
                pnl INTEGER,
                r_multiple INTEGER,
                strategy TEXT,
                status TEXT
            );
        """;

        conn.createStatement().execute(sql);
        conn.close();
    }
}

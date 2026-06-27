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
                entry_price REAL,
                stop_price REAL,
                exit_time INTEGER,
                exit_price REAL,
                qty INTEGER,
                pnl REAL,
                r_multiple REAL,
                strategy TEXT,
                status TEXT
            );
        """;

        conn.createStatement().execute(sql);
        conn.close();
    }
}

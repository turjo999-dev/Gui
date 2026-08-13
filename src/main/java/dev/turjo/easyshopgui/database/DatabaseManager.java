package dev.turjo.easyshopgui.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.turjo.easyshopgui.EasyShopGUI;
import dev.turjo.easyshopgui.utils.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the plugin's persistent storage.
 *
 * Supports SQLITE (default, file-based, no external server required - config.yml's
 * database.type) and MYSQL (for admins who already run one). Connections are pooled
 * through HikariCP, which was already a shaded dependency in this project but was
 * previously unused - initialize()/closeConnections() were empty TODO stubs, so no
 * plugin data (transaction history, cheque audit totals) ever survived a restart
 * despite the dependency footprint suggesting otherwise.
 */
public class DatabaseManager {

    private final EasyShopGUI plugin;
    private HikariDataSource dataSource;
    private DatabaseType type;

    public enum DatabaseType {
        SQLITE, MYSQL
    }

    public DatabaseManager(EasyShopGUI plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the database connection pool and create tables if they don't exist.
     * @return true if successful, false otherwise
     */
    public boolean initialize() {
        try {
            String configuredType = plugin.getConfigManager().getMainConfig()
                    .getString("database.type", "SQLITE").toUpperCase();

            try {
                type = DatabaseType.valueOf(configuredType);
            } catch (IllegalArgumentException e) {
                Logger.warn("Unknown database.type '" + configuredType + "', defaulting to SQLITE");
                type = DatabaseType.SQLITE;
            }

            HikariConfig hikariConfig = new HikariConfig();

            if (type == DatabaseType.MYSQL) {
                String host = plugin.getConfigManager().getMainConfig().getString("database.mysql.host", "localhost");
                int port = plugin.getConfigManager().getMainConfig().getInt("database.mysql.port", 3306);
                String database = plugin.getConfigManager().getMainConfig().getString("database.mysql.database", "easyshopgui");
                String username = plugin.getConfigManager().getMainConfig().getString("database.mysql.username", "root");
                String password = plugin.getConfigManager().getMainConfig().getString("database.mysql.password", "");

                hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database +
                        "?useSSL=false&autoReconnect=true&characterEncoding=utf8");
                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);
                hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");

                Logger.info("Connecting to MySQL database at " + host + ":" + port + "/" + database);
            } else {
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }
                File dbFile = new File(dataFolder, "easyshopgui.db");
                hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
                hikariConfig.setDriverClassName("org.sqlite.JDBC");
                // SQLite only supports a single writer at a time - a pool larger than 1
                // just means threads queue on SQLite's own locking rather than
                // HikariCP's, so keep the pool intentionally small here.
                hikariConfig.setMaximumPoolSize(1);

                Logger.info("Using SQLite database at " + dbFile.getAbsolutePath());
            }

            int maxPoolSize = plugin.getConfigManager().getMainConfig().getInt("database.mysql.pool.maximum-pool-size", 10);
            int minIdle = plugin.getConfigManager().getMainConfig().getInt("database.mysql.pool.minimum-idle", 5);
            long connectionTimeout = plugin.getConfigManager().getMainConfig().getLong("database.mysql.pool.connection-timeout", 30000);
            long idleTimeout = plugin.getConfigManager().getMainConfig().getLong("database.mysql.pool.idle-timeout", 600000);
            long maxLifetime = plugin.getConfigManager().getMainConfig().getLong("database.mysql.pool.max-lifetime", 1800000);

            if (type == DatabaseType.MYSQL) {
                hikariConfig.setMaximumPoolSize(maxPoolSize);
                hikariConfig.setMinimumIdle(minIdle);
            }
            hikariConfig.setConnectionTimeout(connectionTimeout);
            hikariConfig.setIdleTimeout(idleTimeout);
            hikariConfig.setMaxLifetime(maxLifetime);
            hikariConfig.setPoolName("EasyShopGUI-Pool");

            dataSource = new HikariDataSource(hikariConfig);

            createTables();

            Logger.info("Database initialized successfully (" + type + ")");
            return true;

        } catch (Exception e) {
            Logger.error("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create the tables this plugin needs if they don't already exist.
     */
    private void createTables() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS eshop_transactions (" +
                    "id INTEGER PRIMARY KEY " + (type == DatabaseType.MYSQL ? "AUTO_INCREMENT" : "AUTOINCREMENT") + ", " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "player_name VARCHAR(16) NOT NULL, " +
                    "transaction_type VARCHAR(10) NOT NULL, " +
                    "item_name VARCHAR(64) NOT NULL, " +
                    "amount INTEGER NOT NULL, " +
                    "price DOUBLE NOT NULL, " +
                    "timestamp BIGINT NOT NULL" +
                    ")");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_eshop_tx_player ON eshop_transactions(player_uuid)");

            stmt.execute("CREATE TABLE IF NOT EXISTS eshop_cheque_stats (" +
                    "id INTEGER PRIMARY KEY, " +
                    "total_issued DOUBLE NOT NULL DEFAULT 0, " +
                    "total_redeemed DOUBLE NOT NULL DEFAULT 0, " +
                    "cheques_issued_count INTEGER NOT NULL DEFAULT 0, " +
                    "cheques_redeemed_count INTEGER NOT NULL DEFAULT 0" +
                    ")");

            // Ensure the single stats row exists (id=1 is the only row this table ever has)
            try (Statement checkStmt = conn.createStatement()) {
                var rs = checkStmt.executeQuery("SELECT COUNT(*) FROM eshop_cheque_stats WHERE id = 1");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO eshop_cheque_stats (id, total_issued, total_redeemed, cheques_issued_count, cheques_redeemed_count) " +
                            "VALUES (1, 0, 0, 0, 0)");
                }
            }
        }
    }

    /**
     * Get a pooled connection. Caller is responsible for closing it (use try-with-resources).
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not initialized");
        }
        return dataSource.getConnection();
    }

    public DatabaseType getType() {
        return type;
    }

    public boolean isInitialized() {
        return dataSource != null && !dataSource.isClosed();
    }

    /**
     * Close all pooled connections. Safe to call even if initialize() was never
     * called or failed.
     */
    public void closeConnections() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            Logger.info("Database connections closed");
        }
    }
}

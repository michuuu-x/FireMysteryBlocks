package cz.devfire.mysteryblocks.Database.Type;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Interface.Database;
import cz.devfire.mysteryblocks.Database.Object.Results;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHikariCP extends Database {
    private final HikariDataSource ds;

    private final String database;
    private final String host;
    private final String user;
    private final String password;
    private final int port;

    public DatabaseHikariCP(String database, String host, String user, String password) {
        this(database, host, user, password,3306,null);
    }

    public DatabaseHikariCP(String database, String host, String user, String password, int port) {
        this(database, host, user, password, port,null);
    }

    public DatabaseHikariCP(String database, String host, String user, String password, int port, HikariConfig config) {
        this.database = database;
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;

        if (config == null) {
            config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName(Utils.getServerVersionID() >= 17 ? "com.mysql.cj.jdbc.Driver" : "com.mysql.jdbc.Driver");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.setMaximumPoolSize(10);
            config.setIdleTimeout(30000);
        }

        ds = new HikariDataSource(config);
    }

    public DatabaseHikariCP(ConfigurationSection section) {
        this.database = section.getString("Login.DB");
        this.host = section.getString("Login.Host");
        this.user = section.getString("Login.User");
        this.password = section.getString("Login.Pass");
        this.port = section.getInt("Login.Port");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName(Utils.getServerVersionID() >= 17 ? "com.mysql.cj.jdbc.Driver" : "com.mysql.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.setMaximumPoolSize(section.getInt("Hikari.MaximumPoolSize"));
        config.setKeepaliveTime(section.getInt("Hikari.KeepAliveMs"));

        ds = new HikariDataSource(config);
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.MYSQL;
    }

    public HikariDataSource getSource() {
        return ds;
    }

    @Override
    public boolean connect() {
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1")) {
                ps.executeQuery();
            }

            Bukkit.getConsoleSender().sendMessage(Utils.cc("&e - Connecting database &6" + host + "&e... &aSuccessful!"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Utils.cc("&e - Cannot connect to &6" + host + "&e! &cError: " + e.getMessage()));
            e.printStackTrace();
            return false;
        }

        return ds.isRunning();
    }

    @Override
    public boolean disconnect() {
        try {
            ds.close();

            Bukkit.getConsoleSender().sendMessage(Utils.cc("&e - Disconnecting database &6" + host + "&e... &aSuccessful!"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Utils.cc("&e - Cannot disconnect from &6" + host + "&e! &cError: " + e.getMessage()));
            return false;
        }

        return ds.isClosed();
    }

    @Override
    public boolean isConnected() {
        return ds.isRunning();
    }

    @Override
    public void update(String query, Object... args) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(query);) {
            parseStatement(ps, args);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (isIgnoreErrors()) return;

            Bukkit.getConsoleSender().sendMessage("[FireMysteryBlocks-Database]");
            Bukkit.getConsoleSender().sendMessage("§c - Update failed! §4" + query);
            Bukkit.getConsoleSender().sendMessage("§c - Error: " + e.getMessage());
        }
    }

    @Override
    public Results query(String query, Object... args) {
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(query);) {
            parseStatement(ps, args);

            try (ResultSet rs = ps.executeQuery();) {
                return new Results(rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            if (isIgnoreErrors()) return null;

            Bukkit.getConsoleSender().sendMessage("[FireMysteryBlocks-Database]");
            Bukkit.getConsoleSender().sendMessage("§c - Query failed! §4" + query);
            Bukkit.getConsoleSender().sendMessage("§c - Error: " + e.getMessage());
        }

        return null;
    }
}

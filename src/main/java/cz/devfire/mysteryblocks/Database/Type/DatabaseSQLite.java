package cz.devfire.mysteryblocks.Database.Type;

import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Interface.Database;
import cz.devfire.mysteryblocks.Database.Object.QueryResult;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSQLite extends Database {
    private Connection conn;

    private final File file;
    private final String fileName;

    public DatabaseSQLite(String path) {
        this.file = new File(path);
        this.fileName = file.getName();
    }

    public DatabaseType getType() {
        return DatabaseType.SQLITE;
    }

    public boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:plugins/FireMysteryBlocks/" + file.getPath());

            Bukkit.getConsoleSender().sendMessage(Utils.cc("&e - Connecting sql database &6" + fileName + "&e... &aSuccessful!"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Utils.cc("&e - Cannot connect to &6" + fileName + "&e! &cError: " + e.getMessage()));
            return false;
        }

        return true;
    }

    public boolean disconnect() {
        try {
            conn.close();

            Bukkit.getConsoleSender().sendMessage(Utils.cc("§e - Disconnecting sql database §6" + fileName + "§e... §aSuccessful!"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Utils.cc("§e - Cannot disconnect from §6" + fileName + "§e! §cError: " + e.getMessage()));
            return false;
        }

        return true;
    }

    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String query, Object... args) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            parseStatement(ps, args);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (isIgnoreErrors()) return;

            Bukkit.getConsoleSender().sendMessage("[FireMysteryBlocks-Database]");
            Bukkit.getConsoleSender().sendMessage("§c - Update failed! §4" + query);
            Bukkit.getConsoleSender().sendMessage("§c - Error: " + e.getMessage());
        }
    }

    public QueryResult query(String query, Object... args) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            parseStatement(ps, args);
            return new QueryResult(ps.executeQuery());
        } catch (SQLException e) {
            if (isIgnoreErrors()) return null;

            Bukkit.getConsoleSender().sendMessage("[FireMysteryBlocks-Database]");
            Bukkit.getConsoleSender().sendMessage("§c - Query failed! §4" + query);
            Bukkit.getConsoleSender().sendMessage("§c - Error: " + e.getMessage());
        }

        return null;
    }
}

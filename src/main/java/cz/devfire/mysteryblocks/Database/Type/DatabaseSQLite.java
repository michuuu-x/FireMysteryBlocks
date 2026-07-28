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

            Bukkit.getConsoleSender().sendMessage(Utils.mm("<yellow> - Connecting sql database <gold>" + fileName + "<yellow>... <color:#05fa11>Successful!"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Utils.mm("<yellow> - Cannot connect to <gold>" + fileName + "<yellow>! <color:#f01f1f>Error: " + e.getMessage()));
            return false;
        }

        return true;
    }

    public boolean disconnect() {
        try {
            conn.close();

            Bukkit.getConsoleSender().sendMessage(Utils.mm("<yellow> - Disconnecting sql database <gold>" + fileName + "<yellow>... <color:#05fa11>Successful!"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Utils.mm("<yellow> - Cannot disconnect from <gold>" + fileName + "<yellow>! <color:#f01f1f>Error: " + e.getMessage()));
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

            Bukkit.getConsoleSender().sendMessage(Utils.mm("[FireMysteryBlocks-Database]"));
            Bukkit.getConsoleSender().sendMessage(Utils.mm("<color:#f01f1f> - Update failed! <dark_red>" + query));
            Bukkit.getConsoleSender().sendMessage(Utils.mm("<color:#f01f1f> - Error: " + e.getMessage()));
        }
    }

    public QueryResult query(String query, Object... args) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            parseStatement(ps, args);
            return new QueryResult(ps.executeQuery());
        } catch (SQLException e) {
            if (isIgnoreErrors()) return null;

            Bukkit.getConsoleSender().sendMessage(Utils.mm("[FireMysteryBlocks-Database]"));
            Bukkit.getConsoleSender().sendMessage(Utils.mm("<color:#f01f1f> - Query failed! <dark_red>" + query));
            Bukkit.getConsoleSender().sendMessage(Utils.mm("<color:#f01f1f> - Error: " + e.getMessage()));
        }

        return null;
    }
}

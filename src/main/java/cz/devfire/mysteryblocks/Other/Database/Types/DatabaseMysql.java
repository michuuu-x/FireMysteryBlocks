package cz.devfire.mysteryblocks.Other.Database.Types;

import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Other.Database.Database;
import cz.devfire.mysteryblocks.api.Other.Database.Types.DatabaseType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseMysql implements Database {
    private final Plugin plugin;

    private final String db;
    private final String host;
    private final String user;
    private final String pass;
    private final int port;

    private Connection conn;

    public DatabaseMysql(Plugin plugin, String db, String host, String user, String pass) {
        this(plugin, db, host, user, pass, 3306);
    }

    public DatabaseMysql(Plugin plugin, String db, String host, String user, String pass, int port) {
        this.plugin = plugin;
        this.db = db;
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.port = port;
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.MYSQL;
    }

    public Boolean connect() {
        try {
            if (Utils.getServerVersionID() >= 17) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else {
                Class.forName("com.mysql.jdbc.Driver");
            }

            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8", user, pass);

            Bukkit.getConsoleSender().sendMessage("§e - Connecting database §6" + host + "§e... §aSuccessful!");
            return true;
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§e - Cannot connect to §6" + host + "§e! §cError: " + e.getMessage());
            return false;
        }
    }

    public Boolean disconnect() {
        try {
            conn.close();

            Bukkit.getConsoleSender().sendMessage("§e - Disconnecting database §6" + host + "§e... §aSuccessful!");
            return true;
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§e - Cannot disconnect from §6" + host + "§e! §cError: " + e.getMessage());
            return false;
        }
    }

    public Boolean isConnected() {
        Boolean is;

        if (conn == null) {
            is = false;
        } else {
            try {
                is = !conn.isClosed();
            } catch (Exception e) {
                is = false;
            }
        }

        return is;
    }

    public Connection getConnection() {
        return conn;
    }

    public void update(String query) {
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.executeUpdate(query);
        } catch (Exception e) {
            plugin.getLogger().info(" ");
            Bukkit.getConsoleSender().sendMessage("§c - Query failed! §4" + query);
            Bukkit.getConsoleSender().sendMessage("§c - Error: " + e.getMessage());
        }
    }

    public ResultSet query(String query) {
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            plugin.getLogger().info(" ");
            Bukkit.getConsoleSender().sendMessage("§c - Query failed! §4" + query);
            Bukkit.getConsoleSender().sendMessage("§c - Error: " + e.getMessage());
        }

        return null;
    }
}

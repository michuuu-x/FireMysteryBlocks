package cz.devfire.mysteryblocks.Database.Type;

import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Interface.Database;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseMySQL implements Database {
    private Connection conn;

    private final String database;
    private final String host;
    private final String user;
    private final String password;
    private final int port;

    public DatabaseMySQL(String database, String host, String user, String password) {
        this(database, host, user, password,3306);
    }

    public DatabaseMySQL(String database, String host, String user, String password, int port) {
        this.database = database;
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public DatabaseMySQL(ConfigurationSection section) {
        this.database = section.getString("Login.DB");
        this.host = section.getString("Login.Host");
        this.user = section.getString("Login.User");
        this.password = section.getString("Login.Pass");
        this.port = section.getInt("Login.Port");
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.MYSQL;
    }

    @Override
    public boolean connect() {
        try {
            if (Utils.getServerVersionID() >= 17) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else {
                Class.forName("com.mysql.jdbc.Driver");
            }

            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8", user, password);

            Bukkit.getConsoleSender().sendMessage("§e - Connecting database §6" + host + "§e... §aSuccessful!");
            return true;
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§e - Cannot connect to §6" + host + "§e! §cError: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean disconnect() {
        try {
            conn.close();
            Bukkit.getConsoleSender().sendMessage(Utils.cc("&e - Disconnecting database &6" + host + "&e... &aSuccessful!"));

            return conn.isClosed();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Utils.cc("&e - Cannot disconnect from &6" + host + "&e! &cError: " + e.getMessage()));
            return false;
        }

    }

    @Override
    public boolean isConnected() {
        boolean is;

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
}

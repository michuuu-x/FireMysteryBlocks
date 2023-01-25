package cz.devfire.mysteryblocks.Database.Interface;

import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Object.Results;
import org.bukkit.Bukkit;

import java.sql.*;

public interface Database {
    Connection conn = null;

    DatabaseType getType();

    boolean connect();

    boolean disconnect();

    boolean isConnected();

    default void update(String query, Object... args) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            parseStatement(ps, args);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage("§c - Update failed! §4" + query);
            Bukkit.getConsoleSender().sendMessage("§c - Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    default Results query(String query, Object... args) {
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            parseStatement(ps, args);
            return new Results(ps.executeQuery());
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage("§c - Query failed! §4" + query);
            Bukkit.getConsoleSender().sendMessage("§c - Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    default void parseStatement(PreparedStatement ps, Object[] args) throws SQLException {
        int i = 1;

        for (Object arg : args) {
            if (arg instanceof Integer) {
                ps.setInt(i, (int) arg);
            } else if (arg instanceof String) {
                ps.setString(i, (String) arg);
            } else if (arg instanceof Long) {
                ps.setLong(i, (long) arg);
            } else if (arg instanceof Double) {
                ps.setDouble(i, (double) arg);
            } else if (arg instanceof Boolean) {
                ps.setBoolean(i, (boolean) arg);
            } else if (arg instanceof Float) {
                ps.setFloat(i, (float) arg);
            } else if (arg instanceof Date) {
                ps.setDate(i, (Date) arg);
            }

            i++;
        }
    }
}

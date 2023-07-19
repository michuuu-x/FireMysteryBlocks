package cz.devfire.mysteryblocks.Database.Interface;

import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Object.Results;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Database {

    private boolean ignoreErrors = false;

    public boolean isIgnoreErrors() {
        return ignoreErrors;
    }

    public void setIgnoreErrors(boolean bool) {
        this.ignoreErrors = bool;
    }

    public abstract DatabaseType getType();

    public abstract boolean connect();

    public abstract boolean disconnect();

    public abstract boolean isConnected();

    public abstract void update(String query, Object... args);

    public abstract Results query(String query, Object... args);

    public void parseStatement(PreparedStatement ps, Object[] args) throws SQLException {
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

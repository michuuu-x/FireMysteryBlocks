package cz.devfire.mysteryblocks.Database.Interface;

import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;

import java.sql.ResultSet;

public interface Database {
    DatabaseType getType();

    boolean connect();

    boolean disconnect();

    boolean isConnected();

    void update(String query, Object... args);

    ResultSet query(String query, Object... args);
}

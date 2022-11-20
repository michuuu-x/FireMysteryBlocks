package cz.devfire.mysteryblocks.api.Other.Database;

import cz.devfire.mysteryblocks.api.Other.Database.Types.DatabaseType;

import java.sql.Connection;
import java.sql.ResultSet;

public interface Database {

    /**
     * @return
     */
    Boolean connect();

    /**
     * @return
     */
    Boolean disconnect();

    /**
     * @return
     */
    Boolean isConnected();

    /**
     * @return
     */
    Connection getConnection();

    /**
     * @param query
     */
    void update(String query);

    /**
     * @param query
     * @return
     */
    ResultSet query(String query);

    /**
     * @return
     */
    DatabaseType getType();

}

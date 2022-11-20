package cz.devfire.mysteryblocks.Other.Database;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Database.Types.DatabaseMysql;
import cz.devfire.mysteryblocks.Other.Database.Types.DatabaseSqlite;
import cz.devfire.mysteryblocks.api.Other.Database.Database;
import cz.devfire.mysteryblocks.api.Other.Database.Types.DatabaseType;

import java.io.File;

public class DatabaseHandler {
    private final MysteryBlocksPluginImpl plugin;

    private DatabaseType databaseType;
    private Database database;

    public DatabaseHandler(MysteryBlocksPluginImpl plugin) {
        this.plugin = plugin;
    }

    public boolean load() {
        this.databaseType = DatabaseType.valueOf(plugin.getConfig().getString("Settings.Database.Type", "SQLITE"));

        if (databaseType == DatabaseType.SQLITE) {
            this.database = new DatabaseSqlite(plugin, new File(plugin.getDataFolder() + "/data.db"));
        } else {
            this.database = new DatabaseMysql(plugin,
                    plugin.getConfig().getString("Settings.Database.Login.DB"),
                    plugin.getConfig().getString("Settings.Database.Login.Host"),
                    plugin.getConfig().getString("Settings.Database.Login.User"),
                    plugin.getConfig().getString("Settings.Database.Login.Pass"),
                    plugin.getConfig().getInt("Settings.Database.Login.Port", 3306));
        }

        return connect();
    }

    public boolean connect() {
        if (database.connect()) {
            createTables();
            return true;
        }

        return false;
    }

    public void disconnect() {
        database.disconnect();
    }

    public boolean isConnected() {
        return database.isConnected();
    }

    public void createTables() {
        database.update("CREATE TABLE IF NOT EXISTS MysteryBlocksData(" +
                "id SERIAL, " +
                "name VARCHAR(16) PRIMARY KEY, " +
                "cooldown BIGINT, " +
                "destroys BIGINT, " +
                "mines BIGINT, " +
                "playerMines TEXT)");
    }

    public Database getDatabase() {
        return database;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}

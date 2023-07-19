package cz.devfire.mysteryblocks.Database;

import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Interface.Database;
import cz.devfire.mysteryblocks.Database.Type.DatabaseHikariCP;
import cz.devfire.mysteryblocks.Database.Type.DatabaseMySQL;
import cz.devfire.mysteryblocks.Database.Type.DatabaseSQLite;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import org.bukkit.configuration.ConfigurationSection;

public class DatabaseHandler extends AbstractHandler {
    private DatabaseType databaseType;
    private Database database;

    public DatabaseHandler(MysteryBlocksPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = true;
        databaseType = DatabaseType.valueOf(section.getString("Type", "SQLITE").toUpperCase());

        switch (databaseType) {
            case SQLITE: { database = new DatabaseSQLite("data.db"); break; }
            case MYSQL: {  database = new DatabaseMySQL(section); break; }
            case HIKARI: { database = new DatabaseHikariCP(section); break; }
        }

        if (database != null && database.connect()) {
            createTables();
            return true;
        }

        return false;
    }

    @Override
    public boolean destroy() {
        enabled = false;
        return database.disconnect();
    }

    public void createTables() {
        database.update("" +
            "CREATE TABLE IF NOT EXISTS MysteryBlocksData(" +
                "id SERIAL, " +
                "name VARCHAR(16) PRIMARY KEY, " +
                "cooldown BIGINT, " +
                "regeneration BIGINT, " +
                "destroys BIGINT, " +
                "mines BIGINT, " +
                "requiredMines INTEGER, " +
                "lastMine BIGINT, " +
                "playerMines TEXT" +
            ")");

        // TODO: Remove in 2.5
        database.setIgnoreErrors(true);
        database.update("" +
            "ALTER TABLE MysteryBlocksData " +
            "ADD COLUMN regeneration BIGINT");
        database.update("" +
                "ALTER TABLE MysteryBlocksData " +
                "ADD COLUMN requiredMines INTEGER");
        database.update("" +
                "ALTER TABLE MysteryBlocksData " +
                "ADD COLUMN lastMine BIGINT");
        database.setIgnoreErrors(false);

        database.update("" +
            "CREATE TABLE IF NOT EXISTS MysteryBlocksPlayerData(" +
                "id SERIAL, " +
                "name VARCHAR(16) PRIMARY KEY, " +
                "messages TINYINT DEFAULT(1), " +
                "visibility TINYINT DEFAULT(1) " +
            ")");
    }

    public Database getDatabase() {
        return database;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}

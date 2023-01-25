package cz.devfire.mysteryblocks.Database;

import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Interface.Database;
import cz.devfire.mysteryblocks.Database.Type.DatabaseHikariCP;
import cz.devfire.mysteryblocks.Database.Type.DatabaseMySQL;
import cz.devfire.mysteryblocks.Database.Type.DatabaseSQLite;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class DatabaseHandler extends AbstractHandler {
    private DatabaseType databaseType;
    private Database database;

    public DatabaseHandler(MysteryBlocksPlugin plugin) {
        super(plugin);
    }

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
                    "destroys BIGINT, " +
                    "mines BIGINT, " +
                    "playerMines TEXT" +
                ")");
    }

    public Database getDatabase() {
        return database;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}

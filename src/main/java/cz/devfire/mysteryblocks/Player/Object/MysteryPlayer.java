package cz.devfire.mysteryblocks.Player.Object;

import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Interface.Database;
import cz.devfire.mysteryblocks.Database.Object.Results;
import cz.devfire.mysteryblocks.Player.PlayerHandler;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MysteryPlayer {
    private final PlayerHandler playerHandler;

    private final String playerName;
    private boolean messageEnabled = true;
    private boolean visualEnabled = true;

    public MysteryPlayer(PlayerHandler playerHandler, String playerName) {
        this.playerHandler = playerHandler;
        this.playerName = playerName;
    }

    public void load() {
        try {
            Database database = playerHandler.getPlugin().getDatabaseHandler().getDatabase();
            String sql = "SELECT * FROM MysteryBlocksPlayerData WHERE name = ?";

            Results results = database.query(sql, playerName);
            if (results.next()) {
                messageEnabled = results.getInt("messages") == 1;
                visualEnabled = results.getInt("visibility") == 1;
            } else {
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Database database = playerHandler.getPlugin().getDatabaseHandler().getDatabase();

            if (database.getType() == DatabaseType.SQLITE) {
                database.update("" +
                        "REPLACE INTO MysteryBlocksPlayerData VALUES (NULL, ?, ?, ?)",
                        playerName,
                        messageEnabled, visualEnabled
                );
            } else {
                database.update("" +
                        "INSERT INTO MysteryBlocksPlayerData (name, messages, visibility) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE messages = ?, visibility = ?",
                        playerName,
                        messageEnabled, visualEnabled,
                        messageEnabled, visualEnabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

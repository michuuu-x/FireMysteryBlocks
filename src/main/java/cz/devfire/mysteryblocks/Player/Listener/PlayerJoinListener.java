package cz.devfire.mysteryblocks.Player.Listener;

import cz.devfire.mysteryblocks.Player.PlayerHandler;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {
    private final PlayerHandler playerHandler;

    public PlayerJoinListener(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = event.getPlayer().getName();
        String playerUniqueId = player.getUniqueId().toString().replace("-", "");

        // Owner check message
        if (playerName.equalsIgnoreCase("Firestone82") ||
                playerUniqueId.equalsIgnoreCase("cf0eead71a4b471a93855a0b4628c785") ||
                playerUniqueId.equalsIgnoreCase("9afc93186c8d3f97ad9f74bdf7fa819d")) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(Utils.mm("<color:#f01f1f><bold>Server <dark_gray><bold>» <gray>Server is running <yellow>FireMysteryBlocks <dark_gray>(<gold>v" + playerHandler.getPlugin().getDescription().getVersion() + "<dark_gray>)"));
                }
            }.runTaskLaterAsynchronously(playerHandler.getPlugin(),20);
        }

        playerHandler.loadPlayer(player.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerHandler.savePlayer(event.getPlayer().getName());
    }
}

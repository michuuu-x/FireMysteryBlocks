package cz.devfire.mysteryblocks.Listener;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    public PlayerJoinListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = event.getPlayer().getName();
        String playerUniqueId = player.getUniqueId().toString().replace("-", "");

        if (plugin.getCache().exist(playerName.toLowerCase())) {
            for (String line : plugin.getCache().getStringList(playerName.toLowerCase())) {
                if (!line.contains("||")) continue;

                String[] lineArgs = line.split(" \\|\\| ");

                MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(lineArgs[0]);

                if (mysteryBlock != null) {
                    mysteryBlock.getMineActionHandler().parseAction(lineArgs[1], playerName);
                }
            }

            plugin.getCache().set(playerName.toLowerCase(), null);
            plugin.getCache().save();
        }

        // Owner check message
        if (playerName.equalsIgnoreCase("Firestone82") ||
                playerUniqueId.equalsIgnoreCase("cf0eead71a4b471a93855a0b4628c785") ||
                playerUniqueId.equalsIgnoreCase("9afc93186c8d3f97ad9f74bdf7fa819d")) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage(Utils.cc("&c&lServer &8&l» &7Server is running &eFireMysteryBlocks &8(&6v" + plugin.getDescription().getVersion() + "&8)"));
                }
            }.runTaskLaterAsynchronously(plugin,20);
        }
    }
}

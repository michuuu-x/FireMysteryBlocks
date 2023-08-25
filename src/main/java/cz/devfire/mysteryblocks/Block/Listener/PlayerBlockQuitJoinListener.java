package cz.devfire.mysteryblocks.Block.Listener;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerBlockQuitJoinListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    public PlayerBlockQuitJoinListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getCache().exist(event.getPlayer().getName().toLowerCase())) {
            for (String line : plugin.getCache().getStringList(event.getPlayer().getName().toLowerCase())) {
                if (!line.contains("||")) continue;

                String[] lineArgs = line.split(" \\|\\| ");

                MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(lineArgs[0]);

                if (mysteryBlock != null) {
                    mysteryBlock.getMineActionHandler().parseAction(lineArgs[1], event.getPlayer().getName());
                }
            }

            plugin.getCache().set(event.getPlayer().getName().toLowerCase(), null);
            plugin.getCache().save();
        }
    }
}

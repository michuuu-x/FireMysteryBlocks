package cz.devfire.mysteryblocks.Block.Listener;

import cz.devfire.mysteryblocks.Block.Handler.BlockGUIHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerBlockGUIClickListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    public PlayerBlockGUIClickListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {

            BlockGUIHandler blockGUIHandler = mysteryBlock.getGUIHandler();
            if (blockGUIHandler.isEnabled() && blockGUIHandler.getViewers().contains(player)) {
                event.setCancelled(true);

                int slot = event.getSlot();
                Pair<Boolean, List<String>> pair = blockGUIHandler.getActions().get(slot);

                if (pair != null) {
                    boolean close = pair.getFirst();
                    List<String> actions = pair.getSecond();

                    if (close) {
                        player.closeInventory();
                    }

                    mysteryBlock.getMineActionHandler().perform(actions, player.getName());
                }

                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        plugin.getBlockHandler().getBlocks().stream().filter(block -> block.getGUIHandler().isEnabled()).forEach(block -> block.getGUIHandler().getViewers().remove((Player) event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getBlockHandler().getBlocks().stream().filter(block -> block.getGUIHandler().isEnabled()).forEach(block -> block.getGUIHandler().getViewers().remove((Player) event.getPlayer()));
    }
}

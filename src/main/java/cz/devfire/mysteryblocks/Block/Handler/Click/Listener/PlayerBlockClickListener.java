package cz.devfire.mysteryblocks.Block.Handler.Click.Listener;

import cz.devfire.mysteryblocks.Block.Handler.Click.BlockClickHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerBlockClickListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    public PlayerBlockClickListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.LOWEST)
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (Utils.getServerVersionID() > 8 && event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        Block targetBlock = event.getClickedBlock();

        if (targetBlock != null) {
            MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlockAt(targetBlock.getLocation());

            if (mysteryBlock != null) {
                BlockClickHandler clickHandler = mysteryBlock.getClickHandler();

                if (clickHandler.isEnabled()) {
                    clickHandler.perform(player);
                }
            }
        }
    }

}

package cz.devfire.mysteryblocks.Block.Handler.ForceField.Listener;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method.CaptchaMethod;
import cz.devfire.mysteryblocks.Block.Handler.ForceField.BlockForceFieldHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BlockKnockbackListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    public BlockKnockbackListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
        }
    }
}

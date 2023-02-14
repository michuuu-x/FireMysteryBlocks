package cz.devfire.mysteryblocks.Block.AntiAfk.Listener;

import cz.devfire.mysteryblocks.Block.AntiAfk.Method.CaptchaMethod;
import cz.devfire.mysteryblocks.Block.Handler.BlockClickHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerCaptchaListener implements Listener {
    private final MysteryBlocksPlugin plugin;
    private final MysteryBlock mysteryBlock;

    private final CaptchaMethod method;

    public PlayerCaptchaListener(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock, CaptchaMethod method) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;
        this.method = method;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (method == null) return;
        if (!method.getCheckingPlayers().keySet().contains(event.getWhoClicked().getName())) return;

        if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
            if (method.isSkipBlank()) {
                if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(method.getActiveItem())) {
                    method.getCheckingPlayers().remove(event.getWhoClicked().getName());
                    event.getWhoClicked().closeInventory();

                    mysteryBlock.getMineActionHandler().perform(method.getOnSuccessActions(), event.getWhoClicked().getName());
                }
            } else {
                method.getCheckingPlayers().remove(event.getWhoClicked().getName());
                event.getWhoClicked().closeInventory();

                mysteryBlock.getMineActionHandler().perform(event.getCurrentItem() != null && event.getCurrentItem().isSimilar(method.getActiveItem()) ? method.getOnSuccessActions() : method.getOnFailActions(), event.getWhoClicked().getName());
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (method == null) return;
        if (!method.getCheckingPlayers().keySet().contains(event.getPlayer().getName())) return;
        method.getCheckingPlayers().remove(event.getPlayer().getName());

        mysteryBlock.getMineActionHandler().perform(method.getOnFailActions(), event.getPlayer().getName());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (method == null) return;
        if (!method.getCheckingPlayers().keySet().contains(event.getPlayer().getName())) return;
        method.getCheckingPlayers().remove(event.getPlayer().getName());;
    }
}

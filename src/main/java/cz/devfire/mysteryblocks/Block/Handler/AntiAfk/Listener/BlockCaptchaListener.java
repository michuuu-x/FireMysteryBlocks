package cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Listener;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method.CaptchaMethod;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BlockCaptchaListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    public BlockCaptchaListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockAntiAfkHandler antiAfkHandler = mysteryBlock.getAntiAfkHandler();

            if (antiAfkHandler.isEnabled() && antiAfkHandler.getMethod() instanceof CaptchaMethod) {
                CaptchaMethod method = (CaptchaMethod) antiAfkHandler.getMethod();

                if (method.getCheckingPlayers().keySet().contains(event.getPlayer().getName())) {
                    method.getCheckingPlayers().remove(event.getPlayer().getName());
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockAntiAfkHandler antiAfkHandler = mysteryBlock.getAntiAfkHandler();

            if (antiAfkHandler.isEnabled() && antiAfkHandler.getMethod() instanceof CaptchaMethod) {
                CaptchaMethod method = (CaptchaMethod) antiAfkHandler.getMethod();

                HashMap<String, Long> checkingPlayers = Maps.newHashMap(method.getCheckingPlayers());
                ItemStack clickedItem = event.getCurrentItem();
                Player player = (Player) event.getWhoClicked();

                if (checkingPlayers.containsKey(player.getName())) {
                    if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
                        if (method.isSkipBlankEnabled()) {
                            if (clickedItem != null && clickedItem.isSimilar(method.getActiveItem())) {
                                method.getCheckingPlayers().remove(player.getName());
                                player.closeInventory();

                                mysteryBlock.getMineActionHandler().perform(method.getOnSuccessActions(), player.getName());
                            }
                        } else {
                            method.getCheckingPlayers().remove(player.getName());
                            player.closeInventory();

                            mysteryBlock.getMineActionHandler().perform(clickedItem != null && clickedItem.isSimilar(method.getActiveItem()) ? method.getOnSuccessActions() : method.getOnFailActions(), player.getName());
                        }
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockAntiAfkHandler antiAfkHandler = mysteryBlock.getAntiAfkHandler();

            if (antiAfkHandler.isEnabled() && antiAfkHandler.getMethod() instanceof CaptchaMethod) {
                CaptchaMethod method = (CaptchaMethod) antiAfkHandler.getMethod();

                if (method.getCheckingPlayers().containsKey(event.getPlayer().getName())) {
                    method.getCheckingPlayers().remove(event.getPlayer().getName());

                    mysteryBlock.getMineActionHandler().perform(method.getOnFailActions(), event.getPlayer().getName());
                }
            }
        }
    }
}

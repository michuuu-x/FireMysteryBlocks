package cz.devfire.mysteryblocks.Block.AntiAfk.Methods;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class CaptchaMethod extends BaseAntiAfkMethod implements Listener {
    private final boolean fillEnabled;
    private final ItemStack activeItem;
    private final ItemStack fillItem;
    private final ArrayList<String> checkingPlayers = Lists.newArrayList();
    private final ArrayList<String> onFailActions = Lists.newArrayList();
    private final ArrayList<String> onSuccessActions = Lists.newArrayList();
    private int size;

    public CaptchaMethod(BlockAntiAfkHandler handler, MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        super(plugin, handler, mysteryBlock);

        this.size = mysteryBlock.getConfig().getInt("AntiAFK.Methods.Captcha.Inventory.Size");
        this.fillEnabled = mysteryBlock.getConfig().getBoolean("AntiAFK.Methods.Captcha.Inventory.Fill.Enabled");
        this.activeItem = Utils.getItemFromSection(mysteryBlock.getConfig().getConfigurationSection("AntiAFK.Methods.Captcha.Inventory.Active"));
        this.fillItem = Utils.getItemFromSection(mysteryBlock.getConfig().getConfigurationSection("AntiAFK.Methods.Captcha.Inventory.Fill.Item"));
        this.onFailActions.addAll(mysteryBlock.getConfig().getStringList("AntiAFK.Methods.Captcha.Action.OnFail"));
        this.onSuccessActions.addAll(mysteryBlock.getConfig().getStringList("AntiAFK.Methods.Captcha.Action.OnSuccess"));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void check(Player player) {
        Inventory inventory = null;

        if (size < 9) {
            size = 5;

            inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Language.CAPTCHA_TITLE.getMessage());
        } else {
            if (size < 18) {
                size = 9;
            } else if (size < 27) {
                size = 18;
            } else if (size < 36) {
                size = 27;
            } else if (size < 45) {
                size = 36;
            } else if (size < 54) {
                size = 45;
            } else {
                size = 54;
            }

            inventory = Bukkit.createInventory(null, size, Language.CAPTCHA_TITLE.getMessage());
        }

        if (fillEnabled) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, fillItem);
            }
        }

        inventory.setItem((new Random().nextInt(size)), activeItem);

        checkingPlayers.add(player.getName());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!checkingPlayers.contains(event.getWhoClicked().getName())) return;

        if (event.getCurrentItem() != null && event.getClickedInventory() != event.getWhoClicked().getInventory()) {
            checkingPlayers.remove(event.getWhoClicked().getName());
            event.getWhoClicked().closeInventory();

            Utils.doActions(plugin, mysteryBlock, event.getCurrentItem().isSimilar(activeItem) ? onSuccessActions : onFailActions, event.getWhoClicked().getName());
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!checkingPlayers.contains(event.getPlayer().getName())) return;

        checkingPlayers.remove(event.getPlayer().getName());
        Utils.doActions(plugin, mysteryBlock, onFailActions, event.getPlayer().getName());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        checkingPlayers.remove(event.getPlayer().getName());
    }
}

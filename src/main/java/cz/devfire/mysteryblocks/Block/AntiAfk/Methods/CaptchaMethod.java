package cz.devfire.mysteryblocks.Block.AntiAfk.Methods;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.AntiAfkMethod;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CaptchaMethod implements AntiAfkMethod {

    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;

    private final ArrayList<String> checkingPlayers = Lists.newArrayList();

    private final double percentage;
    private final Material item;
    private final ArrayList<String> onFailActions = Lists.newArrayList();
    private final ArrayList<String> onSuccessActions = Lists.newArrayList();

    public CaptchaMethod(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;

        this.percentage = mysteryBlock.getConfig().getDouble("AntiAFK.Methods.Captcha.Chance", 10);
        this.item = Material.valueOf(mysteryBlock.getConfig().getString("AntiAFK.Methods.Captcha.Material"));
        this.onFailActions.addAll(mysteryBlock.getConfig().getStringList("AntiAFK.Methods.Captcha.Action.OnFail"));
        this.onSuccessActions.addAll(mysteryBlock.getConfig().getStringList("AntiAFK.Methods.Captcha.Action.OnSuccess"));

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (!checkingPlayers.contains(event.getWhoClicked().getName())) return;

                if (event.getCurrentItem() != null && event.getClickedInventory() != event.getWhoClicked().getInventory()) {
                    checkingPlayers.remove(event.getWhoClicked().getName());
                    event.getWhoClicked().closeInventory();

                    Utils.doActions(plugin, mysteryBlock, onSuccessActions, event.getWhoClicked().getName());
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
        }, plugin);
    }

    @Override
    public void check(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Language.CAPTCHA_TITLE.getMessage());

        ItemStack itemStack = new ItemStack(item);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Utils.cc(Language.CAPTCHA_ITEM_TITLE.getMessage()));
        itemMeta.setLore(Utils.ccl(Arrays.stream(Language.CAPTCHA_ITEM_DESCRIPTION.getMessage().split("\n")).toList()));
        itemStack.setItemMeta(itemMeta);

        inventory.setItem((new Random().nextInt(27) + 1), itemStack);

        checkingPlayers.add(player.getName());
        player.openInventory(inventory);
    }

    public boolean canCheck(Player player) {
        return (100 * new Random().nextDouble()) <= percentage;
    }
}

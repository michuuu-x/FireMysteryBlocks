package cz.devfire.mysteryblocks.Block.AntiAfk.Method;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CaptchaMethod extends AbstractAntiAfkMethod {
    private final boolean fillEnabled;
    private final ItemStack activeItem;
    private final ItemStack fillItem;
    private final boolean skipBlank;
    private final HashMap<String, Long> checkingPlayers = Maps.newHashMap();
    private final ArrayList<String> onFailActions = Lists.newArrayList();
    private final ArrayList<String> onSuccessActions = Lists.newArrayList();
    private int size;
    private final boolean limitEnabled;
    private final int timeLimit;

    public CaptchaMethod(BlockAntiAfkHandler handler, MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, handler, mysteryBlock);

        this.size = mysteryBlock.getConfig().getInt("AntiAFK.Methods.Captcha.Inventory.Size");
        this.fillEnabled = mysteryBlock.getConfig().getBoolean("AntiAFK.Methods.Captcha.Inventory.Fill.Enabled");
        this.skipBlank = mysteryBlock.getConfig().getBoolean("AntiAFK.Methods.Captcha.Inventory.SkipBank");
        this.activeItem = Utils.getItemFromSection(mysteryBlock.getConfig().getConfigurationSection("AntiAFK.Methods.Captcha.Inventory.Active"));
        this.fillItem = Utils.getItemFromSection(mysteryBlock.getConfig().getConfigurationSection("AntiAFK.Methods.Captcha.Inventory.Fill.Item"));
        this.onFailActions.addAll(mysteryBlock.getConfig().getStringList("AntiAFK.Methods.Captcha.Action.OnFail"));
        this.onSuccessActions.addAll(mysteryBlock.getConfig().getStringList("AntiAFK.Methods.Captcha.Action.OnSuccess"));
        this.limitEnabled = mysteryBlock.getConfig().getBoolean("AntiAFK.Methods.Captcha.Time.Enabled");
        this.timeLimit = mysteryBlock.getConfig().getInt("AntiAFK.Methods.Captcha.Time.Limit");

        if (size < 9) { size = 5;
        } else if (size < 18) { size = 9;
        } else if (size < 27) { size = 18;
        } else if (size < 36) { size = 27;
        } else if (size < 45) { size = 36;
        } else if (size < 54) { size = 45;
        } else { size = 54; }

        plugin.getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (!checkingPlayers.keySet().contains(event.getWhoClicked().getName())) return;

                if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
                    if (skipBlank) {
                        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(activeItem)) {
                            checkingPlayers.remove(event.getWhoClicked().getName());
                            event.getWhoClicked().closeInventory();

                            mysteryBlock.getMineActionHandler().perform(onSuccessActions, event.getWhoClicked().getName());
                        }
                    } else {
                        checkingPlayers.remove(event.getWhoClicked().getName());
                        event.getWhoClicked().closeInventory();

                        mysteryBlock.getMineActionHandler().perform(event.getCurrentItem() != null && event.getCurrentItem().isSimilar(activeItem) ? onSuccessActions : onFailActions, event.getWhoClicked().getName());
                    }
                }

                event.setCancelled(true);
            }

            @EventHandler
            public void onClose(InventoryCloseEvent event) {
                if (!checkingPlayers.keySet().contains(event.getPlayer().getName())) return;
                checkingPlayers.remove(event.getPlayer().getName());

                mysteryBlock.getMineActionHandler().perform(onFailActions, event.getPlayer().getName());
            }

            @EventHandler
            public void onDisconnect(PlayerQuitEvent event) {
                if (!checkingPlayers.keySet().contains(event.getPlayer().getName())) return;
                checkingPlayers.remove(event.getPlayer().getName());;
            }

        }, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!limitEnabled) return;

                for (String playerName : Lists.newArrayList(checkingPlayers.keySet())) {
                    long time = checkingPlayers.get(playerName);

                    if (System.currentTimeMillis() > time + timeLimit) {
                        Player player = Bukkit.getPlayer(playerName);

                        if (player != null) {
                            player.closeInventory();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin,0,10);
    }

    @Override
    public void check(Player player) {
        Inventory inventory = null;

        if (size == 5) {
            inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Language.CAPTCHA_TITLE.getMessage());
        } else {
            inventory = Bukkit.createInventory(null, size, Language.CAPTCHA_TITLE.getMessage());
        }

        if (fillEnabled) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, fillItem);
            }
        }

        inventory.setItem((new Random().nextInt(size)), activeItem);

        checkingPlayers.put(player.getName(), System.currentTimeMillis());
        player.openInventory(inventory);
    }
}

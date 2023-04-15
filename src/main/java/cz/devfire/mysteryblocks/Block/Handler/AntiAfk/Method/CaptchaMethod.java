package cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Listener.BlockCaptchaListener;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Schedule.BlockCaptchaSchedule;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@Getter
@Setter
public class CaptchaMethod extends AbstractAntiAfkMethod {
    private final HashMap<String, Long> checkingPlayers = Maps.newHashMap();

    private int inventorySize;
    private final boolean fillEnabled;
    private final boolean skipBlankEnabled;
    private final ItemStack activeItem;
    private final ItemStack fillItem;

    private final ArrayList<String> onFailActions = Lists.newArrayList();
    private final ArrayList<String> onSuccessActions = Lists.newArrayList();

    private final boolean timeLimitEnabled;
    private final int timeLimit;

    public CaptchaMethod(BlockAntiAfkHandler handler, MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, handler, mysteryBlock);

        this.inventorySize = mysteryBlock.getConfig().getInt("AntiAFK.Methods.Captcha.Inventory.Size");
        this.fillEnabled = mysteryBlock.getConfig().getBoolean("AntiAFK.Methods.Captcha.Inventory.Fill.Enabled");
        this.skipBlankEnabled = mysteryBlock.getConfig().getBoolean("AntiAFK.Methods.Captcha.Inventory.SkipBank");
        this.activeItem = Utils.getItemFromSection(mysteryBlock.getConfig().getConfigurationSection("AntiAFK.Methods.Captcha.Inventory.Active"));
        this.fillItem = Utils.getItemFromSection(mysteryBlock.getConfig().getConfigurationSection("AntiAFK.Methods.Captcha.Inventory.Fill.Item"));
        this.onFailActions.addAll(mysteryBlock.getConfig().getStringList("AntiAFK.Methods.Captcha.Action.OnFail"));
        this.onSuccessActions.addAll(mysteryBlock.getConfig().getStringList("AntiAFK.Methods.Captcha.Action.OnSuccess"));
        this.timeLimitEnabled = mysteryBlock.getConfig().getBoolean("AntiAFK.Methods.Captcha.Time.Enabled");
        this.timeLimit = mysteryBlock.getConfig().getInt("AntiAFK.Methods.Captcha.Time.Limit");

        if (inventorySize < 9) { inventorySize = 5;
        } else if (inventorySize < 18) { inventorySize = 9;
        } else if (inventorySize < 27) { inventorySize = 18;
        } else if (inventorySize < 36) { inventorySize = 27;
        } else if (inventorySize < 45) { inventorySize = 36;
        } else if (inventorySize < 54) { inventorySize = 45;
        } else { inventorySize = 54; }
    }

    @Override
    public void destroy() {
        for (Player player : checkingPlayers.keySet().stream().map(Bukkit::getPlayer).toArray(Player[]::new)) {
            player.closeInventory();
        }
    }

    @Override
    public void check(Player player) {
        Inventory inventory = null;

        if (inventorySize == 5) {
            inventory = Bukkit.createInventory(null, InventoryType.HOPPER, Language.CAPTCHA_TITLE.getMessage());
        } else {
            inventory = Bukkit.createInventory(null, inventorySize, Language.CAPTCHA_TITLE.getMessage());
        }

        if (fillEnabled) {
            for (int i = 0; i < inventorySize; i++) {
                inventory.setItem(i, fillItem);
            }
        }

        inventory.setItem((new Random().nextInt(inventorySize)), activeItem);

        checkingPlayers.put(player.getName(), System.currentTimeMillis());
        player.openInventory(inventory);
    }

//    public ArrayList<String> getOnFailActions() {
//        return onFailActions;
//    }
//
//    public ArrayList<String> getOnSuccessActions() {
//        return onSuccessActions;
//    }
//
//    public HashMap<String, Long> getCheckingPlayers() {
//        return checkingPlayers;
//    }
//
//    public int getInventorySize() {
//        return inventorySize;
//    }
//
//    public int getTimeLimit() {
//        return timeLimit;
//    }
//
//    public ItemStack getActiveItem() {
//        return activeItem;
//    }
//
//    public ItemStack getFillItem() {
//        return fillItem;
//    }
//
//    public boolean isFillEnabled() {
//        return fillEnabled;
//    }
//
//    public boolean isTimeLimitEnabled() {
//        return timeLimitEnabled;
//    }
//
//    public boolean isSkipBlankEnabled() {
//        return skipBlankEnabled;
//    }
}

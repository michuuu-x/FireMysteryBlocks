package cz.devfire.mysteryblocks.Block.Handler.GUI;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Handler.History.BlockHistoryHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Pair;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class BlockGUIHandler extends AbstractBlockHandler {
    private int inventorySize = 0;
    private String inventoryTitle = "";
    private final HashMap<Integer, Pair<Boolean, List<String>>> actions = Maps.newHashMap();

    private final ArrayList<Player> viewers = Lists.newArrayList();
    private Inventory template = null;

    public BlockGUIHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("GUI"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            inventorySize = section.getInt("Size");
            inventoryTitle = section.getString("Title");

            if (inventorySize < 18) { inventorySize = 9;
            } else if (inventorySize < 27) { inventorySize = 18;
            } else if (inventorySize < 36) { inventorySize = 27;
            } else if (inventorySize < 45) { inventorySize = 36;
            } else if (inventorySize < 54) { inventorySize = 45;
            } else { inventorySize = 54; }

            template = Bukkit.createInventory(null, inventorySize);
            int from = 1;
            int to = 1;

            for (String key : section.getConfigurationSection("Items").getKeys(false)) {
                if (key.contains("~")) {
                    String[] keyArgs = key.split("~");

                    from = Integer.parseInt(keyArgs[0]);
                    to = Integer.parseInt(keyArgs[1]);
                } else {
                    from = to = Integer.parseInt(key);
                }

                for (int i = from; i <= to; i++) {
                    ItemStack itemStack = Utils.getItemFromSection(section.getConfigurationSection("Items."+ key));
                    template.setItem(i, itemStack);
                }
            }

            for (String key : section.getConfigurationSection("Actions").getKeys(false)) {
                if (key.contains("~")) {
                    String[] keyArgs = key.split("~");

                    from = Integer.parseInt(keyArgs[0]);
                    to = Integer.parseInt(keyArgs[1]);
                } else {
                    from = to = Integer.parseInt(key);
                }

                for (int i = from; i <= to; i++) {
                    actions.put(i, new Pair<>(section.getBoolean("Actions."+ key +".Close"), section.getStringList("Actions."+ key +".List")));
                }
            }
        }

        return true;
    }

    @Override
    public boolean destroy() {
        for (Player player : viewers) {
            player.closeInventory();
        }

        return super.destroy();
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, inventorySize, Utils.cc(Utils.parseBlockPlaceholders(mysteryBlock,null, inventoryTitle)));
        int i = -1;

        for (ItemStack item : template.getContents()) {
            i++;

            if (item == null || item.getType() == Material.AIR) continue;
            ItemStack itemStack = new ItemStack(item);
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta != null && itemMeta.hasDisplayName() && itemMeta.getDisplayName().trim().length() != 0) {
                itemMeta.setDisplayName(Utils.parseBlockPlaceholders(mysteryBlock,null, itemMeta.getDisplayName()));
            }

            if (itemMeta != null && itemMeta.hasLore() && itemMeta.getLore().size() != 0) {
                itemMeta.setLore(Utils.parseBlockPlaceholders(mysteryBlock,null, itemMeta.getLore()));
            }

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
        }

        BlockHistoryHandler historyHandler = mysteryBlock.getHistoryHandler();
        if (historyHandler.isEnabled()) {
            for (int j = 0; j < historyHandler.getSaveCount(); j++) {
                if (j > historyHandler.getHistorySlots().length) continue;
                int slot = historyHandler.getHistorySlots()[j];

                ItemStack stack = new ItemStack(historyHandler.getHistoryItem());
                ItemMeta meta = stack.getItemMeta();

                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().length() != 0) {
                    meta.setDisplayName(Utils.parseBlockPlaceholders(mysteryBlock,null, meta.getDisplayName().replace("{history-id}",(j+1) +"")));
                }

                if (meta != null && meta.hasLore() && meta.getLore().size() != 0) {
                    meta.setLore(Utils.parseBlockPlaceholders(mysteryBlock,null, Utils.replaceAll(meta.getLore() == null ? new ArrayList<>() : meta.getLore(),"{history-id}",(j+1) +"")));
                }

                stack.setItemMeta(meta);
                inventory.setItem(slot, stack);
            }
        }

        player.openInventory(inventory);
        viewers.add(player);
    }
}
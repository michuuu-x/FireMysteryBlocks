package cz.devfire.mysteryblocks.Block.Handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.History.BlockHistoryHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Pair;
import cz.devfire.mysteryblocks.Util.Utils;
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

public class BlockGUIHandler extends AbstractBlockHandler {
    private Inventory template = null;
    private String title = "";
    private int size = 0;
    private final HashMap<Integer, Pair<Boolean, List<String>>> actions = Maps.newHashMap();

    private final ArrayList<Player> viewers = Lists.newArrayList();

    public BlockGUIHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("GUI"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            size = section.getInt("Size");
            title = section.getString("Title");

            template = Bukkit.createInventory(null, size);

            for (String key : section.getConfigurationSection("Items").getKeys(false)) {
                int from = 1;
                int to = 1;

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
                int from = 1;
                int to = 1;

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
        enabled = false;

        for (Player player : viewers) {
            player.closeInventory();
        }

        return true;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, Utils.cc(Utils.parseBlockPlaceholders(mysteryBlock,null, title)));
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
            for (int j = historyHandler.getCount(); j > 0; j--) {
                int slot = historyHandler.getSlots()[j-1];

                ItemStack stack = new ItemStack(historyHandler.getItem());
                ItemMeta meta = stack.getItemMeta();

                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().length() != 0) {
                    meta.setDisplayName(Utils.parseBlockPlaceholders(mysteryBlock,null, meta.getDisplayName().replace("{history-id}",(j) +"")));
                }

                if (meta != null && meta.hasLore() && meta.getLore().size() != 0) {
                    meta.setLore(Utils.parseBlockPlaceholders(mysteryBlock,null, Utils.replaceAll(meta.getLore() == null ? new ArrayList<>() : meta.getLore(),"{history-id}",(historyHandler.getCount() - j +1) +"")));
                }

                stack.setItemMeta(meta);
                inventory.setItem(slot, stack);
            }
        }

        player.openInventory(inventory);
        viewers.add(player);
    }

    public ArrayList<Player> getViewers() {
        return viewers;
    }

    public HashMap<Integer, Pair<Boolean, List<String>>> getActions() {
        return actions;
    }
}
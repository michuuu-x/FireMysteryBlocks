package cz.devfire.mysteryblocks.Block.History;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.History.Object.History;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Pair;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BlockHistoryHandler extends AbstractBlockHandler {
    private int count = 0;
    private final ArrayList<History> history = Lists.newArrayList();
    private ItemStack item = new ItemStack(Material.STONE);
    private Integer[] slots = new Integer[]{};
    public static String format = "YYYY-MM-dd HH:mm";

    public BlockHistoryHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("History"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            count = section.getInt("Count");

            for (String line : plugin.getHistory().getStringList(mysteryBlock.getName().toLowerCase())) {
                history.add(new History(line));
            }

            item = Utils.getItemFromSection(section.getConfigurationSection("Item"));
            slots = Arrays.stream(section.getString("Slots").isEmpty() ? new String[]{} : section.getString("Slots").split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            format = section.getString("Format");
        }

        return true;
    }

    public void save() {
        history.add(new History(System.currentTimeMillis() +"-"+ String.join(",", mysteryBlock.getMineList())));

        while(history.size() > count) {
            history.remove(0);
        }

        plugin.getHistory().set(mysteryBlock.getName().toLowerCase(), history.stream().map(History::toString).collect(Collectors.toList()));
        plugin.getHistory().save();
    }

    public int getCount() {
        return count;
    }

    public History getHistory(int pos) {
        return pos >= history.size() ? new History() : history.get(pos);
    }

    public ItemStack getItem() {
        return item;
    }

    public Integer[] getSlots() {
        return slots;
    }
}

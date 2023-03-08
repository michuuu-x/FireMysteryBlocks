package cz.devfire.mysteryblocks.Block.Handler.History;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Handler.History.Object.History;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BlockHistoryHandler extends AbstractBlockHandler {
    private int saveCount = 0;
    private String dateFormat = "dd.MM.yyyy HH:mm:ss";
    private ItemStack historyItem = new ItemStack(Material.STONE);
    private Integer[] historySlots = new Integer[]{};

    private final ArrayList<History> historyList = Lists.newArrayList();

    public BlockHistoryHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("History"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        dateFormat = section.getString("Format");

        if (enabled) {
            saveCount = section.getInt("Count");

            for (String line : plugin.getHistory().getStringList(mysteryBlock.getName().toLowerCase())) {
                historyList.add(new History(line));
            }

            historyItem = Utils.getItemFromSection(section.getConfigurationSection("Item"));
            historySlots = Arrays.stream(section.getString("Slots").isEmpty() ? new String[]{} : section.getString("Slots").split(",")).map(Integer::parseInt).toArray(Integer[]::new);
        }

        return true;
    }

    public void save() {
        historyList.add(new History(System.currentTimeMillis() +"-"+ String.join(",", mysteryBlock.getMineList())));

        while (historyList.size() > saveCount) {
            historyList.remove(0);
        }

        plugin.getHistory().set(mysteryBlock.getName().toLowerCase(), historyList.stream().map(History::toString).collect(Collectors.toList()));
        plugin.getHistory().save();
    }

    public ArrayList<History> getHistoryList() {
        return historyList;
    }

    public int getSaveCount() {
        return saveCount;
    }

    public History getHistory(int pos) {
        return pos >= historyList.size() ? new History() : historyList.get(pos);
    }

    public ItemStack getHistoryItem() {
        return historyItem;
    }

    public Integer[] getHistorySlots() {
        return historySlots;
    }

    public String getDateFormat() {
        return dateFormat;
    }
}

package cz.devfire.mysteryblocks.Block;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Database.Object.Results;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Scheduler.BlockCooldownShedule;
import cz.devfire.mysteryblocks.Scheduler.BlockSaveSchedule;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class BlockHandler extends AbstractHandler {
    private final HashMap<String, MysteryBlock> blocks = Maps.newHashMap();

    private BlockCooldownShedule cooldownShedule = null;
    private BlockSaveSchedule saveSchedule = null;

    public BlockHandler(MysteryBlocksPlugin plugin) {
        super(plugin);
    }

    public boolean init(ConfigurationSection section) {
        enabled = true;

        File folder = new File(plugin.getDataFolder(),"blocks");
        if (folder.exists()) {
            File[] files = folder.listFiles();

            if (files.length != 0) {
                Utils.log(" &e- Loading blocks");

                for (File file : files) {
                    String name = file.getName().split("\\.")[0];

                    if (loadBlock(name)) {
                        Utils.log("   &e- Loading block &6" + name + "&e... &aSuccessful");
                    } else {
                        Utils.log("   &e- Loading block &6" + name + "&e... &cFailed");
                    }
                }
            } else {
                Utils.log(" &e- No blocks were found");
            }
        } else {
            folder.mkdir();
            plugin.saveResource("blocks/first.yml", false);

            Utils.log(" &e- Loading blocks");
            Utils.log("   &e- Default block was created. Configure it!");

            loadBlock("first");
        }

        startSchedulers();

        return true;
    }

    public boolean destroy() {
        enabled = false;

        stopSchedulers();
        save();
        removeOld();

        for (MysteryBlock block : blocks.values()) {
            block.destroy();
        }
        blocks.clear();

        return true;
    }

    public void save() {
        Utils.log(" §e- Saving blocks");

        for (MysteryBlock block : blocks.values()) {
            block.save();
            Utils.log("   §e- Saving block §6" + block.getName() + "§e... §aFinished");
        }
    }

    public boolean loadBlock(String blockName) {
        try {
            MysteryBlock mysteryBlock = new MysteryBlock(plugin, blockName);
            blocks.put(blockName.toLowerCase(), mysteryBlock);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean removeBlock(MysteryBlock block) {
        block.destroy();

        File blockFile = new File(plugin.getDataFolder(), "blocks/" + block.getName() + ".yml");
        blockFile.delete();

        blocks.remove(block.getName());
        return true;
    }

    public void removeOld() {
        Utils.log(" §e- Removing old blocks");

        try {
            Results rs = plugin.getDatabaseHandler().getDatabase().query("SELECT * FROM MysteryBlocksData");

            while (rs.next()) {
                String name = rs.getString("name");

                if (!blocks.containsKey(name.toLowerCase())) {
                    Utils.log("   §e- Found.. &6"+ name +"&e. &aRemoved! §r");
                    plugin.getDatabaseHandler().getDatabase().update("DELETE FROM MysteryBlocksData WHERE name = ?", name);
                }
            }
        } catch (Exception e) {
            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    public void stopSchedulers() {
        if (cooldownShedule != null) cooldownShedule.cancel();
        if (saveSchedule != null) saveSchedule.cancel();
    }

    public void startSchedulers() {
        cooldownShedule = new BlockCooldownShedule(plugin);
        saveSchedule = new BlockSaveSchedule(plugin);

        cooldownShedule.runTaskTimerAsynchronously(plugin,0,10);

        if (plugin.getConfig().getBoolean("Settings.AutoSave.Enabled")) {
            int delay = plugin.getConfig().getInt("Settings.AutoSave.Time") * 20;
            saveSchedule.runTaskTimerAsynchronously(plugin, delay, delay);
        }
    }

    public Collection<MysteryBlock> getBlocks() {
        return blocks.values();
    }

    public MysteryBlock getBlock(String name) {
        return blocks.get(name.toLowerCase());
    }

    public MysteryBlock getBlockAt(Location location) {
        return blocks.values().stream().filter(b -> b.getLocation() != null && location.getBlock().equals(b.getLocation().getBlock())).findFirst().orElse(null);
    }
}

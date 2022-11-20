package cz.devfire.mysteryblocks.Block;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Objects.MysteryBlockImpl;
import cz.devfire.mysteryblocks.Block.Scheduler.BlockCooldownScheduler;
import cz.devfire.mysteryblocks.Block.Scheduler.BlockSaveScheduler;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.BlockHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;

public class BlockHandlerImpl implements BlockHandler {
    private final MysteryBlocksPluginImpl plugin;

    private final HashMap<String, MysteryBlock> blocks = Maps.newHashMap();

    private BlockCooldownScheduler cooldownScheduler = null;
    private BlockSaveScheduler saveScheduler = null;

    public BlockHandlerImpl(MysteryBlocksPluginImpl plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File folder = new File(plugin.getDataFolder(), "blocks");

        if (folder.exists()) {
            File[] files = folder.listFiles();

            if (files.length != 0) {
                Bukkit.getConsoleSender().sendMessage(" §e- Loading blocks");

                for (File file : files) {
                    String name = file.getName().split("\\.")[0];

                    if (addBlock(name)) {
                        Bukkit.getConsoleSender().sendMessage("   §e- Loading block §6" + name + "§e... §aSuccessful");
                    } else {
                        Bukkit.getConsoleSender().sendMessage("   §e- Loading block §6" + name + "§e... §cFailed");
                    }
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(" §e- No blocks were found");
            }
        } else {
            folder.mkdir();
            plugin.saveResource("blocks/first.yml", false);

            Bukkit.getConsoleSender().sendMessage(" §e- Loading blocks");
            Bukkit.getConsoleSender().sendMessage("   §e- Default block was created. Configure it!");

            addBlock("first");
        }
    }

    public boolean addBlock(String name) {
        try {
            MysteryBlock mysteryBlock = new MysteryBlockImpl(plugin, name);
            blocks.put(name, mysteryBlock);
        } catch (Exception e) {
            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    public void removeBlock(MysteryBlock block) {
        block.remove();

        File blockFile = new File(plugin.getDataFolder(), "blocks/" + block.getName() + ".yml");
        blockFile.delete();

        blocks.remove(block.getName());
    }

    public void save() {
        Bukkit.getConsoleSender().sendMessage(" §e- Saving blocks");

        for (MysteryBlock block : blocks.values()) {
            block.save();
            Bukkit.getConsoleSender().sendMessage("   §e- Saving block §6" + block.getName() + "§e... §aFinished");
        }
    }

    public void removeOld() {
        Bukkit.getConsoleSender().sendMessage(" §e- Removing old blocks");

        try {
            ResultSet rs = plugin.getDatabaseHandler().getDatabase().query("SELECT * FROM MysteryBlocksData");

            while (rs.next()) {
                String name = rs.getString("name");

                if (!blocks.containsKey(name.toLowerCase())) {
                    plugin.getDatabaseHandler().getDatabase().update("DELETE FROM MysteryBlocksData WHERE name = '" + name + "'");
                }
            }
        } catch (Exception e) {
            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    public void stopSchedulers() {
        if (cooldownScheduler != null) cooldownScheduler.cancel();
        if (saveScheduler != null) saveScheduler.cancel();
    }

    public void startSchedulers() {
        cooldownScheduler = new BlockCooldownScheduler(plugin, this);
        saveScheduler = new BlockSaveScheduler(plugin, this);

        cooldownScheduler.runTaskTimerAsynchronously(plugin, 0, 10);

        if (plugin.getConfig().getBoolean("Settings.AutoSave.Enabled")) {
            int delay = plugin.getConfig().getInt("Settings.AutoSave.Time") * 20;
            saveScheduler.runTaskTimerAsynchronously(plugin, delay, delay);
        }
    }

    public Collection<MysteryBlock> getBlocks() {
        return blocks.values();
    }

    public MysteryBlock getBlock(String name) {
        return blocks.get(name.toLowerCase());
    }

    public MysteryBlock getBlockAt(Location location) {
        for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
            if (location.getBlock().equals(block.getBlock())) {
                return block;
            }
        }

        return null;
    }
}

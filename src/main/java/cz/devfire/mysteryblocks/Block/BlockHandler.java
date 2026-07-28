package cz.devfire.mysteryblocks.Block;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Listener.BlockCaptchaListener;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Schedule.BlockCaptchaSchedule;
import cz.devfire.mysteryblocks.Block.Handler.AntiCheat.Schedule.BlockAntiCheatSchedule;
import cz.devfire.mysteryblocks.Block.Handler.Click.Listener.PlayerBlockClickListener;
import cz.devfire.mysteryblocks.Block.Handler.Cooldown.Schedule.BlockCooldownSchedule;
import cz.devfire.mysteryblocks.Block.Handler.ForceField.Listener.BlockFieldListener;
import cz.devfire.mysteryblocks.Block.Handler.GUI.Listener.BlockGUIListener;
import cz.devfire.mysteryblocks.Block.Handler.Regeneration.Schedule.BlockRegenerationSchedule;
import cz.devfire.mysteryblocks.Block.Handler.Visibility.Schedule.BlockVisibilitySchedule;
import cz.devfire.mysteryblocks.Block.Listener.PlayerBlockMineListener;
import cz.devfire.mysteryblocks.Block.Listener.PlayerBlockQuitJoinListener;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Database.Object.QueryResult;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Scheduler.BlockSaveSchedule;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class BlockHandler extends AbstractHandler {
    private final HashMap<String, MysteryBlock> blocks = Maps.newHashMap();

    private final ArrayList<Listener> listeners = new ArrayList<>();
    private final ArrayList<BukkitRunnable> runnables = new ArrayList<>();

    public BlockHandler(MysteryBlocksPlugin plugin) {
        super(plugin);
    }

    public boolean init(ConfigurationSection section) {
        enabled = true;

        File folder = new File(plugin.getDataFolder(),"blocks");
        if (folder.exists()) {
            File[] files = folder.listFiles();

            if (files.length != 0) {
                Utils.log(" <yellow>- Loading blocks");

                for (File file : files) {
                    String name = file.getName().split("\\.")[0];

                    if (loadBlock(name)) {
                        Utils.log("   <yellow>- Loading block <gold>" + name + "<yellow>... <color:#05fa11>Successful");
                    } else {
                        Utils.log("   <yellow>- Loading block <gold>" + name + "<yellow>... <color:#f01f1f>Failed");
                    }
                }
            } else {
                Utils.log(" <yellow>- No blocks were found");
            }
        } else {
            folder.mkdir();
            plugin.saveResource("blocks/first.yml", false);

            Utils.log(" <yellow>- Loading blocks");
            Utils.log("   <yellow>- Default block was created. Configure it!");

            loadBlock("first");
        }

        initListeners();
        initSchedules();

        return true;
    }

    public boolean destroy() {
        enabled = false;

        stopListeners();
        stopSchedules();

        save();
        removeOld();

        blocks.values().forEach(MysteryBlock::destroy);
        blocks.clear();

        return true;
    }

    public void save() {
        Utils.log(" <yellow>- Saving blocks");

        for (MysteryBlock block : blocks.values()) {
            block.save();
            Utils.log("   <yellow>- Saving block <gold>" + block.getName() + "<yellow>... <color:#05fa11>Finished");
        }
    }

    public boolean loadBlock(String blockName) {
        try {
            MysteryBlock mysteryBlock = new MysteryBlock(plugin, blockName);
            blocks.put(mysteryBlock.getName().toLowerCase(), mysteryBlock);
        } catch (Exception e) {
            e.printStackTrace();

            if (MysteryBlocksPlugin.isDebugEnabled()) {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    public boolean removeBlock(MysteryBlock block) {
        try {
            blocks.remove(block.getName().toLowerCase());

            File blockFile = new File(plugin.getDataFolder(), "blocks/" + block.getName() + ".yml");
            blockFile.delete();

            block.destroy();
        } catch (Exception e) {
            // TODO: Error message

            if (MysteryBlocksPlugin.isDebugEnabled()) {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    public void removeOld() {
        Utils.log(" <yellow>- Removing old blocks");

        try {
            QueryResult rs = plugin.getDatabaseHandler().getDatabase().query("SELECT * FROM MysteryBlocksData");

            while (rs.next()) {
                String name = rs.getString("name");
                if (!blocks.containsKey(name.toLowerCase())) {
                    Utils.log("   <yellow>- Found.. <gold>"+ name +"<yellow>. <color:#05fa11>Removed! <reset>");
                    plugin.getDatabaseHandler().getDatabase().update("DELETE FROM MysteryBlocksData WHERE name = ?", name);
                }
            }
        } catch (Exception e) {
            // TODO: Error message

            if (MysteryBlocksPlugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    public void initSchedules() {
        if (plugin.getConfig().getBoolean("Settings.AutoSave.Enabled")) {
            int delay = plugin.getConfig().getInt("Settings.AutoSave.Time") * 20;
            initSchedule(new BlockSaveSchedule(plugin),true, delay, delay);
        }

        initSchedule(new BlockCooldownSchedule(plugin),false,20,5);
        initSchedule(new BlockCaptchaSchedule(plugin),true,20,20);
        initSchedule(new BlockAntiCheatSchedule(plugin),true,20,1);
        initSchedule(new BlockRegenerationSchedule(plugin),true,20,10);
        initSchedule(new BlockVisibilitySchedule(plugin),false,20,5);
    }

    public void initSchedule(BukkitRunnable runnable, boolean async, long delay, long period) {
        if (async) {
            runnable.runTaskTimerAsynchronously(plugin, delay, period);
        } else {
            runnable.runTaskTimer(plugin, delay, period);
        }

        runnables.add(runnable);
    }

    public void stopSchedule(BukkitRunnable runnable) {
        runnable.cancel();
        runnables.remove(runnable);
    }

    public void stopSchedules() {
        for (BukkitRunnable runnable : runnables) {
            runnable.cancel();
        }

        runnables.clear();
    }

    public void initListeners() {
        initListener(new PlayerBlockMineListener(plugin));
        initListener(new PlayerBlockQuitJoinListener(plugin));

        initListener(new BlockCaptchaListener(plugin));
        initListener(new BlockFieldListener(plugin));
        initListener(new BlockGUIListener(plugin));
        initListener(new PlayerBlockClickListener(plugin));
    }

    public void initListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        listeners.add(listener);
    }

    public void stopListener(Listener listener) {
        HandlerList.unregisterAll(listener);
        listeners.remove(listener);
    }

    public void stopListeners() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }

        listeners.clear();
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

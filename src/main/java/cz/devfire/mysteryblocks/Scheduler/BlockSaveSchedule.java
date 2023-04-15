package cz.devfire.mysteryblocks.Scheduler;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockSaveSchedule extends BukkitRunnable {
    private final MysteryBlocksPlugin plugin;

    public BlockSaveSchedule(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            mysteryBlock.save();
        }
    }
}
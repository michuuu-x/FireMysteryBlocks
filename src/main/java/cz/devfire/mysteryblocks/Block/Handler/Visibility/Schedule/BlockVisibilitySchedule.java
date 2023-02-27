package cz.devfire.mysteryblocks.Block.Handler.Visibility.Schedule;

import cz.devfire.mysteryblocks.Block.Handler.Visibility.BlockVisibilityHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockVisibilitySchedule extends BukkitRunnable {
    private final MysteryBlocksPlugin plugin;

    public BlockVisibilitySchedule(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockVisibilityHandler visibilityHandler = mysteryBlock.getVisibilityHandler();

            if (visibilityHandler.isEnabled()) {
                visibilityHandler.check();
            }
        }
    }
}

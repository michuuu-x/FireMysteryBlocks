package cz.devfire.mysteryblocks.Scheduler;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Block.Regeneration.BlockRegenerationHandler;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockRegenerationSchedule extends BukkitRunnable {
    private final MysteryBlocksPlugin plugin;

    public BlockRegenerationSchedule(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockRegenerationHandler regenerationHandler = mysteryBlock.getRegenerationHandler();

            if (regenerationHandler.isEnabled() && !mysteryBlock.getCooldownHandler().isUnder()) {
                if ((mysteryBlock.getLastMine() != Long.MAX_VALUE && mysteryBlock.getLastMine() != 0) && mysteryBlock.getLastMine() + regenerationHandler.getTime() < System.currentTimeMillis()) {
                    regenerationHandler.perform();
                }
            }
        }
    }
}

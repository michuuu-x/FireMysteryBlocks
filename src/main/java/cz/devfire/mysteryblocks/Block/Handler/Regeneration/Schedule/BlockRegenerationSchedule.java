package cz.devfire.mysteryblocks.Block.Handler.Regeneration.Schedule;

import cz.devfire.mysteryblocks.Block.Handler.Regeneration.BlockRegenerationHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
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
                if (mysteryBlock.getLastMine() != Long.MAX_VALUE && mysteryBlock.getLastMine() != 0) {
                    if (mysteryBlock.getLastMine() + regenerationHandler.getRegenerationTime() < System.currentTimeMillis()) {
                        regenerationHandler.perform();
                    }
                } else if (regenerationHandler.getLastProcess() != Long.MAX_VALUE && regenerationHandler.getLastProcess() != 0) {
                    if (regenerationHandler.getLastProcess() + regenerationHandler.getMillisPerProcess() < System.currentTimeMillis()) {
                        regenerationHandler.perform();
                    }
                }

                if (regenerationHandler.isProgressiveEnabled() && regenerationHandler.isUnder()) {
                    if (mysteryBlock.getHologramHandler().isEnabled()) {
                        mysteryBlock.getHologramHandler().getHologram().update();
                    }
                }
            }
        }
    }
}

package cz.devfire.mysteryblocks.Block.Scheduler;

import cz.devfire.mysteryblocks.Block.BlockHandlerImpl;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Hologram.BlockHologram;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockCooldownScheduler extends BukkitRunnable {
    private final MysteryBlocksPluginImpl plugin;
    private final BlockHandlerImpl blockHandler;

    public BlockCooldownScheduler(MysteryBlocksPluginImpl plugin, BlockHandlerImpl blockHandler) {
        this.plugin = plugin;
        this.blockHandler = blockHandler;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : blockHandler.getBlocks()) {
            if (mysteryBlock.isCooldownEnabled() && mysteryBlock.getCooldownCurrent() != 0) {
                Long time = mysteryBlock.getCooldownCurrent() + mysteryBlock.getCooldownRequired() - System.currentTimeMillis();

                if (time > 1000) {
                    BlockHologram hologram = mysteryBlock.getHologramHandler().getHologram();

                    if (hologram != null) {
                        try {
                            hologram.update();
                        } catch (Exception e) { /* */ }
                    }
                } else {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            mysteryBlock.reset();
                        }
                    }.runTask(plugin);
                }
            }
        }
    }
}

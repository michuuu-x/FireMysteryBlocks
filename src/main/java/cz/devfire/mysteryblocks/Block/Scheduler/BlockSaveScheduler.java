package cz.devfire.mysteryblocks.Block.Scheduler;

import cz.devfire.mysteryblocks.Block.BlockHandlerImpl;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockSaveScheduler extends BukkitRunnable {
    private final MysteryBlocksPluginImpl plugin;
    private final BlockHandlerImpl blockHandler;

    public BlockSaveScheduler(MysteryBlocksPluginImpl plugin, BlockHandlerImpl blockHandler) {
        this.plugin = plugin;
        this.blockHandler = blockHandler;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : blockHandler.getBlocks()) {
            mysteryBlock.save(false);
        }
    }
}

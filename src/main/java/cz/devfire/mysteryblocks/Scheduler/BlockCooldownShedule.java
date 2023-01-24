package cz.devfire.mysteryblocks.Scheduler;

import cz.devfire.mysteryblocks.Block.Handler.BlockCooldownHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Interface.HologramProvider;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockCooldownShedule extends BukkitRunnable {
    private final MysteryBlocksPlugin plugin;

    public BlockCooldownShedule(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockCooldownHandler cooldownHandler = mysteryBlock.getCooldownHandler();

            if (cooldownHandler.isEnabled() && cooldownHandler.isUnder()) {
                if (cooldownHandler.getETA() > 1000) {
                    HologramProvider hologramProvider = mysteryBlock.getHologramHandler().getHologram();

                    if (hologramProvider != null) {
                        hologramProvider.update();
                    }
                } else {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            mysteryBlock.reset(false);
                        }
                    }.runTask(plugin);
                }
            }
        }
    }
}

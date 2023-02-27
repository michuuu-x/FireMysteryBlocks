package cz.devfire.mysteryblocks.Block.Handler.Cooldown.Schedule;

import cz.devfire.mysteryblocks.Block.Handler.Cooldown.BlockCooldownHandler;
import cz.devfire.mysteryblocks.Block.Handler.Schedule.BlockScheduleHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Interface.HologramProvider;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockCooldownSchedule extends BukkitRunnable {
    private final MysteryBlocksPlugin plugin;

    public BlockCooldownSchedule(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockCooldownHandler cooldownHandler = mysteryBlock.getCooldownHandler();
            BlockScheduleHandler scheduleHandler = mysteryBlock.getScheduleHandler();

            if (cooldownHandler.isEnabled() && cooldownHandler.isUnder()) {
                if (cooldownHandler.getETA() > 500) {
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
            } else {
                if (scheduleHandler.isEnabled() && scheduleHandler.isAutoDestroyEnabled()) {
                    HologramProvider hologramProvider = mysteryBlock.getHologramHandler().getHologram();

                    if (hologramProvider != null) {
                        hologramProvider.update();
                    }

                    if (mysteryBlock.getLastReset() + scheduleHandler.getDestroyTime() < System.currentTimeMillis()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (scheduleHandler.isActionsEnabled()) {
                                    mysteryBlock.broke(false);
                                } else {
                                    mysteryBlock.broke(true);
                                }
                            }
                        }.runTask(plugin);
                    }
                }
            }
        }
    }
}

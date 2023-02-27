package cz.devfire.mysteryblocks.Block.Handler.AntiCheat.Schedule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.Block.Handler.AntiCheat.BlockAntiCheatHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Set;

public class BlockAntiCheatSchedule extends BukkitRunnable {
    private final MysteryBlocksPlugin plugin;

    public BlockAntiCheatSchedule(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockAntiCheatHandler antiCheatHandler = mysteryBlock.getAntiCheatHandler();

            if (antiCheatHandler != null && antiCheatHandler.isEnabled()) {
                HashMap<Player, Set<Long>> mineMap = Maps.newHashMap(antiCheatHandler.getMineMap());

                for (Player player : mineMap.keySet()) {
                    Set<Long> playerMap = Sets.newHashSet(mineMap.get(player));

                    if (!player.hasPermission(mysteryBlock.getPermission() +".bypass.anti-cheat")) {
                        for (Integer actionPoint : antiCheatHandler.getActionKeyList()) {
                            if (playerMap.size() > actionPoint) {
                                if (antiCheatHandler.getWarnCooldownMap().getOrDefault(actionPoint + "-" + player.getName().toLowerCase(),0L) + antiCheatHandler.getActionCooldown() < System.currentTimeMillis() || actionPoint == 0) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            mysteryBlock.getMineActionHandler().perform(antiCheatHandler.getActions().get(actionPoint), player.getName());
                                        }
                                    }.runTask(plugin);

                                    antiCheatHandler.getWarnCooldownMap().put(actionPoint + "-" + player.getName().toLowerCase(), System.currentTimeMillis());
                                }

                                break;
                            }
                        }
                    }

                    for (Long time : playerMap) {
                        if (time + antiCheatHandler.getCalculationTime() < System.currentTimeMillis()) {
                            antiCheatHandler.getMineMap().get(player).remove(time);
                        }
                    }

                    if (playerMap.size() == 0 || player == null || !player.isOnline()) {
                        antiCheatHandler.getMineMap().remove(player);

                        for (String warn : Lists.newArrayList(antiCheatHandler.getWarnCooldownMap().keySet())) {
                            if (warn.endsWith(player.getName().toLowerCase())) {
                                antiCheatHandler.getWarnCooldownMap().remove(warn);
                            }
                        }
                    }
                }
            }
        }
    }
}

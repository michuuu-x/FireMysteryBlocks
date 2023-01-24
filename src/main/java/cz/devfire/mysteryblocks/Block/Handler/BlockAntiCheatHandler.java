package cz.devfire.mysteryblocks.Block.Handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockAntiCheatHandler extends AbstractBlockHandler {
    private long checkMillis = 1000;
    private long cooldown = 1000;
    private int breakLimit = Integer.MAX_VALUE;
    private final HashMap<Integer, List<String>> actions = Maps.newLinkedHashMap();
    private final LinkedList<Integer> actionKeyList = Lists.newLinkedList();
    private final HashMap<Player, Set<Long>> mineMap = Maps.newHashMap();
    private final HashMap<String, Long> warnCooldown = Maps.newHashMap();
    private BukkitRunnable schedule = null;

    public BlockAntiCheatHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);

        schedule = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Lists.newArrayList(mineMap.keySet())) {
                    Set<Long> playerMap = Sets.newHashSet(mineMap.get(player));

                    if (!player.hasPermission(mysteryBlock.getPermission() +".bypass.anticheat")) {
                        for (Integer actionPoint : actionKeyList) {
                            if (playerMap.size() > actionPoint) {
                                if (warnCooldown.getOrDefault(actionPoint + "-" + player.getName().toLowerCase(), 0L) + cooldown < System.currentTimeMillis() || actionPoint == 0) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            mysteryBlock.getMineActionHandler().perform(actions.get(actionPoint), player.getName());
                                        }
                                    }.runTask(plugin);

                                    warnCooldown.put(actionPoint + "-" + player.getName().toLowerCase(), System.currentTimeMillis());
                                }

                                break;
                            }
                        }
                    }

                    for (Long time : playerMap) {
                        if (time + checkMillis < System.currentTimeMillis()) {
                            mineMap.get(player).remove(time);
                        }
                    }

                    if (playerMap.size() == 0 || player == null || !player.isOnline()) {
                        mineMap.remove(player);

                        for (String warn : Lists.newArrayList(warnCooldown.keySet())) {
                            if (warn.endsWith(player.getName().toLowerCase())) {
                                warnCooldown.remove(warn);
                            }
                        }
                    }
                }
            }
        };

        init(mysteryBlock.getConfig().getConfigurationSection("AntiCheat"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            checkMillis = section.getLong("Time");
            cooldown = section.getLong("Cooldown");
            breakLimit = section.getInt("Break");

            for (String key : section.getConfigurationSection("Action").getKeys(false)) {
                actions.put(Integer.parseInt(key), section.getStringList("Action."+ key));
            }
        }

        actionKeyList.addAll(actions.keySet());
        Collections.sort(actionKeyList);
        Collections.reverse(actionKeyList);

        schedule.runTaskTimerAsynchronously(plugin,0,1);
        return true;
    }

    public long getSpeed(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        return mineMap.get(player) == null ? -1 : mineMap.get(player).size();
    }

    public boolean mine(Player player) {
        Set<Long> playerMap = mineMap.computeIfAbsent(player, k -> Sets.newHashSet());

        playerMap.add(System.currentTimeMillis());

        return playerMap.size() > breakLimit && breakLimit > 0;
    }
}

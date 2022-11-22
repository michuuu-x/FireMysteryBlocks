package cz.devfire.mysteryblocks.Block.Handlers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockAntiCheatHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockAntiCheatHandlerImpl extends BukkitRunnable implements BlockAntiCheatHandler {
    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;

    private boolean enabled;
    private long checkMillis;
    private long cooldown;
    private int breakLimit;
    private HashMap<Integer, List<String>> actions = Maps.newLinkedHashMap();
    private HashMap<Player, Set<Long>> mineMap = Maps.newHashMap();
    private HashMap<String, Long> warnCooldown = Maps.newHashMap();
    private final LinkedList<Integer> actionKeyList = Lists.newLinkedList();

    public BlockAntiCheatHandlerImpl(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;

        this.runTaskTimerAsynchronously(plugin,0,1);

        load();
    }

    @Override
    public void load() {
        enabled = mysteryBlock.getConfig().getBoolean("AntiCheat.Enabled");
        checkMillis = mysteryBlock.getConfig().getLong("AntiCheat.Time");
        cooldown = mysteryBlock.getConfig().getLong("AntiCheat.Cooldown");
        breakLimit = mysteryBlock.getConfig().getInt("AntiCheat.Break");

        for (String key : mysteryBlock.getConfig().getKeys("AntiCheat.Action")) {
            actions.put(Integer.parseInt(key), mysteryBlock.getConfig().getStringList("AntiCheat.Action."+ key));
        }

        actionKeyList.addAll(actions.keySet());
        Collections.sort(actionKeyList);
        Collections.reverse(actionKeyList);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public long getCheckMillis() {
        return checkMillis;
    }

    @Override
    public HashMap<Integer, List<String>> getActions() {
        return actions;
    }

    @Override
    public HashMap<Player, Set<Long>> getMineMap() {
        return mineMap;
    }

    @Override
    public void run() {
        for (Player player : Lists.newArrayList(mineMap.keySet())) {
            Set<Long> playerMap = Sets.newHashSet(mineMap.get(player));

            for (Integer actionPoint : actionKeyList) {
                if (playerMap.size() > actionPoint) {
                    if (warnCooldown.getOrDefault(actionPoint +"-"+ player.getName().toLowerCase(),0L) + cooldown < System.currentTimeMillis() || actionPoint == 0) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Utils.doActions(plugin, mysteryBlock, actions.get(actionPoint), player.getName());
                            }
                        }.runTask(plugin);

                        warnCooldown.put(actionPoint +"-"+ player.getName().toLowerCase(), System.currentTimeMillis());
                    }

                    break;
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

    @Override
    public boolean mine(Player player) {
        Set<Long> playerMap = mysteryBlock.getAntiCheatHandler().getMineMap().get(player);

        if (playerMap == null) {
            playerMap = Sets.newHashSet();
            mysteryBlock.getAntiCheatHandler().getMineMap().put(player, playerMap);
        }

        playerMap.add(System.currentTimeMillis());

        return playerMap.size() > breakLimit && breakLimit > 0;
    }
}

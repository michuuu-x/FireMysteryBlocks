package cz.devfire.mysteryblocks.Block.Handler.AntiCheat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class BlockAntiCheatHandler extends AbstractBlockHandler {
    private long calculationTime = 1000;
    private long actionCooldown = 1000;
    private int breakLimit = Integer.MAX_VALUE;

    private final HashMap<Integer, List<String>> actions = Maps.newLinkedHashMap();
    private final LinkedList<Integer> actionKeyList = Lists.newLinkedList();
    private final HashMap<Player, Set<Long>> mineMap = Maps.newHashMap();
    private final HashMap<String, Long> warnCooldownMap = Maps.newHashMap();
    private final HashMap<Integer, Long> modifiersEfficiency = Maps.newHashMap();
    private final HashMap<Integer, Long> modifiersHaste = Maps.newHashMap();

    public BlockAntiCheatHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("AntiCheat"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            calculationTime = section.getLong("Time");
            actionCooldown = section.getLong("Cooldown");
            breakLimit = section.getInt("Break");

            for (String key : section.getConfigurationSection("Action").getKeys(false)) {
                actions.put(Integer.parseInt(key), section.getStringList("Action."+ key));
            }

            actionKeyList.addAll(actions.keySet());
            Collections.sort(actionKeyList);
            Collections.reverse(actionKeyList);

            for (String key : section.getConfigurationSection("Modifiers.Efficiency").getKeys(false)) {
                modifiersEfficiency.put(Integer.parseInt(key), section.getLong("Modifiers.Efficiency."+ key));
            }

            for (String key : section.getConfigurationSection("Modifiers.Haste").getKeys(false)) {
                modifiersHaste.put(Integer.parseInt(key), section.getLong("Modifiers.Haste."+ key));
            }
        }

        return true;
    }

    public long getSpeed(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        return mineMap.get(player) == null ? -1 : mineMap.get(player).size();
    }

    public boolean mine(Player player) {
        mineMap.computeIfAbsent(player, k -> Sets.newHashSet()).add(System.currentTimeMillis());
        return mineMap.get(player).size() > breakLimit && breakLimit > 0;
    }
}

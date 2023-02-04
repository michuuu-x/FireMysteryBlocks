package cz.devfire.mysteryblocks.Block.Handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BlockMiningEffectsHandler extends AbstractBlockHandler {
    private final ArrayList<PotionEffect> constantList = Lists.newArrayList();
    private final HashMap<PotionEffect, Double> percentageMap = Maps.newHashMap();
    private final HashMap<Player, Long> cooldownMap = Maps.newHashMap();

    private Long cooldown = 0L;

    public BlockMiningEffectsHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("MiningEffects"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            this.cooldown = section.getLong("Percentage.Cooldown");

            for (String effect : section.getStringList("Constant.List")) {
                String[] effectArgs = effect.split(":");

                PotionEffectType type = null;
                try {
                    type = PotionEffectType.getByName(effectArgs[0]);
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] &cUnknown effect: \"" + effectArgs[1] + "\"");

                    if (plugin.isDebugEnabled()) {
                        e.printStackTrace();
                    }
                }

                int amplifier = Math.min(Integer.parseInt(effectArgs[1]) - 1, 0);
                int duration = 20;

                if (type != null) {
                    constantList.add(new PotionEffect(type, duration, amplifier,true,false));
                }
            }

            for (String effect : section.getStringList("Percentage.List")) {
                String[] percentageArgs = effect.split("-");
                String[] effectArgs = percentageArgs[1].split(":");

                PotionEffectType type = null;
                try {
                    type = PotionEffectType.getByName(effectArgs[0]);
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] &cUnknown effect: \"" + percentageArgs[1] + "\"");

                    if (plugin.isDebugEnabled()) {
                        e.printStackTrace();
                    }
                }

                double percentage = Double.parseDouble(percentageArgs[0]);
                int amplifier = Math.min(Integer.parseInt(effectArgs[1]) - 1, 0);
                int duration = Math.min(Integer.parseInt(effectArgs[2]), 20);

                if (type != null) {
                    percentageMap.put(new PotionEffect(type, duration, amplifier, true, false), percentage);
                }
            }
        }

        return true;
    }

    public void apply(Player player) {
        for (PotionEffect effect : constantList) {
            if (!player.hasPotionEffect(effect.getType())) {
                ItemStack itemStack = player.getInventory().getItemInHand();
                player.getInventory().setItemInHand(null);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().setItemInHand(itemStack);
                    }
                }.runTaskLater(plugin,2);
            }

            player.addPotionEffect(effect);
        }

        if (cooldownMap.getOrDefault(player, Long.MAX_VALUE) + cooldown < System.currentTimeMillis()) {
            cooldownMap.put(player, System.currentTimeMillis());

            double targetPercentage = new Random().nextDouble() * 100;
            for (PotionEffect effect : percentageMap.keySet()) {
                double percentage = percentageMap.get(effect);

                if (percentage < targetPercentage) {
                    player.addPotionEffect(effect);
                }
            }
        }
    }
}
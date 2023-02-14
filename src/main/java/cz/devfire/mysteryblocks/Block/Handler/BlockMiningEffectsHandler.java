package cz.devfire.mysteryblocks.Block.Handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class BlockMiningEffectsHandler extends AbstractBlockHandler {
    private final ArrayList<PotionEffect> constantList = Lists.newArrayList();
    private final HashMap<PotionEffect, Double> percentageMap = Maps.newHashMap();
    private final HashMap<Player, Long> cooldownMap = Maps.newHashMap();
    private final ArrayList<String> bypassList = Lists.newArrayList();

    private Long cooldown = 0L;

    public BlockMiningEffectsHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("MiningEffects"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            this.bypassList.addAll(section.getStringList("Bypass"));
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
        if (!bypassList.isEmpty()) {
            ItemStack itemStack = player.getInventory().getItemInHand();

            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();

                for (String bypass : bypassList) {
                    String[] bypassArgs = bypass.split(";;");

                    if (bypassArgs.length > 0 && itemMeta.hasDisplayName()) {
                        String originMatch = bypassArgs[0].replaceAll("§.", "").trim();
                        String targetMatch = itemMeta.getDisplayName().replaceAll("§.", "").trim();

                        if (originMatch.equalsIgnoreCase(targetMatch) || originMatch.matches(targetMatch)) {
                            if (bypassArgs.length > 1 && itemMeta.hasLore()) {
                               originMatch = bypassArgs[1].replaceAll("§.", "");
                               targetMatch = itemMeta.getLore().stream().map(line -> line.replaceAll("§.","")).collect(Collectors.joining("\\n"));

                                if (originMatch.equalsIgnoreCase(targetMatch) || originMatch.matches(targetMatch)) {
                                    return;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        }

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

                if (percentage > targetPercentage) {
                    player.addPotionEffect(effect);
                }
            }
        }
    }
}
package cz.devfire.mysteryblocks.Block.Handler.MiningEffects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Tuple;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BlockMiningEffectsHandler extends AbstractBlockHandler {
    private final HashMap<String, Tuple<Double, Integer, Integer>> effectMap = Maps.newHashMap();

    private final HashMap<Player, Long> cooldownMap = Maps.newHashMap();
    private Long cooldownTime = 0L;

    private final ArrayList<String> bypassList = Lists.newArrayList();

    public BlockMiningEffectsHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("MiningEffects"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            for (String constantEffect : section.getStringList("Constant.List")) {
                String[] effectArgs = constantEffect.split("-");

                if (effectArgs.length != 2) {
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §cConstant PotionEffectType " + constantEffect + " is not valid!");
                } else {
                    String effectName = effectArgs[0];
                    int amplifier = Math.min(Math.max(Integer.parseInt(effectArgs[1]) - 1, 0), 255);
                    int duration = 20;

                    effectMap.put(effectName, new Tuple<>(100D, amplifier, duration));
                }
            }

            for (String percentageEffect : section.getStringList("Percentage.List")) {
                String[] effectArgs = percentageEffect.split("-");

                if (effectArgs.length != 4) {
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §cPercentage PotionEffectType " + percentageEffect + " is not valid!");
                } else {
                    double chance = Math.min(Math.max(Double.parseDouble(effectArgs[0]), 0), 100);
                    String effectName = effectArgs[1];
                    int amplifier = Math.min(Math.max(Integer.parseInt(effectArgs[2]) - 1, 0), 255);
                    int duration = Math.max(Integer.parseInt(effectArgs[3]), 20);

                    effectMap.put(effectName, new Tuple<>(chance, amplifier, duration));
                }
            }

            this.cooldownTime = section.getLong("Percentage.Cooldown");
            this.bypassList.addAll(section.getStringList("Bypass"));
        }

        return true;
    }

    public void apply(Player player) {
        if (!bypassList.isEmpty()) {
            ItemStack tool = Utils.getPlayerItemInHand(player);
            if (Utils.isItemBypassed(tool, bypassList)) return;
        }

        boolean skip = cooldownMap.getOrDefault(player, Long.MAX_VALUE) + cooldownTime > System.currentTimeMillis();

        for (String effect : effectMap.keySet()) {
            Double chance = effectMap.get(effect).getFirst();
            Integer amplifier = effectMap.get(effect).getSecond();
            Integer duration = effectMap.get(effect).getThird();

            try {
                PotionEffectType type = PotionEffectType.getByName(effect);
                if (type == null && !effect.equalsIgnoreCase("FREEZE")) {
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §cPotionEffectType " + effect + " is not valid!");
                    continue;
                }

                if (chance < 100) {
                    if (skip) continue;
                    cooldownMap.put(player, System.currentTimeMillis());

                    double targetPercentage = new Random().nextDouble() * 100;
                    if (targetPercentage > chance) continue;
                }

                if (effect.equalsIgnoreCase("FREEZE")) {
                    player.setFreezeTicks(duration * Math.max(amplifier, 1));
                    continue;
                }

                player.addPotionEffect(new PotionEffect(type, duration, amplifier));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
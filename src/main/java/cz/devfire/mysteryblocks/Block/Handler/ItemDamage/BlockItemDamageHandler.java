package cz.devfire.mysteryblocks.Block.Handler.ItemDamage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Pair;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BlockItemDamageHandler extends AbstractBlockHandler {
    private int itemDamage = 1;
    private boolean ignoreEnchants = false;

    private final ArrayList<String> bypassList = Lists.newArrayList();
    private final HashMap<Enchantment, List<Pair<Integer, Double>>> enchantmentModifiers = Maps.newHashMap();
    private final HashMap<PotionEffectType, List<Pair<Integer, Double>>> effectModifiers = Maps.newHashMap();

    public BlockItemDamageHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("ItemDamage"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            itemDamage = section.getInt("Damage");
            ignoreEnchants = section.getBoolean("IgnoreEnchants");
            bypassList.addAll(section.getStringList("Bypass"));

            for (String enchant : section.getStringList("Modifiers.Enchantments")) {
                String[] enchantArgs = enchant.split(":");

                for (Enchantment ench : Enchantment.values()) {
                    if (ench.getKey().getKey().equalsIgnoreCase(enchantArgs[0]) || ench.getName().equalsIgnoreCase(enchantArgs[0])) {
                        int level = Integer.parseInt(enchantArgs[1]);
                        double modifier = Double.parseDouble(enchantArgs[2]);

                        if (enchantmentModifiers.containsKey(ench)) {
                            enchantmentModifiers.get(ench).add(new Pair<>(level, modifier));
                        } else {
                            List<Pair<Integer, Double>> list = new ArrayList<>();
                            list.add(new Pair<>(level, modifier));
                            enchantmentModifiers.put(ench, list);
                        }
                    }
                }
            }

            for (String effect : section.getStringList("Modifiers.Effects")) {
                String[] effectArgs = effect.split(":");

                PotionEffectType potionEffectType = PotionEffectType.getByName(effectArgs[0]);
                int amplifier = Integer.parseInt(effectArgs[1]);
                double modifier = Double.parseDouble(effectArgs[2]);

                if (potionEffectType != null) {
                    if (effectModifiers.containsKey(potionEffectType)) {
                        effectModifiers.get(potionEffectType).add(new Pair<>(amplifier, modifier));
                    } else {
                        List<Pair<Integer, Double>> list = new ArrayList<>();
                        list.add(new Pair<>(amplifier, modifier));
                        effectModifiers.put(potionEffectType, list);
                    }
                }
            }
        }

        return true;
    }

    public void apply(Player player) {
        ItemStack tool = Utils.getPlayerItemInHand(player);

        if (tool == null || tool.getType() == Material.AIR || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!bypassList.isEmpty() && Utils.isItemBypassed(tool, bypassList)) {
            return;
        }

        if (!EnchantmentTarget.TOOL.includes(tool) && !EnchantmentTarget.WEAPON.includes(tool)) {
            return;
        }

        if (tool.getItemMeta() instanceof Damageable damageable) {
            if (itemDamage != 0 && !player.hasPermission(mysteryBlock.getPermission() + ".bypass.item-damage")) {
                double durability = damageable.getDamage();
                short maxDurability = tool.getType().getMaxDurability();

                if (durability + itemDamage >= maxDurability) {
                    Utils.setPlayerItemInHand(player, null);
                } else {
                    int unbreaking = tool.getEnchantmentLevel(Enchantment.DURABILITY);

                    if (unbreaking != 0 && !ignoreEnchants) {
                        float prc = 100 / (float) (unbreaking + 1);
                        float hit = new Random().nextInt(100);

                        if (prc < 1) {
                            prc = 1;
                        }

                        if (prc >= hit) {
                            damageable.setDamage((short) (damageable.getDamage() + itemDamage));
                            tool.setItemMeta(damageable);
                        }
                    } else {
                        damageable.setDamage((short) (damageable.getDamage() + itemDamage));
                        tool.setItemMeta(damageable);
                    }
                }
            }
        }
    }

    public int getDamage(Player player) {
        ItemStack tool = Utils.getPlayerItemInHand(player);
        double enchantModifier = 0;
        double effectModifier = 0;

        if (tool.getItemMeta() instanceof Damageable damageable) {
            for (Enchantment ench : tool.getEnchantments().keySet()) {
                if (enchantmentModifiers.containsKey(ench)) {
                    for (Pair<Integer, Double> pair : enchantmentModifiers.get(ench)) {
                        if (pair.getFirst() <= tool.getEnchantmentLevel(ench)) {
                            enchantModifier = pair.getSecond();
                        }
                    }
                }
            }

            for (PotionEffectType effect : player.getActivePotionEffects().stream().map(e -> e.getType()).toArray(PotionEffectType[]::new)) {
                if (effectModifiers.containsKey(effect)) {
                    for (Pair<Integer, Double> pair : effectModifiers.get(effect)) {
                        if (pair.getFirst() <= player.getPotionEffect(effect).getAmplifier()) {
                            effectModifier = pair.getSecond();
                        }
                    }
                }
            }
        }

        return (int) Math.round(1 * Math.max(enchantModifier + effectModifier, 1D));
    }
}
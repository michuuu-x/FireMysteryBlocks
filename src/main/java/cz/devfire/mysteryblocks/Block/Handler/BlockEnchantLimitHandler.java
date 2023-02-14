package cz.devfire.mysteryblocks.Block.Handler;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BlockEnchantLimitHandler extends AbstractBlockHandler {
    private final HashMap<Enchantment, Integer> map = Maps.newHashMap();
    private final ArrayList<String> bypassList = Lists.newArrayList();

    public BlockEnchantLimitHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("EnchantLimit"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            for (String enchant : section.getStringList("List")) {
                String[] enchantArgs = enchant.split(":");

                map.put(Enchantment.getByName(enchantArgs[0]), Integer.parseInt(enchantArgs[1]));
            }

            this.bypassList.addAll(section.getStringList("Bypass"));
        }

        return true;
    }

    public List<String> isValid(ItemStack itemStack) {
        List<String> list = Lists.newArrayList();

        if (!bypassList.isEmpty()) {
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
                                    return list;
                                }
                            } else {
                                return list;
                            }
                        }
                    }
                }
            }
        }

        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            int level = itemStack.getEnchantmentLevel(enchantment);
            int allowedLevel = map.getOrDefault(enchantment, Integer.MAX_VALUE);

            if (allowedLevel < level) {
                list.add(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, enchantment.getKey().getKey()) +"|"+ level +"|"+ allowedLevel);
            }
        }

        return list;
    }

    public HashMap<Enchantment, Integer> getMap() {
        return map;
    }
}
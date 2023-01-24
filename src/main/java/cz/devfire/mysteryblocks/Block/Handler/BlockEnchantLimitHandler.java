package cz.devfire.mysteryblocks.Block.Handler;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class BlockEnchantLimitHandler extends AbstractBlockHandler {
    private final HashMap<Enchantment, Integer> map = Maps.newHashMap();

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
        }

        return true;
    }

    public List<String> isValid(ItemStack itemStack) {
        List<String> list = Lists.newArrayList();

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
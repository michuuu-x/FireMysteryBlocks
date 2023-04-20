package cz.devfire.mysteryblocks.Block.Handler.EnchantLimit;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class BlockEnchantLimitHandler extends AbstractBlockHandler {
    private final HashMap<Enchantment, Integer> enchantList = Maps.newHashMap();
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

                try {
                    enchantList.put(Enchantment.getByName(enchantArgs[0]), Integer.parseInt(enchantArgs[1]));
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §cUnknown enchant: \"" + enchant + "\"");

                    if (MysteryBlocksPlugin.isDebugEnabled()) {
                        e.printStackTrace();
                    }
                }
            }

            this.bypassList.addAll(section.getStringList("Bypass"));
        }

        return true;
    }

    public List<String> isValid(ItemStack itemStack) {
        List<String> list = Lists.newArrayList();

        if (!bypassList.isEmpty()) {
            if (Utils.isItemBypassed(itemStack, bypassList)) return list;
        }

        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            int level = itemStack.getEnchantmentLevel(enchantment);
            int allowedLevel = enchantList.getOrDefault(enchantment, Integer.MAX_VALUE);

            if (allowedLevel < level) {
                list.add(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, enchantment.getKey().getKey()) +"|"+ level +"|"+ allowedLevel);
            }
        }

        return list;
    }
}
package cz.devfire.mysteryblocks.Block.Handler.ItemDamage;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Random;

public class BlockItemDamageHandler extends AbstractBlockHandler {
    private int itemDamage = 1;
    private boolean ignoreEnchants = false;

    private final ArrayList<String> bypassList = Lists.newArrayList();

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
        }

        return true;
    }

    public void apply(Player player) {
        ItemStack tool = Utils.getPlayerItemInHand(player);

        if (tool == null || tool.getType() == Material.AIR || player.getGameMode() == GameMode.CREATIVE) return;
        if (!EnchantmentTarget.TOOL.includes(tool)) return;

        if (!bypassList.isEmpty()) {
            if (Utils.isItemBypassed(tool, bypassList)) return;
        }

        if (tool.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) tool.getItemMeta();

            if (itemDamage != 0 && !player.hasPermission(mysteryBlock.getPermission() +".bypass.item-damage")) {
                double durability = damageable.getDamage();
                short maxDurability = tool.getType().getMaxDurability();

                if (durability + itemDamage >= maxDurability) {
                    Utils.setPlayerItemInHand(player,null);
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
}
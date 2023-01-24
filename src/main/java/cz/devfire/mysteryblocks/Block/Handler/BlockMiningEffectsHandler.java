package cz.devfire.mysteryblocks.Block.Handler;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class BlockMiningEffectsHandler extends AbstractBlockHandler {
    private final HashMap<PotionEffectType, Integer> map = Maps.newHashMap();

    public BlockMiningEffectsHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("MiningEffects"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            for (String effect : section.getStringList("List")) {
                String[] effectArgs = effect.split(":");

                map.put(PotionEffectType.getByName(effectArgs[0]), Integer.parseInt(effectArgs[1]));
            }
        }

        return true;
    }

    public void apply(Player player) {
        for (PotionEffectType effectType : map.keySet()) {
            if (!player.hasPotionEffect(effectType)) {
                ItemStack itemStack = player.getInventory().getItemInHand();
                player.getInventory().setItemInHand(null);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().setItemInHand(itemStack);
                    }
                }.runTaskLater(plugin,2);
            }

            player.addPotionEffect(new PotionEffect(effectType,20, Math.min(map.get(effectType) - 1, 0),true,false));
        }
    }

    public HashMap<PotionEffectType, Integer> getMap() {
        return map;
    }
}
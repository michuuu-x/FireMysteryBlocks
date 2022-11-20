package cz.devfire.mysteryblocks.Block.Listener;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class BlockMineListener implements Listener {
    private final MysteryBlocksPluginImpl plugin;

    private final HashMap<String, Long> mineMap = Maps.newHashMap();

    public BlockMineListener(MysteryBlocksPluginImpl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMineStart(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlockExact(4, FluidCollisionMode.NEVER);

        if (block != null) {
            MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlockAt(block.getLocation());

            if (mysteryBlock != null && mysteryBlock.isMiningEffectsEnabled()) {
                for (String effect : mysteryBlock.getMiningEffects().keySet()) {
                    PotionEffectType potion = PotionEffectType.getByName(effect);

                    if (potion != null) {
                        if (!player.hasPotionEffect(potion)) {
                            ItemStack itemStack = player.getInventory().getItemInMainHand();
                            player.getInventory().setItemInMainHand(null);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.getInventory().setItemInMainHand(itemStack);
                                }
                            }.runTaskLater(plugin, 2);
                        }

                        player.addPotionEffect(new PotionEffect(potion, 5, mysteryBlock.getMiningEffects().get(effect) - 1, true, false));
                    }
                }
            }
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.LOWEST)
    public void onMine(BlockBreakEvent event) {
        MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlockAt(event.getBlock().getLocation());
        Player player = event.getPlayer();
        ItemStack tool = player.getItemInHand();

        if (mysteryBlock != null) {
            event.setCancelled(true);

            long time = mineMap.getOrDefault(player.getName(),0L);
            if (time + 100 > System.currentTimeMillis()) {
                return;
            } else {
                mineMap.put(player.getName(), System.currentTimeMillis());
            }

            if (mysteryBlock.isCooldownEnabled() && mysteryBlock.getCooldownCurrent() != 0) {
                Language.BLOCK_COOLDOWN.send(player);
                return;
            }

            if (mysteryBlock.isPermissionEnabled() && !player.hasPermission(mysteryBlock.getPermission())) {
                Language.BLOCK_PERMISSION.send(player);
                return;
            }

            if (mysteryBlock.isEnchantLimitEnabled()) {
                if (tool != null && tool.getType() != Material.AIR && player.getGameMode() != GameMode.CREATIVE) {
                    for (Enchantment enchantment : tool.getEnchantments().keySet()) {
                        int level = tool.getEnchantmentLevel(enchantment);
                        String ench = enchantment.getKey().toString().split(":")[1].toUpperCase();

                        int allowedLevel = mysteryBlock.getEnchantLimits().getOrDefault(enchantment.getName(), Integer.MAX_VALUE);

                        if (allowedLevel < level) {
                            Language.ENCHANT_LIMIT.send(player, ench, level, allowedLevel);
                            return;
                        }
                    }
                }
            }

            if (mysteryBlock.isDurabilityEnabled()) {
                if (tool != null && tool.getType() != Material.AIR && player.getGameMode() != GameMode.CREATIVE) {
                    short durability = tool.getDurability();
                    short maxDurability = tool.getType().getMaxDurability();

                    if (durability + mysteryBlock.getDurabilityDamage() >= maxDurability) {
                        // TODO: Simulate item break
                        player.setItemInHand(null);
                    } else {
                        int unbreaking = tool.getEnchantmentLevel(Enchantment.DURABILITY);

                        if (unbreaking != 0) {
                            float prc = 100 / (float) (unbreaking + 1);
                            float hit = new Random().nextInt(100);

                            if (prc < 1) {
                                prc = 1;
                            }

                            if (prc >= hit) {
                                tool.setDurability((short) (tool.getDurability() + mysteryBlock.getDurabilityDamage()));
                            }
                        } else {
                            tool.setDurability((short) (tool.getDurability() + mysteryBlock.getDurabilityDamage()));
                        }
                    }
                }
            }

            if (mysteryBlock.getAntiAfkHandler().isEnabled() && mysteryBlock.getAntiAfkHandler().getMethod().canCheck(player)) {
                mysteryBlock.getAntiAfkHandler().getMethod().check(player);
            }

            mysteryBlock.mine(player);
            event.setDropItems(false);
        }
    }
}

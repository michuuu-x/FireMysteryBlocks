package cz.devfire.mysteryblocks.Block.Listener;

import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.Block.AntiAfk.Interface.AntiAfkMethod;
import cz.devfire.mysteryblocks.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Handler.BlockAntiCheatHandler;
import cz.devfire.mysteryblocks.Block.Handler.BlockCooldownHandler;
import cz.devfire.mysteryblocks.Block.Handler.BlockEnchantLimitHandler;
import cz.devfire.mysteryblocks.Block.Handler.BlockMiningEffectsHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
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
import org.bukkit.permissions.Permission;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class PlayerBlockMineListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    public PlayerBlockMineListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.LOWEST)
    public void onMineStart(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        Block targetBlock = Utils.getServerVersionID() == 8 ? player.getTargetBlock(Sets.newHashSet(),5) : player.getTargetBlockExact(5);

        if (targetBlock != null) {
            MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlockAt(targetBlock.getLocation());

            if (mysteryBlock != null) {
                BlockMiningEffectsHandler miningEffectsHandler = mysteryBlock.getMiningEffectsHandler();

                if (miningEffectsHandler != null && miningEffectsHandler.isEnabled() && !player.hasPermission(mysteryBlock.getPermission() +".bypass.mining-effect")) {
                    miningEffectsHandler.apply(player);
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

            BlockCooldownHandler cooldownHandler = mysteryBlock.getCooldownHandler();
            if (cooldownHandler.isEnabled() && cooldownHandler.isUnder()) {
                Language.BLOCK_COOLDOWN.send(player);
                return;
            }

            if (mysteryBlock.isPermissionRequired() && !player.hasPermission(mysteryBlock.getPermission() +".mine")) {
                Language.BLOCK_PERMISSION.send(player);
                return;
            }

            BlockEnchantLimitHandler enchantLimitHandler = mysteryBlock.getEnchantLimitHandler();
            if (enchantLimitHandler.isEnabled() && !player.hasPermission(mysteryBlock.getPermission() +".bypass.enchant-limit")) {
                if (tool != null && tool.getType() != Material.AIR && player.getGameMode() != GameMode.CREATIVE) {
                    List<String> badEnchants = enchantLimitHandler.isValid(tool);

                    if (badEnchants.size() != 0) {
                        for (String ench : badEnchants) {
                            String[] enchArgs = ench.split("\\|");

                            Language.ENCHANT_LIMIT.send(player, enchArgs[0], enchArgs[1], enchArgs[2]);
                        }

                        return;
                    }
                }
            }

            BlockAntiCheatHandler antiCheatHandler = mysteryBlock.getAntiCheatHandler();
            if (antiCheatHandler.isEnabled()) {
                antiCheatHandler.mine(player);
            }

            BlockAntiAfkHandler antiAfkHandler = mysteryBlock.getAntiAfkHandler();
            if (antiAfkHandler.isEnabled() && !player.hasPermission(mysteryBlock.getPermission() +".bypass.anti-afk")) {
                AntiAfkMethod antiAfkMethod = antiAfkHandler.getMethod();

                if (antiAfkMethod.canCheck(player)) {
                    antiAfkMethod.check(player);
                }
            }

            if (mysteryBlock.getItemDamage() != 0 && !player.hasPermission(mysteryBlock.getPermission() +".bypass.item-damage")) {
                if (tool != null && tool.getType() != Material.AIR && player.getGameMode() != GameMode.CREATIVE) {
                    short durability = tool.getDurability();
                    short maxDurability = tool.getType().getMaxDurability();

                    if (durability + mysteryBlock.getItemDamage() >= maxDurability) {
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
                                tool.setDurability((short) (tool.getDurability() + mysteryBlock.getItemDamage()));
                            }
                        } else {
                            tool.setDurability((short) (tool.getDurability() + mysteryBlock.getItemDamage()));
                        }
                    }
                }
            }

            mysteryBlock.mine(player);
        }
    }
}

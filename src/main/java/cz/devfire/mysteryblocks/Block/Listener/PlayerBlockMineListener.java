package cz.devfire.mysteryblocks.Block.Listener;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Interface.AntiAfkMethod;
import cz.devfire.mysteryblocks.Block.Handler.AntiCheat.BlockAntiCheatHandler;
import cz.devfire.mysteryblocks.Block.Handler.Cooldown.BlockCooldownHandler;
import cz.devfire.mysteryblocks.Block.Handler.EnchantLimit.BlockEnchantLimitHandler;
import cz.devfire.mysteryblocks.Block.Handler.ForceField.BlockForceFieldHandler;
import cz.devfire.mysteryblocks.Block.Handler.ItemDamage.BlockItemDamageHandler;
import cz.devfire.mysteryblocks.Block.Handler.MiningEffects.BlockMiningEffectsHandler;
import cz.devfire.mysteryblocks.Block.Handler.Visibility.BlockVisibilityHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Player.Object.MysteryPlayer;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class PlayerBlockMineListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    private final HashMap<Player, Long> cooldown = Maps.newHashMap();

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
            MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(player.getName());

            if (mysteryBlock != null) {
                BlockMiningEffectsHandler miningEffectsHandler = mysteryBlock.getMiningEffectsHandler();
                if (miningEffectsHandler != null && miningEffectsHandler.isEnabled() && !player.hasPermission(mysteryBlock.getPermission() +".bypass.mining-effect")) {
                    miningEffectsHandler.apply(player);
                }

                BlockVisibilityHandler visibilityHandler = mysteryBlock.getVisibilityHandler();
                if (visibilityHandler != null && visibilityHandler.isEnabled() && !player.hasPermission(mysteryBlock.getPermission() +".bypass.visibility")) {
                    if (mysteryPlayer.isVisualEnabled()) {
                        visibilityHandler.hideAll(player);
                    }
                }
            }
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGHEST)
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

            BlockForceFieldHandler forceFieldHandler = mysteryBlock.getForceFieldHandler();
            if (forceFieldHandler.isEnabled()) {
                if (forceFieldHandler.isProtectedEnabled() && forceFieldHandler.isInside(player, mysteryBlock)) {
                    player.sendMessage(Language.BLOCK_FORCE_FIELD_PROTECTED.getMessage());
                    return;
                }
            }

            BlockEnchantLimitHandler enchantLimitHandler = mysteryBlock.getEnchantLimitHandler();
            if (enchantLimitHandler.isEnabled() && !player.hasPermission(mysteryBlock.getPermission() +".bypass.enchant-limit")) {
                if (tool != null && tool.getType() != Material.AIR && player.getGameMode() != GameMode.CREATIVE) {
                    List<String> badEnchants = enchantLimitHandler.isValid(tool);

                    if (badEnchants.size() != 0) {
                        for (String ench : badEnchants) {
                            String[] enchArgs = ench.split("\\|");

                            if (cooldown.getOrDefault(player, Long.MAX_VALUE) + 1000 < System.currentTimeMillis()) {
                                Language.BLOCK_ENCHANT_LIMIT.send(player, enchArgs[0], enchArgs[1], enchArgs[2]);
                                cooldown.put(player, System.currentTimeMillis());
                            }
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

            BlockItemDamageHandler itemDamageHandler = mysteryBlock.getItemDamageHandler();
            if (itemDamageHandler.isEnabled() && !player.hasPermission(mysteryBlock.getPermission() +".bypass.item-damage")) {
                itemDamageHandler.apply(player);
            }

            mysteryBlock.mine(player, itemDamageHandler.isEnabled() ? itemDamageHandler.getDamage(player) : 1);
        }
    }
}

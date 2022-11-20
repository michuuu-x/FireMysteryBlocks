package cz.devfire.mysteryblocks.api.Block.Objects;

import cz.devfire.mysteryblocks.api.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Other.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;

public interface MysteryBlock {
    void load();

    void save();

    void save(boolean full);

    void remove();

    void reset();

    void reset(boolean full);

    void mine(Player player);

    void destroy();

    void redefine(Block block);

    void redefine(Location location);

    Config getConfig();

    Material getMaterial();

    String getName();

    Location getLocation();

    Block getBlock();

    HashMap<String, Integer> getMineMap();

    int getRequiredMines();

    int getTotalDestroys();

    int getCurrentMines();

    boolean isPermissionEnabled();

    String getPermission();

    BlockAntiAfkHandler getAntiAfkHandler();

    BlockHologramHandler getHologramHandler();

    boolean isDurabilityEnabled();

    boolean isEnchantLimitEnabled();

    HashMap<String, Integer> getEnchantLimits();

    boolean isMiningEffectsEnabled();

    HashMap<String, Integer> getMiningEffects();

    int getDurabilityDamage();

    boolean isCooldownEnabled();

    Material getCooldownBlock();

    long getCooldownRequired();

    long getCooldownCurrent();

    boolean isUnderCooldown();
}

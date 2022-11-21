package cz.devfire.mysteryblocks.api.Block.Objects;

import cz.devfire.mysteryblocks.api.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockAntiCheatHandler;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockCooldownHandler;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockEnchantLimitHandler;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockMiningEffectsHandler;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Other.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

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

    Material getMaterial();

    String getName();

    HashMap<String, Integer> getMineMap();

    HashMap<String, List<String>> getActionMap();

    int getItemDamage();

    boolean isPermissionRequired();

    Location getLocation();

    Block getBlock();

    int getRequiredMines();

    int getCurrentMines();

    int getTotalDestroys();

    BlockAntiAfkHandler getAntiAfkHandler();

    BlockHologramHandler getHologramHandler();

    BlockAntiCheatHandler getAntiCheatHandler();

    BlockCooldownHandler getCooldownHandler();

    BlockEnchantLimitHandler getEnchantLimitHandler();

    BlockMiningEffectsHandler getMiningEffectsHandler();

    Config getConfig();
}


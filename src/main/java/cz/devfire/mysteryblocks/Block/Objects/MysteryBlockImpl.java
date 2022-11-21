package cz.devfire.mysteryblocks.Block.Objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.AntiAfk.BlockAntiAfkHandlerImpl;
import cz.devfire.mysteryblocks.Block.Hologram.Handlers.BlockHologramHandlerImpl;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.ConfigImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import cz.devfire.mysteryblocks.api.Events.*;
import cz.devfire.mysteryblocks.api.Other.Config;
import cz.devfire.mysteryblocks.api.Other.Database.Types.DatabaseType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MysteryBlockImpl implements MysteryBlock {
    private final MysteryBlockImpl mysBlock;
    private final MysteryBlocksPluginImpl plugin;
    private final Material material;
    private final String name;
    private final HashMap<String, Integer> mineMap = Maps.newHashMap();
    private final int requiredMines;
    private final boolean durabilityEnabled;
    private final int durabilityDamage;
    private final boolean permissionEnabled;
    private final String permission;
    private final BlockAntiAfkHandler antiAfkHandler;
    private final BlockHologramHandler hologramHandler;
    private final boolean cooldownEnabled;
    private final Material cooldownBlock;
    private final long cooldownRequired;
    private final boolean enchantLimitEnabled;
    private final HashMap<String, Integer> enchantLimitList = Maps.newHashMap();
    private final boolean miningEffectsEnabled;
    private final HashMap<String, Integer> miningEffectsList = Maps.newHashMap();
    private final ArrayList<String> onResetActions = Lists.newArrayList();
    private final ArrayList<String> onMineActions = Lists.newArrayList();
    private final ArrayList<String> onDestroyGlobalActions = Lists.newArrayList();
    private final ArrayList<String> onDestroyPlaceActions = Lists.newArrayList();
    private final ArrayList<String> onDestroyEveryPlaceActions = Lists.newArrayList();
    private ConfigImpl config;
    private File configFile;
    private Location location;
    private Block block;
    private int currentMines;
    private int totalDestroys;
    private long cooldownCurrent = 0;

    public MysteryBlockImpl(MysteryBlocksPluginImpl plugin, String name) {
        this.plugin = plugin;
        this.mysBlock = this;

        try {
            this.configFile = new File(plugin.getDataFolder(),"blocks/" + name + ".yml");
            if (!this.configFile.exists()) this.configFile.createNewFile();

            this.config = ConfigImpl.loadConfiguration(this.configFile);
            this.config.syncWithConfig(this.configFile, plugin.getResource("blocks/first.yml"), "Action.OnDestroy.PerPlace", "AntiCheat.Action");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Block
        this.material = Material.valueOf(config.getString("Block.Material"));
        this.name = name;
        this.location = null;
        this.block = null;

        // Breaks
        this.requiredMines = config.getInt("Block.Limit");
        this.totalDestroys = 0;
        this.currentMines = 0;

        // Durability
        this.durabilityEnabled = config.getBoolean("Durability.Enabled");
        this.durabilityDamage = config.getInt("Durability.Damage");

        // Permissions
        this.permissionEnabled = config.getBoolean("Permissions.Enabled");
        this.permission = config.getString("Permissions.Permission");

        // AntiAfk
        this.antiAfkHandler = new BlockAntiAfkHandlerImpl(plugin, this);
        this.antiAfkHandler.load();

        // Hologram
        this.hologramHandler = new BlockHologramHandlerImpl(plugin, this);
        this.hologramHandler.init();

        // Cooldown
        this.cooldownEnabled = config.getBoolean("Cooldown.Enabled");
        this.cooldownBlock = Material.valueOf(config.getString("Cooldown.Material"));
        this.cooldownRequired = config.getLong("Cooldown.Time");
        this.cooldownCurrent = 0;

        // EnchantLimit
        this.enchantLimitEnabled = config.getBoolean("EnchantLimit.Enabled");

        for (String enchant : config.getStringList("EnchantLimit.List")) {
            String[] enchantArgs = enchant.split(":");

            this.enchantLimitList.put(enchantArgs[0].toUpperCase(), Integer.parseInt(enchantArgs[1]));
        }

        // MiningEffects
        this.miningEffectsEnabled = config.getBoolean("MiningEffects.Enabled");

        for (String effect : config.getStringList("MiningEffects.List")) {
            String[] effectArgs = effect.split(":");

            this.miningEffectsList.put(effectArgs[0].toUpperCase(), Integer.parseInt(effectArgs[1]));
        }

        // Actions
        this.onResetActions.addAll(config.getStringList("Action.OnReset"));
        this.onMineActions.addAll(config.getStringList("Action.OnMine"));
        this.onDestroyGlobalActions.addAll(config.getStringList("Action.OnDestroy.Global"));
        this.onDestroyEveryPlaceActions.addAll(config.getStringList("Action.OnDestroy.EveryPlace"));

        for (String place : config.getKeys("Action.OnDestroy.PerPlace")) {
            for (String action : config.getStringList("Action.OnDestroy.PerPlace." + place)) {
                this.onDestroyPlaceActions.add(place + " " + action);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                load();
            }
        }.runTaskLaterAsynchronously(plugin, 0);
    }

    public void load() {
        String loc = config.getString("Block.Location");
        Location location = Utils.getLocationFromString(loc);
        String[] locArgs = loc.split("/");

        World world = Bukkit.getWorld(locArgs[0]);
        if (world == null) {
            World firstWorld = Bukkit.getWorlds().get(0);
            location = new Location(firstWorld, Double.parseDouble(locArgs[1]), Double.parseDouble(locArgs[2]), Double.parseDouble(locArgs[3]));

            try {
                config.set("Block.Location", Utils.locationToString(location));
                config.save(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.location = location;
        this.block = location.getBlock();

        try {
            ResultSet rs = plugin.getDatabaseHandler().getDatabase().query("SELECT * FROM MysteryBlocksData WHERE name = '" + name + "'");

            if (rs.next()) {
                this.cooldownCurrent = rs.getLong("cooldown");
                this.totalDestroys = rs.getInt("destroys");
                this.currentMines = rs.getInt("mines");

                String playerDataLine = rs.getString("playerMines");

                if (!playerDataLine.isEmpty()) {
                    for (String playerData : playerDataLine.split("\\|")) {
                        String[] dataArgs = playerData.split("-");
                        this.mineMap.put(dataArgs[0], Integer.parseInt(dataArgs[1]));
                    }
                }
            }
        } catch (Exception e) {
            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        hologramHandler.load();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldownCurrent != 0) {
                    block.setType(cooldownBlock);
                } else {
                    block.setType(material);
                }

                if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
                    hologramHandler.getHologram().recreate();
                }

                Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockLoadEvent(mysBlock));
            }
        }.runTask(plugin);
    }

    public void save() {
        save(true);
    }

    public void save(boolean full) {
        String playerMap = "";

        for (String playerName : mineMap.keySet()) {
            playerMap += "|" + playerName + "-" + mineMap.get(playerName);
        }

        if (playerMap.startsWith("|")) {
            playerMap = playerMap.substring(1);
        }

        if (plugin.getDatabaseHandler().getDatabaseType() == DatabaseType.SQLITE) {
            plugin.getDatabaseHandler().getDatabase().update("REPLACE INTO MysteryBlocksData VALUES (NULL, '" + name + "', " + cooldownCurrent + ", " + totalDestroys + ", " + currentMines + ", '" + playerMap + "')");
        } else {
            plugin.getDatabaseHandler().getDatabase().update("INSERT INTO MysteryBlocksData VALUES (NULL, '" + name + "', " + cooldownCurrent + ", " + totalDestroys + ", " + currentMines + ", '" + playerMap + "') " +
                    "ON DUPLICATE KEY UPDATE cooldown = " + cooldownCurrent + ", destroys = " + totalDestroys + ", mines = " + currentMines + ", playerMines = '" + playerMap + "'");
        }

        if (full && hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().destroy();
        }
    }

    public void remove() {
        block.setType(Material.AIR);

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().destroy();
        }

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockUnloadEvent(this));
    }

    public void reset() {
        reset(true);
    }

    public void reset(boolean full) {
        block.setType(material);

        mineMap.clear();
        currentMines = 0;
        cooldownCurrent = 0;

        if (full) {
            try {
                Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockRespawnEvent(this));
                Utils.doActions(plugin, this, onResetActions);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "[FireMysteryBlocks] " + name + " | onResetActions is wrongly configured! Check your config!");

                if (plugin.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        }

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().update();
        }
    }

    public void mine(Player player) {
        mineMap.put(player.getName(), mineMap.getOrDefault(player.getName(), 0) + 1);
        currentMines++;

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockMineEvent(this, player));

        try {
            Utils.doActions(plugin, this, onMineActions, player.getName(), mineMap.get(player.getName()) + "");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[FireMysteryBlocks] " + name + " | onMineActions is wrongly configured! Check your config!");

            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        if (currentMines >= requiredMines) {
            destroy();
        } else {
            if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
                hologramHandler.getHologram().update();
            }
        }
    }

    public void destroy() {
        Map<String, Integer> map = Utils.sortMapByValue(mineMap, false);
        ArrayList<String> playerList = Lists.newArrayList();
        String playerListString = "$";
        String empty = Language.EMPTY.getMessage();

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockDestroyEvent(this));

        for (String playerName : map.keySet()) {
            playerList.add(playerName);
            playerList.add(map.get(playerName) + "");

            playerListString += "=" + playerName + "|" + map.get(playerName);
        }

        for (int i = playerList.size() / 2; i < 10; i++) {
            playerList.add(empty);
            playerList.add("0");

            playerListString += "=" + empty + "|" + 0;
        }

        try {
            Utils.doActions(plugin, this, onDestroyGlobalActions, playerListString);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[FireMysteryBlocks] " + name + " | onDestroyGlobalActions is wrongly configured! Check your config!");

            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        try {
            for (String playerName : map.keySet()) {
                Utils.doActions(plugin, this, onDestroyEveryPlaceActions, playerName);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[FireMysteryBlocks] " + name + " | onDestroyEveryPlaceActions is wrongly configured! Check your config!");

            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        try {
            Utils.doPlaceActions(plugin, this, onDestroyPlaceActions, playerList);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[FireMysteryBlocks] " + name + " | onDestroyPlaceActions is wrongly configured! Check your config!");

            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        if (cooldownEnabled) {
            block.setType(cooldownBlock);
            cooldownCurrent = System.currentTimeMillis();

            if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
                hologramHandler.getHologram().update();
            }
        } else {
            reset();
        }

        totalDestroys++;
    }

    public void redefine(Block block) {
        redefine(block.getLocation());
    }

    public void redefine(Location location) {
        this.block.setType(Material.AIR);

        this.location = location;
        this.block = location.getBlock();

        if (cooldownEnabled && cooldownCurrent != 0) {
            block.setType(cooldownBlock);
        } else {
            block.setType(material);
        }

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().recreate();
        }

        try {
            config.set("Block.Location", Utils.locationToString(location));
            config.save(configFile);

            config.syncWithConfig(this.configFile, plugin.getResource("blocks/first.yml"), "Action.OnDestroy.PerPlace", "AntiCheat.Action");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    // --- --- --- --- --- ---- ---
    //

    public Config getConfig() {
        return config;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Block getBlock() {
        return block;
    }

    public HashMap<String, Integer> getMineMap() {
        return mineMap;
    }

    public int getRequiredMines() {
        return requiredMines;
    }

    public int getTotalDestroys() {
        return totalDestroys;
    }

    public int getCurrentMines() {
        return currentMines;
    }

    public boolean isPermissionEnabled() {
        return permissionEnabled;
    }

    public String getPermission() {
        return permission;
    }

    public BlockAntiAfkHandler getAntiAfkHandler() {
        return antiAfkHandler;
    }

    public BlockHologramHandler getHologramHandler() {
        return hologramHandler;
    }

    public boolean isEnchantLimitEnabled() {
        return enchantLimitEnabled;
    }

    public HashMap<String, Integer> getEnchantLimits() {
        return enchantLimitList;
    }

    public boolean isMiningEffectsEnabled() {
        return miningEffectsEnabled;
    }

    public HashMap<String, Integer> getMiningEffects() {
        return miningEffectsList;
    }

    public boolean isDurabilityEnabled() {
        return durabilityEnabled;
    }

    public int getDurabilityDamage() {
        return durabilityDamage;
    }

    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    public Material getCooldownBlock() {
        return cooldownBlock;
    }

    public long getCooldownRequired() {
        return cooldownRequired;
    }

    public long getCooldownCurrent() {
        return cooldownCurrent;
    }

    public long getCooldown() {
        return cooldownCurrent + cooldownRequired - System.currentTimeMillis();
    }

    public boolean isUnderCooldown() {
        if (!cooldownEnabled) return false;

        return cooldownCurrent + cooldownRequired - System.currentTimeMillis() > 0;
    }
}

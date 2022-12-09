package cz.devfire.mysteryblocks.Block.Objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.AntiAfk.BlockAntiAfkHandlerImpl;
import cz.devfire.mysteryblocks.Block.Handlers.BlockAntiCheatHandlerImpl;
import cz.devfire.mysteryblocks.Block.Handlers.BlockCooldownHandlerImpl;
import cz.devfire.mysteryblocks.Block.Handlers.BlockEnchantLimitHandlerImpl;
import cz.devfire.mysteryblocks.Block.Handlers.BlockMiningEffectsHandlerImpl;
import cz.devfire.mysteryblocks.Block.Hologram.Handlers.BlockHologramHandlerImpl;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.ConfigImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockAntiCheatHandler;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockCooldownHandler;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockEnchantLimitHandler;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockMiningEffectsHandler;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MysteryBlockImpl implements MysteryBlock {
    private final MysteryBlockImpl mysBlock;
    private final MysteryBlocksPluginImpl plugin;

    private final Material material;
    private final String name;
    private final Map<String, Integer> mineMap = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
    private final HashMap<String, List<String>> actionMap = Maps.newHashMap();
    private final int itemDamage;
    private final boolean permission;
    private final int requiredMines;
    private final BlockAntiAfkHandler antiAfkHandler;
    private final BlockHologramHandler hologramHandler;
    private final BlockAntiCheatHandler antiCheatHandler;
    private final BlockCooldownHandler cooldownHandler;
    private final BlockEnchantLimitHandler enchantLimitHandler;
    private final BlockMiningEffectsHandler miningEffectsHandler;
    private Location location;
    private int currentMines;
    private int totalDestroys;
    private ConfigImpl config;
    private File configFile;

    public MysteryBlockImpl(MysteryBlocksPluginImpl plugin, String name) {
        this.plugin = plugin;
        this.mysBlock = this;

        try {
            this.configFile = new File(plugin.getDataFolder(), "blocks/" + name + ".yml");
            if (!this.configFile.exists()) this.configFile.createNewFile();

            this.config = ConfigImpl.loadConfiguration(this.configFile);
            this.config.syncWithConfig(this.configFile, plugin.getResource("blocks/first.yml"), "Action.OnDestroy.PerPlace", "AntiCheat.Action");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Block
        this.material = Material.valueOf(config.getString("Block.Material"));
        this.name = name;
        this.itemDamage = config.getInt("Block.ItemDamage");
        this.permission = config.getBoolean("Block.Permission");
        this.location = null;

        // Breaks
        this.requiredMines = config.getInt("Block.Limit");
        this.totalDestroys = 0;
        this.currentMines = 0;

        // Handlers
        this.antiAfkHandler = new BlockAntiAfkHandlerImpl(plugin, this);
        this.antiCheatHandler = new BlockAntiCheatHandlerImpl(plugin, this);
        this.hologramHandler = new BlockHologramHandlerImpl(plugin, this);
        this.cooldownHandler = new BlockCooldownHandlerImpl(plugin, this);
        this.enchantLimitHandler = new BlockEnchantLimitHandlerImpl(plugin, this);
        this.miningEffectsHandler = new BlockMiningEffectsHandlerImpl(plugin, this);

        // Actions
        this.actionMap.put("onReset", config.getStringList("Action.OnReset"));
        this.actionMap.put("onMine", config.getStringList("Action.OnMine"));
        this.actionMap.put("onDestroyGlobal", config.getStringList("Action.OnDestroy.Global"));
        this.actionMap.put("onDestroyEveryPlace", config.getStringList("Action.OnDestroy.EveryPlace"));
        this.actionMap.put("onDestroyPerPlace", Lists.newArrayList());

        for (String place : config.getKeys("Action.OnDestroy.PerPlace")) {
            for (String action : config.getStringList("Action.OnDestroy.PerPlace." + place)) {
                this.actionMap.get("onDestroyPerPlace").add(place + " " + action);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                load();
            }
        }.runTaskLaterAsynchronously(plugin, 0);
    }

    @Override
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

        try {
            ResultSet rs = plugin.getDatabaseHandler().getDatabase().query("SELECT * FROM MysteryBlocksData WHERE name = '" + name + "'");

            if (rs.next()) {
                cooldownHandler.setCurrent(rs.getLong("cooldown"));
                totalDestroys = rs.getInt("destroys");
                currentMines = rs.getInt("mines");

                if (!rs.getString("playerMines").isEmpty()) {
                    for (String playerData : rs.getString("playerMines").split("\\|")) {
                        String[] dataArgs = playerData.split("-");
                        mineMap.put(dataArgs[0], Integer.parseInt(dataArgs[1]));
                    }
                }
            }
        } catch (Exception e) {
            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        this.location = location;
        hologramHandler.load();

        Block block = location.getBlock();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldownHandler.getCurrent() != 0) {
                    block.setType(cooldownHandler.getMaterial());
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

    @Override
    public void save() {
        save(true);
    }

    @Override
    public void save(boolean full) {
        String playerMap = mineMap.keySet().stream().map(p -> p + "-" + mineMap.get(p)).collect(Collectors.joining("|"));

        if (plugin.getDatabaseHandler().getDatabaseType() == DatabaseType.SQLITE) {
            plugin.getDatabaseHandler().getDatabase().update("" +
                    "REPLACE INTO MysteryBlocksData " +
                    "VALUES (NULL, '" + name + "', " + cooldownHandler.getCurrent() + ", " + totalDestroys + ", " + currentMines + ", '" + playerMap + "')");
        } else {
            plugin.getDatabaseHandler().getDatabase().update("" +
                    "INSERT INTO MysteryBlocksData " +
                    "VALUES (NULL, '" + name + "', " + cooldownHandler.getCurrent() + ", " + totalDestroys + ", " + currentMines + ", '" + playerMap + "') " +
                    "ON DUPLICATE KEY " +
                    "UPDATE cooldown = " + cooldownHandler.getCurrent() + ", destroys = " + totalDestroys + ", mines = " + currentMines + ", playerMines = '" + playerMap + "'");
        }

        if (full && hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().destroy();
        }
    }

    @Override
    public void remove() {
        location.getBlock().setType(Material.AIR);

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().destroy();
        }

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockUnloadEvent(this));
    }

    @Override
    public void reset() {
        reset(true);
    }

    @Override
    public void reset(boolean full) {
        location.getBlock().setType(material);

        mineMap.clear();
        currentMines = 0;
        cooldownHandler.setCurrent(0);

        if (full) {
            try {
                Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockRespawnEvent(this));
                Utils.doActions(plugin, this, actionMap.get("onReset"));
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

    @Override
    public void mine(Player player) {
        mineMap.put(player.getName(), mineMap.getOrDefault(player.getName(), 0) + 1);
        currentMines++;

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockMineEvent(this, player));

        try {
            Utils.doActions(plugin, this, actionMap.get("onMine"), player.getName(), mineMap.get(player.getName()) + "");
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

    @Override
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

        for (int i = playerList.size(); i < 10; i++) {
            playerList.add(empty);
            playerList.add("0");

            playerListString += "=" + empty + "|" + 0;
        }

        try {
            Utils.doActions(plugin, this, actionMap.get("onDestroyGlobal"), playerListString);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[FireMysteryBlocks] " + name + " | onDestroyGlobalActions is wrongly configured! Check your config!");

            if (plugin.isDebugEnabled()) e.printStackTrace();
        }

        try {
            for (String playerName : map.keySet()) {
                Utils.doActions(plugin, this, actionMap.get("onDestroyEveryPlace"), playerName);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[FireMysteryBlocks] " + name + " | onDestroyEveryPlaceActions is wrongly configured! Check your config!");

            if (plugin.isDebugEnabled()) e.printStackTrace();
        }

        try {
            Utils.doPlaceActions(plugin, this, actionMap.get("onDestroyPerPlace"), playerList);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[FireMysteryBlocks] " + name + " | onDestroyPlaceActions is wrongly configured! Check your config!");

            if (plugin.isDebugEnabled()) e.printStackTrace();
        }

        if (cooldownHandler.isEnabled()) {
            location.getBlock().setType(cooldownHandler.getMaterial());
            cooldownHandler.setCurrent(System.currentTimeMillis());
        } else {
            reset();
        }

        totalDestroys++;
    }

    @Override
    public void redefine(Block block) {
        redefine(block.getLocation());
    }

    @Override
    public void redefine(Location location) {
        this.location.getBlock().setType(Material.AIR);
        this.location = location;

        if (cooldownHandler.isEnabled() && cooldownHandler.getCurrent() != 0) {
            this.location.getBlock().setType(cooldownHandler.getMaterial());
        } else {
            this.location.getBlock().setType(material);
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


    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Integer> getMineMap() {
        return mineMap;
    }

    @Override
    public HashMap<String, List<String>> getActionMap() {
        return actionMap;
    }

    @Override
    public int getItemDamage() {
        return itemDamage;
    }

    @Override
    public boolean isPermissionRequired() {
        return permission;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Block getBlock() {
        return location.getBlock();
    }

    @Override
    public int getRequiredMines() {
        return requiredMines;
    }

    @Override
    public int getCurrentMines() {
        return currentMines;
    }

    @Override
    public int getTotalDestroys() {
        return totalDestroys;
    }

    @Override
    public BlockAntiAfkHandler getAntiAfkHandler() {
        return antiAfkHandler;
    }

    @Override
    public BlockHologramHandler getHologramHandler() {
        return hologramHandler;
    }

    @Override
    public BlockAntiCheatHandler getAntiCheatHandler() {
        return antiCheatHandler;
    }

    @Override
    public BlockCooldownHandler getCooldownHandler() {
        return cooldownHandler;
    }

    @Override
    public BlockEnchantLimitHandler getEnchantLimitHandler() {
        return enchantLimitHandler;
    }

    @Override
    public BlockMiningEffectsHandler getMiningEffectsHandler() {
        return miningEffectsHandler;
    }

    @Override
    public Config getConfig() {
        return config;
    }
}

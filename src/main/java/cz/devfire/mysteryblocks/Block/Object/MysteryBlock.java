package cz.devfire.mysteryblocks.Block.Object;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Action.Enum.BlockActionSection;
import cz.devfire.mysteryblocks.Block.Action.BlockMineActionHandler;
import cz.devfire.mysteryblocks.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Handler.BlockClickHandler;
import cz.devfire.mysteryblocks.Block.Handler.BlockGUIHandler;
import cz.devfire.mysteryblocks.Block.Handler.*;
import cz.devfire.mysteryblocks.Block.Handler.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.History.BlockHistoryHandler;
import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Object.Results;
import cz.devfire.mysteryblocks.Listener.Event.*;
import cz.devfire.mysteryblocks.Files.Config;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MysteryBlock {
    private final MysteryBlocksPlugin plugin;
    private final MysteryBlock block;

    private File file;
    private Config config;

    private final Material material;
    private final String name;
    private LinkedHashMap<String, Integer> mineMap = Maps.newLinkedHashMap();
    private ArrayList<String> mineList = Lists.newArrayList();
    private final int itemDamage;
    private final boolean permissionRequired;
    private final int requiredMines;
    private Location location = null;
    private int currentMines = 0;
    private int totalDestroys = 0;

    private final BlockGUIHandler guiHandler;
    private final BlockClickHandler clickHandler;
    private final BlockAntiAfkHandler antiAfkHandler;
    private final BlockHistoryHandler historyHandler;
    private final BlockCooldownHandler cooldownHandler;
    private final BlockHologramHandler hologramHandler;
    private final BlockAntiCheatHandler antiCheatHandler;
    private final BlockMineActionHandler mineActionHandler;
    private final BlockEnchantLimitHandler enchantLimitHandler;
    private final BlockMiningEffectsHandler miningEffectsHandler;

    public MysteryBlock(MysteryBlocksPlugin plugin, String blockName) {
        this.plugin = plugin;
        this.block = this;

        try {
            this.file = new File(plugin.getDataFolder(),"blocks/"+ blockName +".yml");
            if (!this.file.exists()) this.file.createNewFile();

            this.config = Config.loadConfiguration(this.file);
            this.config.syncWithConfig(file, plugin.getResource("blocks/first.yml"),"Action.OnDestroy.PerPlace", "AntiCheat.Action", "GUI.Items", "GUI.Actions");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Block
        this.material = Material.valueOf(config.getString("Block.Material","STONE"));
        this.name = blockName;
        this.mineMap.clear();
        this.itemDamage = config.getInt("Block.ItemDamage");
        this.permissionRequired = config.getBoolean("Block.Permission");
        this.requiredMines = config.getInt("Block.Limit");

        // Handlers
        this.guiHandler = new BlockGUIHandler(plugin,this);
        this.clickHandler = new BlockClickHandler(plugin,this);
        this.antiAfkHandler = new BlockAntiAfkHandler(plugin,this);
        this.historyHandler = new BlockHistoryHandler(plugin,this);
        this.cooldownHandler = new BlockCooldownHandler(plugin,this);
        this.hologramHandler = new BlockHologramHandler(plugin,this);
        this.antiCheatHandler = new BlockAntiCheatHandler(plugin,this);
        this.mineActionHandler = new BlockMineActionHandler(plugin,this);
        this.enchantLimitHandler = new BlockEnchantLimitHandler(plugin,this);
        this.miningEffectsHandler = new BlockMiningEffectsHandler(plugin,this);

        new BukkitRunnable() {
            @Override
            public void run() {
                load();
            }
        }.runTaskLaterAsynchronously(plugin,0);
    }

    public void load() {
        String stringLocation = config.getString("Block.Location");
        String[] locArgs = stringLocation.split("/");

        World world = Bukkit.getWorld(locArgs[0]);
        if (world == null) {
            World firstWorld = Bukkit.getWorlds().get(0);
            location = new Location(firstWorld, Double.parseDouble(locArgs[1]), Double.parseDouble(locArgs[2]), Double.parseDouble(locArgs[3]));

            try {
                config.set("Block.Location", Utils.putLocationToString(location));
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            location = Utils.getLocationFromString(stringLocation);
        }

        try {
            Results rs = plugin.getDatabaseHandler().getDatabase().query("SELECT * FROM MysteryBlocksData WHERE name = ?", name);

            if (rs.next()) {
                cooldownHandler.setCurrentTime(rs.getLong("cooldown"));
                totalDestroys = (int) rs.getLong("destroys");
                currentMines = (int) rs.getLong("mines");

                if (!rs.getString("playerMines").isEmpty()) {
                    for (String playerData : rs.getString("playerMines").split("\\|")) {
                        String[] dataArgs = playerData.split("-");
                        mineMap.put(dataArgs[0], Integer.parseInt(dataArgs[1]));
                    }

                }
            }

            update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldownHandler.isEnabled() && cooldownHandler.isUnder()) {
                    location.getBlock().setType(cooldownHandler.getMaterial());
                } else {
                    location.getBlock().setType(material);
                }

                Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockLoadEvent(block));
            }
        }.runTask(plugin);

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().recreate();
        }
    }

    public void save() {
        String playerMap = mineMap.keySet().stream().map(p -> p + "-" + mineMap.get(p)).collect(Collectors.joining("|"));

        if (plugin.getDatabaseHandler().getDatabaseType() == DatabaseType.SQLITE) {
            plugin.getDatabaseHandler().getDatabase().update("" +
                    "REPLACE INTO MysteryBlocksData VALUES (NULL, ?, ?, ?, ?, ?)",
                    name, cooldownHandler.getCurrentTime(), totalDestroys, currentMines, playerMap
            );
        } else {
            plugin.getDatabaseHandler().getDatabase().update("" +
                    "INSERT INTO MysteryBlocksData VALUES (NULL, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE cooldown = ?, destroys = ?, mines = ?, playerMines = ?",
                    name, cooldownHandler.getCurrentTime(), totalDestroys, currentMines, playerMap, cooldownHandler.getCurrentTime(), totalDestroys, currentMines, playerMap
            );
        }
    }

    public void update() {
        mineMap = Utils.sortMapByValue(mineMap,false);
        mineList = mineMap.entrySet().stream().limit(10).map(e -> e.getKey() +"|"+ e.getValue()).collect(Collectors.toCollection(ArrayList::new));
    }

    public void destroy() {
        location.getBlock().setType(Material.AIR);

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().destroy();
        }

        if (guiHandler.isEnabled()) {
            guiHandler.destroy();
        }

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockUnloadEvent(this));
    }

    public void reset(boolean force) {
        location.getBlock().setType(material);

        mineMap.clear();
        currentMines = 0;
        cooldownHandler.setCurrentTime(0);

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockRespawnEvent(this));

        if (!force) {
            try {
                mineActionHandler.perform(BlockActionSection.RESET,null);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c"+ name +" | onResetActions is wrongly configured! Check your config!");
                e.printStackTrace();
            }
        }

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().update();
        }
    }

    public void mine(Player player) {
        mineMap.put(player.getName(), mineMap.getOrDefault(player.getName(),0) + 1);
        update();
        currentMines++;

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockMineEvent(this, player));

        try {
            mineActionHandler.perform(BlockActionSection.MINE, player.getName());
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c"+ name +" | onMineActions is wrongly configured! Check your config!");
            e.printStackTrace();
        }

        if (currentMines >= requiredMines) {
            broke();
        } else {
            if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
                hologramHandler.getHologram().update();
            }
        }
    }

    public void broke() {
        totalDestroys++;

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockDestroyEvent(this));

        try {
            mineActionHandler.perform(BlockActionSection.DESTROY_GLOBAL,null);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c"+ name +" | onDestroyGlobalActions is wrongly configured! Check your config!");
            e.printStackTrace();
        }

        try {
            mineActionHandler.perform(BlockActionSection.DESTROY_EVERY_PLACE,null);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c"+ name +" | onDestroyEveryPlaceActions is wrongly configured! Check your config!");
            e.printStackTrace();
        }

        try {
            mineActionHandler.perform(BlockActionSection.DESTROY_PER_PLACE,null);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c"+ name +" | onDestroyPlaceActions is wrongly configured! Check your config!");
            e.printStackTrace();
        }

        if (historyHandler.isEnabled()) {
            historyHandler.save();
        }

        if (cooldownHandler.isEnabled()) {
            location.getBlock().setType(cooldownHandler.getMaterial());
            cooldownHandler.setCurrentTime(System.currentTimeMillis());
        } else {
            reset(false);
        }
    }

    public void redefine(Location location) {
        this.location.getBlock().setType(Material.AIR);
        this.location = location;

        if (cooldownHandler.isEnabled() && cooldownHandler.getCurrentTime() != 0) {
            this.location.getBlock().setType(cooldownHandler.getMaterial());
        } else {
            this.location.getBlock().setType(material);
        }

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().recreate();
        }

        try {
            config.set("Block.Location", Utils.putLocationToString(location));
            config.save(file);
            config.syncWithConfig(file, plugin.getResource("blocks/first.yml"),"Action.OnDestroy.PerPlace", "AntiCheat.Action", "GUI.Items", "GUI.Actions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentMines(int number) {
        this.currentMines = number;
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

    public LinkedHashMap<String, Integer> getMineMap() {
        return mineMap;
    }

    public int getItemDamage() {
        return itemDamage;
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public int getRequiredMines() {
        return requiredMines;
    }

    public Location getLocation() {
        return location;
    }

    public int getCurrentMines() {
        return currentMines;
    }

    public int getTotalDestroys() {
        return totalDestroys;
    }

    public String getPermission() { return "firemysteryblocks."+ name; }

    public ArrayList<String> getMineList() {
        return mineList;
    }

    public BlockGUIHandler getGUIHandler() {
        return guiHandler;
    }

    public BlockClickHandler getClickHandler() {
        return clickHandler;
    }

    public BlockAntiAfkHandler getAntiAfkHandler() {
        return antiAfkHandler;
    }

    public BlockHistoryHandler getHistoryHandler() {
        return historyHandler;
    }

    public BlockCooldownHandler getCooldownHandler() {
        return cooldownHandler;
    }

    public BlockHologramHandler getHologramHandler() {
        return hologramHandler;
    }

    public BlockAntiCheatHandler getAntiCheatHandler() {
        return antiCheatHandler;
    }

    public BlockMineActionHandler getMineActionHandler() {
        return mineActionHandler;
    }

    public BlockEnchantLimitHandler getEnchantLimitHandler() {
        return enchantLimitHandler;
    }

    public BlockMiningEffectsHandler getMiningEffectsHandler() {
        return miningEffectsHandler;
    }
}

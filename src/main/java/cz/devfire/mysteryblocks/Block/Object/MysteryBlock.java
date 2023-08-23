package cz.devfire.mysteryblocks.Block.Object;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.Action.BlockMineActionHandler;
import cz.devfire.mysteryblocks.Block.Handler.Action.Enum.BlockActionSection;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Handler.AntiCheat.BlockAntiCheatHandler;
import cz.devfire.mysteryblocks.Block.Handler.Click.BlockClickHandler;
import cz.devfire.mysteryblocks.Block.Handler.Cooldown.BlockCooldownHandler;
import cz.devfire.mysteryblocks.Block.Handler.EnchantLimit.BlockEnchantLimitHandler;
import cz.devfire.mysteryblocks.Block.Handler.ForceField.BlockForceFieldHandler;
import cz.devfire.mysteryblocks.Block.Handler.GUI.BlockGUIHandler;
import cz.devfire.mysteryblocks.Block.Handler.History.BlockHistoryHandler;
import cz.devfire.mysteryblocks.Block.Handler.Hologram.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Handler.ItemDamage.BlockItemDamageHandler;
import cz.devfire.mysteryblocks.Block.Handler.MiningEffects.BlockMiningEffectsHandler;
import cz.devfire.mysteryblocks.Block.Handler.Regeneration.BlockRegenerationHandler;
import cz.devfire.mysteryblocks.Block.Handler.Schedule.BlockScheduleHandler;
import cz.devfire.mysteryblocks.Block.Handler.Visibility.BlockVisibilityHandler;
import cz.devfire.mysteryblocks.Database.Enum.DatabaseType;
import cz.devfire.mysteryblocks.Database.Object.Results;
import cz.devfire.mysteryblocks.Files.Config;
import cz.devfire.mysteryblocks.Listener.*;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class MysteryBlock {
    private final MysteryBlocksPlugin plugin;
    private final MysteryBlock block;

    private File file;
    private Config config;

    private final Material material;
    private final String name;
    private LinkedHashMap<String, Integer> mineMap = Maps.newLinkedHashMap();
    private ArrayList<String> mineList = Lists.newArrayList();
    private final boolean permissionRequired;
    private final int requiredMines;
    private int requiredTempMines;
    private Location location = null;
    private int currentMines = 0;
    private int totalDestroys = 0;
    private long lastMine = Long.MAX_VALUE;
    private long lastReset = Long.MAX_VALUE;

    private final BlockGUIHandler guiHandler;
    private final BlockClickHandler clickHandler;
    private final BlockAntiAfkHandler antiAfkHandler;
    private final BlockHistoryHandler historyHandler;
    private final BlockCooldownHandler cooldownHandler;
    private final BlockScheduleHandler scheduleHandler;
    private final BlockHologramHandler hologramHandler;
    private final BlockAntiCheatHandler antiCheatHandler;
    private final BlockMineActionHandler mineActionHandler;
    private final BlockItemDamageHandler itemDamageHandler;
    private final BlockVisibilityHandler visibilityHandler;
    private final BlockForceFieldHandler forceFieldHandler;
    private final BlockEnchantLimitHandler enchantLimitHandler;
    private final BlockRegenerationHandler regenerationHandler;
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
        this.permissionRequired = config.getBoolean("Block.Permission");
        this.requiredMines = config.getInt("Block.Limit");

        // Handlers
        this.guiHandler = new BlockGUIHandler(plugin,this);
        this.clickHandler = new BlockClickHandler(plugin,this);
        this.antiAfkHandler = new BlockAntiAfkHandler(plugin,this);
        this.historyHandler = new BlockHistoryHandler(plugin,this);
        this.hologramHandler = new BlockHologramHandler(plugin,this);
        this.cooldownHandler = new BlockCooldownHandler(plugin,this);
        this.scheduleHandler = new BlockScheduleHandler(plugin,this);
        this.antiCheatHandler = new BlockAntiCheatHandler(plugin,this);
        this.mineActionHandler = new BlockMineActionHandler(plugin,this);
        this.itemDamageHandler = new BlockItemDamageHandler(plugin,this);
        this.visibilityHandler = new BlockVisibilityHandler(plugin,this);
        this.forceFieldHandler = new BlockForceFieldHandler(plugin,this);
        this.enchantLimitHandler = new BlockEnchantLimitHandler(plugin,this);
        this.regenerationHandler = new BlockRegenerationHandler(plugin,this);
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

            if (rs.hasNext()) {
                rs.next();
                cooldownHandler.setCurrentTime(rs.getLong("cooldown"));
                regenerationHandler.setLastProcess(rs.getLong("regeneration"));
                totalDestroys = (int) rs.getLong("destroys");
                currentMines = (int) rs.getLong("mines");
                requiredTempMines = (int) rs.getLong("requiredMines");
                lastMine = rs.getLong("lastMine");

//                if (currentMines != 0) {
//                    lastMine = System.currentTimeMillis();
//                }

                if (scheduleHandler.isEnabled()) {
                    lastReset = scheduleHandler.prev().getTime();
                }

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
                    location.getBlock().setType(cooldownHandler.getCooldownMaterial());
                } else {
                    if (scheduleHandler.isEnabled() && scheduleHandler.isAutoDestroyEnabled()) {
                        if (scheduleHandler.prev().getTime() + scheduleHandler.getDestroyTime() < System.currentTimeMillis()) {
                            location.getBlock().setType(cooldownHandler.getCooldownMaterial());
                            cooldownHandler.setCurrentTime(System.currentTimeMillis());
                        } else {
                            location.getBlock().setType(material);
                        }
                    } else {
                        location.getBlock().setType(material);
                    }
                }
            }
        }.runTask(plugin);

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().recreate();
        }

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockLoadEvent(block));
    }

    public void save() {
        String playerMap = mineMap.keySet().stream().map(p -> p + "-" + mineMap.get(p)).collect(Collectors.joining("|"));

        if (plugin.getDatabaseHandler().getDatabaseType() == DatabaseType.SQLITE) {
            plugin.getDatabaseHandler().getDatabase().update("" +
                    "REPLACE INTO MysteryBlocksData (`name`,`cooldown`,`regeneration`,`destroys`,`mines`,`requiredMines`,`lastMine`,`playerMines`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    name,
                    cooldownHandler.getCurrentTime(), regenerationHandler.getLastProcess(), totalDestroys, currentMines, requiredTempMines, lastMine, playerMap
            );
        } else {
            plugin.getDatabaseHandler().getDatabase().update("" +
                    "INSERT INTO MysteryBlocksData (`name`,`cooldown`,`regeneration`,`destroys`,`mines`,`requiredMines`,`lastMine`,`playerMines`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE cooldown = ?, regeneration = ?, destroys = ?, mines = ?, requiredMines = ?, lastMine = ?, playerMines = ?",
                    name,
                    cooldownHandler.getCurrentTime(), regenerationHandler.getLastProcess(), totalDestroys, currentMines, requiredTempMines, lastMine, playerMap,
                    cooldownHandler.getCurrentTime(), regenerationHandler.getLastProcess(), totalDestroys, currentMines, requiredTempMines, lastMine, playerMap
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
        mineList.clear();
        currentMines = 0;
        requiredTempMines = 0;
        cooldownHandler.setCurrentTime(0);
        lastMine = 0;
        lastReset = System.currentTimeMillis();

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockRespawnEvent(this));

        if (!force) {
            try {
                mineActionHandler.perform(BlockActionSection.RESET,null);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c"+ name +" | onResetActions is wrongly configured! Check your config!");
                e.printStackTrace();
            }
        }

        if (scheduleHandler.isEnabled()) {
            scheduleHandler.convert();
        }

        if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
            hologramHandler.getHologram().update();
        }
    }

    public void mine(Player player) {
        mineMap.put(player.getName(), mineMap.getOrDefault(player.getName(),0) + 1);
        update();
        currentMines++;
        lastMine = System.currentTimeMillis();

        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockMineEvent(this, player));

        try {
            mineActionHandler.perform(BlockActionSection.MINE, player.getName());
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c"+ name +" | onMineActions is wrongly configured! Check your config!");
            e.printStackTrace();
        }

        if (currentMines >= requiredMines + requiredTempMines) {
            broke(false);
        } else {
            if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
                hologramHandler.getHologram().update();
            }
        }
    }

    public void broke(boolean force) {
        Bukkit.getServer().getPluginManager().callEvent(new MysteryBlockDestroyEvent(this));

        if (!force) {
            totalDestroys++;

            try {
                mineActionHandler.perform(BlockActionSection.DESTROY_GLOBAL,null);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c" + name + " | onDestroyGlobalActions is wrongly configured! Check your config!");
                e.printStackTrace();
            }

            try {
                mineActionHandler.perform(BlockActionSection.DESTROY_EVERY_PLACE,null);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c" + name + " | onDestroyEveryPlaceActions is wrongly configured! Check your config!");
                e.printStackTrace();
            }

            try {
                mineActionHandler.perform(BlockActionSection.DESTROY_PER_PLACE,null);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §c" + name + " | onDestroyPlaceActions is wrongly configured! Check your config!");
                e.printStackTrace();
            }

            if (historyHandler.isEnabled()) {
                historyHandler.save();
            }
        }

        if (scheduleHandler.isEnabled()) {
            scheduleHandler.convert();
        }

        if (cooldownHandler.isEnabled()) {
            if (hologramHandler.isEnabled() && hologramHandler.getHologram() != null) {
                hologramHandler.getHologram().update();
            }

            location.getBlock().setType(cooldownHandler.getCooldownMaterial());
            cooldownHandler.setCurrentTime(System.currentTimeMillis());
        } else {
            reset(false);
        }
    }

    public void redefine(Location location) {
        this.location.getBlock().setType(Material.AIR);
        this.location = location;

        if (cooldownHandler.isEnabled() && cooldownHandler.getCurrentTime() != 0) {
            this.location.getBlock().setType(cooldownHandler.getCooldownMaterial());
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

    public int getRequiredMines() {
        return requiredMines + requiredTempMines;
    }

    public String getPermission() {
        return "firemysteryblocks."+ name;
    }
}

package cz.devfire.mysteryblocks;

import cz.devfire.mysteryblocks.Block.BlockHandlerImpl;
import cz.devfire.mysteryblocks.Block.Hologram.Handlers.HologramHandlerImpl;
import cz.devfire.mysteryblocks.Block.Listener.BlockMineListener;
import cz.devfire.mysteryblocks.Commands.CommandsHandlerImpl;
import cz.devfire.mysteryblocks.Listeners.PlayerCommandListener;
import cz.devfire.mysteryblocks.Listeners.PlayerJoinListener;
import cz.devfire.mysteryblocks.Other.Database.DatabaseHandler;
import cz.devfire.mysteryblocks.Other.Files.ConfigImpl;
import cz.devfire.mysteryblocks.Other.Files.Data;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Metrics;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.Placeholders.PlaceholderHandlerImpl;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public final class MysteryBlocksPluginImpl extends JavaPlugin implements MysteryBlocksPlugin {
    private File configFile;
    private ConfigImpl config;
    private Data data;

    private boolean debugEnabled = false;
    private boolean pluginEnabled = false;

    private DatabaseHandler databaseHandler = null;
    private PlaceholderHandlerImpl placeholderHandler = null;
    private CommandsHandlerImpl commandsHandler = null;
    private HologramHandlerImpl hologramHandler = null;
    private BlockHandlerImpl blockHandler = null;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("  ______ _          __  __           _                  ____  _            _        ");
        Bukkit.getConsoleSender().sendMessage(" |  ____(_)        |  \\/  |         | |                |  _ \\| |          | |       ");
        Bukkit.getConsoleSender().sendMessage(" | |__   _ _ __ ___| \\  / |_   _ ___| |_ ___ _ __ _   _| |_) | | ___   ___| | _____ ");
        Bukkit.getConsoleSender().sendMessage(" |  __| | | '__/ _ \\ |\\/| | | | / __| __/ _ \\ '__| | | |  _ <| |/ _ \\ / __| |/ / __|");
        Bukkit.getConsoleSender().sendMessage(" | |    | | | |  __/ |  | | |_| \\__ \\ ||  __/ |  | |_| | |_) | | (_) | (__|   <\\__ \\");
        Bukkit.getConsoleSender().sendMessage(" |_|    |_|_|  \\___|_|  |_|\\__, |___/\\__\\___|_|   \\__, |____/|_|\\___/ \\___|_|\\_\\___/");
        Bukkit.getConsoleSender().sendMessage("                            __/ |                  __/ |                            ");
        Bukkit.getConsoleSender().sendMessage("                           |___/                  |___/                             ");
        Bukkit.getConsoleSender().sendMessage("§6Loading..§r");

        Language.reload(this);

        Metrics metrics = new Metrics(this, 16913);
        metrics.addCustomChart(new Metrics.SimplePie("hologramtype", () -> config.getString("Settings.Holograms.Provider", "NONE")));

        try {
            configFile = new File(this.getDataFolder(), "config.yml");
            if (!configFile.exists()) this.saveResource("config.yml", false);

            config = ConfigImpl.loadConfiguration(configFile);
            config.syncWithConfig(configFile, this.getResource("config.yml"));

            Bukkit.getConsoleSender().sendMessage("§e - Loading config.yml... §aSuccessful!");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§e - Loading config.yml... §cFailed!");
            Bukkit.getConsoleSender().sendMessage("§e - Shutting down plugin, plugin cant work without config!");
            Bukkit.getConsoleSender().sendMessage("§e - Error: ");
            e.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        try {
            data = Data.loadFile(this, "data.yml", "data.yml");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§e - Loading data.yml... §cFailed!");
            Bukkit.getConsoleSender().sendMessage("§e - Shutting down plugin, plugin cant work without data file!");
            Bukkit.getConsoleSender().sendMessage("§e - Error: ");
            e.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        debugEnabled = config.getBoolean("Settings.Debug");
        if (debugEnabled) {
            Bukkit.getConsoleSender().sendMessage("§e - Debug mode is §aenabled§e!");
        } else {
            Bukkit.getConsoleSender().sendMessage("§e - Debug mode is §cdisabled§e!");
        }

        databaseHandler = new DatabaseHandler(this);
        if (!databaseHandler.load()) {
            Bukkit.getConsoleSender().sendMessage("§e - Shutting down plugin, plugin cant work without database!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        commandsHandler = new CommandsHandlerImpl(this, "mysteryblocks");
        commandsHandler.load();

        hologramHandler = new HologramHandlerImpl(this);
        hologramHandler.load();

        blockHandler = new BlockHandlerImpl(this);
        blockHandler.load();
        blockHandler.startSchedulers();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderHandler = new PlaceholderHandlerImpl(this);
            placeholderHandler.init();
        }

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommandListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockMineListener(this), this);

        pluginEnabled = true;
        getLogger().info("Enabled plugin with version " + getDescription().getVersion() + " by " + Utils.stripBrackets(getDescription().getAuthors().toString()));
    }

    @Override
    public void onDisable() {
        blockHandler.save();
        blockHandler.removeOld();

        if (databaseHandler != null && databaseHandler.isConnected()) {
            databaseHandler.disconnect();
        }

        if (placeholderHandler != null) {
            placeholderHandler.destroy();
        }

        pluginEnabled = false;
        getLogger().info("Disabled plugin with version " + getDescription().getVersion() + " by " + Utils.stripBrackets(getDescription().getAuthors().toString()));
    }

    public long reload() {
        long time = System.currentTimeMillis();

        Bukkit.getConsoleSender().sendMessage("[FireMysteryBlocks] Re-Loading..");

        blockHandler.save();
        blockHandler.removeOld();
        blockHandler.getBlocks().clear();
        blockHandler.stopSchedulers();

        // -------------

        Language.reload(this);

        try {
            config = ConfigImpl.loadConfiguration(configFile);
            config.syncWithConfig(configFile, this.getResource("config.yml"));

            Bukkit.getConsoleSender().sendMessage("§e - Loading config.yml... §aSuccessful!");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§e - Loading config.yml... §cFailed!");
            Bukkit.getConsoleSender().sendMessage("§e - Shutting down plugin, plugin cant work without config!");
            Bukkit.getConsoleSender().sendMessage("§e - Error: ");
            e.printStackTrace();
            onDisable();
            return -1;
        }

        try {
            data = Data.loadFile(this, "data.yml", "data.yml");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§e - Loading data.yml... §cFailed!");
            Bukkit.getConsoleSender().sendMessage("§e - Shutting down plugin, plugin cant work without data file!");
            Bukkit.getConsoleSender().sendMessage("§e - Error: ");
            e.printStackTrace();
            onDisable();
            return -1;
        }

        debugEnabled = config.getBoolean("Settings.Debug");
        if (debugEnabled) {
            Bukkit.getConsoleSender().sendMessage("§e - Debug mode is §aenabled§e!");
        } else {
            Bukkit.getConsoleSender().sendMessage("§e - Debug mode is §cdisabled§e!");
        }

        hologramHandler.load();
        blockHandler.load();
        blockHandler.startSchedulers();

        Bukkit.getConsoleSender().sendMessage("[FireMysteryBlocks] Plugin reloaded in.. " + (System.currentTimeMillis() - time) + "ms");
        return System.currentTimeMillis() - time;
    }

    // ------

    public void debug(String string) {
        if (debugEnabled) Bukkit.getLogger().log(Level.INFO, "DEBUG: " + string);
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public boolean isPluginEnabled() {
        return pluginEnabled;
    }

    // ------

    public Data getData() {
        return data;
    }

    @Override
    public ConfigImpl getConfig() {
        return config;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public CommandsHandlerImpl getCommandsHandler() {
        return commandsHandler;
    }

    public HologramHandlerImpl getHologramHandler() {
        return hologramHandler;
    }

    public BlockHandlerImpl getBlockHandler() {
        return blockHandler;
    }

    public PlaceholderHandlerImpl getPlaceholderHandler() {
        return placeholderHandler;
    }
}

package cz.devfire.mysteryblocks;

import cz.devfire.mysteryblocks.Block.BlockHandler;
import cz.devfire.mysteryblocks.Command.CommandHandler;
import cz.devfire.mysteryblocks.Database.DatabaseHandler;
import cz.devfire.mysteryblocks.Files.Config;
import cz.devfire.mysteryblocks.Files.Data;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.Hologram.HologramHandler;
import cz.devfire.mysteryblocks.Listener.PlayerCommandListener;
import cz.devfire.mysteryblocks.Listener.PlayerJoinListener;
import cz.devfire.mysteryblocks.Placeholders.PlaceholderHandler;
import cz.devfire.mysteryblocks.Util.Metrics;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class MysteryBlocksPlugin extends JavaPlugin {
    private File configFile;
    private Config config;
    private Data cache;
    private Data history;

    private static boolean debugEnabled = false;
    private static boolean pluginEnabled = false;

    private DatabaseHandler databaseHandler = null;
    private BlockHandler blockHandler = null;
    private HologramHandler hologramHandler = null;
    private PlaceholderHandler placeholderHandler = null;
    private CommandHandler commandHandler = null;

    @Override
    public void onEnable() {
        Utils.log("  ______ _          __  __           _                  ____  _            _        ");
        Utils.log(" |  ____(_)        |  \\/  |         | |                |  _ \\| |          | |       ");
        Utils.log(" | |__   _ _ __ ___| \\  / |_   _ ___| |_ ___ _ __ _   _| |_) | | ___   ___| | _____ ");
        Utils.log(" |  __| | | '__/ _ \\ |\\/| | | | / __| __/ _ \\ '__| | | |  _ <| |/ _ \\ / __| |/ / __|");
        Utils.log(" | |    | | | |  __/ |  | | |_| \\__ \\ ||  __/ |  | |_| | |_) | | (_) | (__|   <\\__ \\");
        Utils.log(" |_|    |_|_|  \\___|_|  |_|\\__, |___/\\__\\___|_|   \\__, |____/|_|\\___/ \\___|_|\\_\\___/");
        Utils.log("                            __/ |                  __/ |                            ");
        Utils.log("                           |___/                  |___/                             ");
        Utils.log("&6Loading..§r");

        Language.reload(this);

        Metrics metrics = new Metrics(this, 16913);
        metrics.addCustomChart(new Metrics.SimplePie("hologramtype", () -> config.getString("Settings.Holograms.Provider", "NONE")));

        try {
            configFile = new File(this.getDataFolder(),"config.yml");
            if (!configFile.exists()) this.saveResource("config.yml",false);

            config = Config.loadConfiguration(configFile);
            config.syncWithConfig(configFile, this.getResource("config.yml"));

            Utils.log("&e - Loading config.yml... &aSuccessful!");
        } catch (Exception e) {
            Utils.log("&e - Loading config.yml... &cFailed!");
            Utils.log("&e - Shutting down plugin, plugin cant work without config!");
            Utils.log("&e - Error: ");
            e.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        try {
            cache = Data.loadFile(this,"cache.yml","cache.yml");
        } catch (Exception e) {
            Utils.log("&e - Loading cache.yml... &cFailed!");
            Utils.log("&e - Shutting down plugin, plugin cant work without data file!");
            Utils.log("&e - Error: ");
            e.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        try {
            history = Data.loadFile(this,"history.yml","history.yml");
        } catch (Exception e) {
            Utils.log("&e - Loading history.yml... &cFailed!");
            Utils.log("&e - Shutting down plugin, plugin cant work without data file!");
            Utils.log("&e - Error: ");
            e.printStackTrace();
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        databaseHandler = new DatabaseHandler(this);
        if (!databaseHandler.init(config.getConfigurationSection("Settings.Database"))) {
            Utils.log("&e - Shutting down plugin, plugin cant work without database!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        placeholderHandler = new PlaceholderHandler(this);
        placeholderHandler.init(config.getConfigurationSection("Settings.Placeholders"));

        hologramHandler = new HologramHandler(this);
        hologramHandler.init(config.getConfigurationSection("Settings.Holograms"));

        blockHandler = new BlockHandler(this);
        blockHandler.init();

        commandHandler = new CommandHandler(this,"mysteryblocks");
        commandHandler.init();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this),this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommandListener(this),this);

        debugEnabled = config.getBoolean("Settings.Debug");
        if (debugEnabled) {
            Utils.log("&e - Debug mode is &aenabled§e!");
        } else {
            Utils.log("&e - Debug mode is &cdisabled§e!");
        }

        pluginEnabled = true;
        getLogger().info("Enabled plugin with version " + getDescription().getVersion() + " by " + Utils.stripBrackets(getDescription().getAuthors().toString()));
    }

    @Override
    public void onDisable() {
        if (blockHandler != null && blockHandler.isEnabled()) {
            blockHandler.destroy();
        }

        if (databaseHandler != null && databaseHandler.isEnabled()) {
            databaseHandler.destroy();
        }

        if (placeholderHandler != null && placeholderHandler.isEnabled()) {
            placeholderHandler.destroy();
        }

        if (commandHandler != null && commandHandler.isEnabled()) {
            commandHandler.destroy();
        }

        if (hologramHandler != null && hologramHandler.isEnabled()) {
            hologramHandler.destroy();
        }

        pluginEnabled = false;
        getLogger().info("Disabled plugin with version " + getDescription().getVersion() + " by " + Utils.stripBrackets(getDescription().getAuthors().toString()));
    }

    public long reload() {
        long time = System.currentTimeMillis();

        Bukkit.getConsoleSender().sendMessage("[FireMysteryBlocks] Re-Loading..");

        hologramHandler.destroy();
        blockHandler.destroy();

        // -------------

        Language.reload(this);

        try {
            config = Config.loadConfiguration(configFile);
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
            cache = Data.loadFile(this, "cache.yml", "cache.yml");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§e - Loading cache.yml... §cFailed!");
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

        hologramHandler.init(config.getConfigurationSection("Settings.Holograms"));
        blockHandler.init();

        Bukkit.getConsoleSender().sendMessage("[FireMysteryBlocks] Plugin reloaded in.. " + (System.currentTimeMillis() - time) + "ms");
        return System.currentTimeMillis() - time;
    }

    // ----

    public static boolean isPluginEnabled() {
        return pluginEnabled;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    // ----

    @Override
    public Config getConfig() {
        return config;
    }
}

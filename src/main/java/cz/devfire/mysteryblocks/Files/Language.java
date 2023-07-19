package cz.devfire.mysteryblocks.Files;

import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Language {
    private static final Map<String, Language> localeMap = new HashMap<>();

    public static Language EMPTY = new Language("EMPTY");
    public static Language USAGE = new Language("USAGE");
    public static Language HELP = new Language("HELP");
    public static Language HELP_COMMAND = new Language("HELP_COMMAND");
    public static Language NO_COMMAND = new Language("NO_COMMAND");
    public static Language NO_PLAYER = new Language("NO_PLAYER");
    public static Language NO_PERMISSION = new Language("NO_PERMISSION");
    public static Language PLAYER_ONLY_COMMAND = new Language("PLAYER_ONLY_COMMAND");
    public static Language RELOAD = new Language("RELOAD");
    public static Language TELEPORTED = new Language("TELEPORTED");
    public static Language NUMBER_OVER = new Language("NUMBER_OVER");
    public static Language NUMBER_NEGATIVE = new Language("NUMBER_NEGATIVE");
    public static Language BLOCK_LIST = new Language("BLOCK_LIST");
    public static Language BLOCK_LIST_ITEM = new Language("BLOCK_LIST_ITEM");
    public static Language BLOCK_DAMAGE_ADDED = new Language("BLOCK_DAMAGE_ADDED");
    public static Language BLOCK_DAMAGE_ADDED_AS = new Language("BLOCK_DAMAGE_ADDED_AS");
    public static Language BLOCK_ENCHANT_LIMIT = new Language("BLOCK_ENCHANT_LIMIT");
    public static Language BLOCK_PERMISSION = new Language("BLOCK_PERMISSION");
    public static Language BLOCK_COOLDOWN = new Language("BLOCK_COOLDOWN");
    public static Language BLOCK_CREATED = new Language("BLOCK_CREATED");
    public static Language BLOCK_DELETED = new Language("BLOCK_DELETED");
    public static Language BLOCK_SET = new Language("BLOCK_SET");
    public static Language BLOCK_UNKNOWN = new Language("BLOCK_UNKNOWN");
    public static Language BLOCK_EXISTS = new Language("BLOCK_EXISTS");
    public static Language BLOCK_NOT_FOUND = new Language("BLOCK_NOT_FOUND");
    public static Language BLOCK_ALREADY_SET = new Language("BLOCK_ALREADY_SET");
    public static Language BLOCK_RESET = new Language("BLOCK_RESET");
    public static Language BLOCK_RESET_ALL = new Language("BLOCK_RESET_ALL");
    public static Language BLOCK_FINISH = new Language("BLOCK_FINISH");
    public static Language BLOCK_GUI_DISABLED = new Language("BLOCK_GUI_DISABLED");
    public static Language BLOCK_HISTORY = new Language("BLOCK_HISTORY");
    public static Language BLOCK_HISTORY_LINE = new Language("BLOCK_HISTORY_LINE");
    public static Language BLOCK_HISTORY_DISABLED = new Language("BLOCK_HISTORY_DISABLED");
    public static Language BLOCK_HISTORY_EMPTY = new Language("BLOCK_HISTORY_EMPTY");
    public static Language BLOCK_HISTORY_OVER = new Language("BLOCK_HISTORY_OVER");
    public static Language BLOCK_VISIBILITY_START = new Language("BLOCK_VISIBILITY_START");
    public static Language BLOCK_VISIBILITY_STOP = new Language("BLOCK_VISIBILITY_STOP");
    public static Language BLOCK_VISIBILITY_TOGGLE = new Language("BLOCK_VISIBILITY_TOGGLE");
    public static Language BLOCK_VISIBILITY_TOGGLE_ENABLED = new Language("BLOCK_VISIBILITY_TOGGLE_ENABLED");
    public static Language BLOCK_VISIBILITY_TOGGLE_DISABLED = new Language("BLOCK_VISIBILITY_TOGGLE_DISABLED");
    public static Language BLOCK_MESSAGE_TOGGLE = new Language("BLOCK_MESSAGE_TOGGLE");
    public static Language BLOCK_MESSAGE_TOGGLE_ENABLED = new Language("BLOCK_MESSAGE_TOGGLE_ENABLED");
    public static Language BLOCK_MESSAGE_TOGGLE_DISABLED = new Language("BLOCK_MESSAGE_TOGGLE_DISABLED");
    private String message;

    private Language(String identifier) {
        localeMap.put(identifier, this);
    }

    public static void reload(MysteryBlocksPlugin plugin) {
        Config config = null;

        try {
            File langFile = new File(plugin.getDataFolder(), "lang.yml");
            if (!langFile.exists()) plugin.saveResource("lang.yml", false);

            config = Config.loadConfiguration(langFile);
            config.syncWithConfig(langFile, plugin.getResource("lang.yml"));

            Bukkit.getConsoleSender().sendMessage("§e - Loading language... §aSuccessful!");
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("§e - Loading language... §cFailed!");

            e.printStackTrace();
        }

        for (String identifier : localeMap.keySet()) {
            localeMap.get(identifier).setMessage(Utils.cc(config.getString(identifier, "")));
        }
    }

    public boolean isEmpty() {
        return message == null || message.isEmpty();
    }

    public String getMessage(Object... objects) {
        if (message != null && !message.isEmpty()) {
            String msg = message;

            for (int i = 0; i < objects.length; i++)
                msg = msg.replace("{" + i + "}", objects[i].toString());

            return msg;
        }

        if (message.isEmpty()) {
            return "";
        }

        return "FireMysteryBlocks Message";
    }

    public void send(CommandSender sender, Object... objects) {
        String message = getMessage(objects);

        if (message != null && sender != null)
            sender.sendMessage(message);
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}

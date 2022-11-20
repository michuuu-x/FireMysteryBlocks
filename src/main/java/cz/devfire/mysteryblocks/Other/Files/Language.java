package cz.devfire.mysteryblocks.Other.Files;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Language {

    private static final Map<String, Language> localeMap = new HashMap<>();
    public static Language USAGE = new Language("USAGE");
    public static Language HELP = new Language("HELP");
    public static Language HELP_COMMAND = new Language("HELP_COMMAND");
    public static Language NO_COMMAND = new Language("NO_COMMAND");
    public static Language LIST = new Language("LIST");
    public static Language LIST_BLOCK = new Language("LIST_BLOCK");
    public static Language NO_PERMISSIONS = new Language("NO_PERMISSIONS");
    public static Language RELOAD = new Language("RELOAD");
    public static Language EMPTY = new Language("EMPTY");
    public static Language TELEPORTED = new Language("TELEPORTED");
    public static Language PLAYER_ONLY_COMMAND = new Language("PLAYER_ONLY_COMMAND");
    public static Language BLOCK_PERMISSION = new Language("BLOCK_PERMISSION");
    public static Language BLOCK_COOLDOWN = new Language("BLOCK_COOLDOWN");
    public static Language BLOCK_CREATED = new Language("BLOCK_CREATED");
    public static Language BLOCK_DELETED = new Language("BLOCK_DELETED");
    public static Language BLOCK_SET = new Language("BLOCK_SET");
    public static Language BLOCK_UNKNOWN = new Language("BLOCK_UNKNOWN");
    public static Language BLOCK_EXISTS = new Language("BLOCK_EXISTS");
    public static Language BLOCK_NOT_FOUND = new Language("BLOCK_NOT_FOUND");
    public static Language BLOCK_ALREADY_SET = new Language("BLOCK_ALREADY_SET");
    public static Language BLOCK_RESETED = new Language("BLOCK_RESETED");
    public static Language BLOCKS_RESETED = new Language("BLOCKS_RESETED");
    public static Language CAPTCHA_TITLE = new Language("CAPTCHA_TITLE");
    public static Language CAPTCHA_ITEM_TITLE = new Language("CAPTCHA_ITEM_TITLE");
    public static Language CAPTCHA_ITEM_DESCRIPTION = new Language("CAPTCHA_ITEM_DESCRIPTION");
    public static Language ENCHANT_LIMIT = new Language("ENCHANT_LIMIT");
    private String message;

    private Language(String identifier) {
        localeMap.put(identifier, this);
    }

    public static void reload(MysteryBlocksPluginImpl plugin) {
        ConfigImpl config = null;

        try {
            File langFile = new File(plugin.getDataFolder(), "lang.yml");
            if (!langFile.exists()) plugin.saveResource("lang.yml", false);

            config = ConfigImpl.loadConfiguration(langFile);
            config.syncWithConfig(langFile, plugin.getResource("lang.yml"));

            Bukkit.getConsoleSender().sendMessage("§e - Loading language... §aSuccessful!");
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("§e - Loading language... §cFailed!");

            e.printStackTrace();
        }

        for (String identifier : localeMap.keySet()) {
            localeMap.get(identifier).setMessage(ChatColor.translateAlternateColorCodes('&', config.getString(identifier, "")));
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
}

package cz.devfire.mysteryblocks.Other;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.iridium.iridiumcolorapi.IridiumColorAPI;
import cz.devfire.mysteryblocks.Block.Objects.MysteryBlockImpl;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static String stripBrackets(String string) {
        return string.replace("[", "").replace("]", "");
    }

    public static Location getLocationFromString(String string) {
        String[] stringArgs = string.split("[/:,|]");

        World world = Bukkit.getWorld(stringArgs[0]);
        double x = Double.parseDouble(stringArgs[1]);
        double y = Double.parseDouble(stringArgs[2]);
        double z = Double.parseDouble(stringArgs[3]);
        float yaw = 0F;
        float pitch = 0F;

        if (stringArgs.length > 4) {
            yaw = Float.parseFloat(stringArgs[4]);
            pitch = Float.parseFloat(stringArgs[5]);
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String parseArgs(String string, String... args) {
        int i = 0;

        for (String arg : args) {
            string = string.replace("{" + i + "}", arg);
            i++;
        }

        return string;
    }

    public static String parseArgs(String string, Collection<String> args) {
        int i = 0;

        for (String arg : args) {
            string = string.replace("{" + i + "}", arg);
            i++;
        }

        return string;
    }

    public static List<String> parseArgs(List<String> stringList, String... args) {
        List<String> a = Lists.newArrayList();

        for (String string : stringList) {
            int i = 0;

            for (String arg : args) {
                string = string.replace("{" + i + "}", arg);
                i++;
            }

            a.add(string);
        }

        return a;
    }

    public static String cc(String string) {
        if (getServerVersionID() <= 12) {
            return ChatColor.translateAlternateColorCodes('&', string);
        }

        return IridiumColorAPI.process(string);
    }

    public static List<String> ccl(List<String> stringList) {
        List<String> list = Lists.newArrayList();

        for (String s : stringList) {
            list.add(cc(s));
        }

        return list;
    }

    public static String getServerVersion() {
        String version = null;

        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (Exception e) {
            try {
                version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[1];
            } catch (Exception e2) { /* */ }
        }

        return version;
    }

    public static int getServerVersionID() {
        return Integer.parseInt(getServerVersion().split("_")[1]);
    }

    public static LinkedHashMap<String, Integer> sortMapByValue(Map<String, Integer> unsortedMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));

        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    public static void doPlaceActions(MysteryBlocksPluginImpl plugin, MysteryBlock block, List<String> actions, List<String> playerList) {
        for (String placeAction : actions) {
            String[] placeActionArgs = placeAction.split(" ", 3);
            String place = placeActionArgs[0];
            String actionType = placeActionArgs[1];
            String action = placeActionArgs[2];

            if (place.contains("~")) {
                String[] placeArgs = placeActionArgs[0].split("~");
                int start = Integer.parseInt(placeArgs[0]);
                int end = Integer.parseInt(placeArgs[1]);

                for (int pos = start; pos < end + 1; pos++) {
                    if (pos * 2 > playerList.size()) continue;

                    String playerName = playerList.get(pos * 2 - 2);
                    String value = playerList.get(pos * 2 - 1);

                    if (!playerName.equalsIgnoreCase(Language.EMPTY.getMessage())) {
                        Utils.doAction(plugin, block, actionType, action, playerName, value);
                    }
                }
            } else {
                int p = Integer.parseInt(place);

                String playerName = playerList.get(p * 2 - 2);
                String value = playerList.get(p * 2 - 1);

                if (!playerName.equalsIgnoreCase(Language.EMPTY.getMessage())) {
                    Utils.doAction(plugin, block, actionType, action, playerName, value);
                }
            }
        }
    }

    public static void doActions(MysteryBlocksPluginImpl plugin, MysteryBlock block, List<String> actions) {
        doActions(plugin, block, actions, "null");
    }

    public static void doActions(MysteryBlocksPluginImpl plugin, MysteryBlock block, List<String> actions, String playerName, String... add) {
        for (String action : actions) {
            doAction(plugin, block, action.split(" ", 2), playerName, add);
        }
    }

    public static void doAction(MysteryBlocksPluginImpl plugin, MysteryBlock block, String[] actionArgs, String playerName, String... add) {
        doAction(plugin, block, actionArgs[0], actionArgs[1], playerName, add);
    }

    public static String parseBlockPlaceholders(MysteryBlock mysteryBlock, Player player, String line) {
        boolean blockNull = mysteryBlock == null;
        boolean playerNull = player == null;

        long time = mysteryBlock.getCooldownHandler().isUnder() ? mysteryBlock.getCooldownHandler().getTime() : 0;
        boolean under = time > 0;

        return Utils.parseArgs(line,
                /* 0  - Block Name             */ blockNull  ? "null" : mysteryBlock.getName(),
                /* 1  - Block Material         */ blockNull  ? "null" : mysteryBlock.getMaterial().name(),
                /* 2  - Player Name            */ playerNull ? "null" : player.getName(),
                /* 3  - Block Mines ASC        */ blockNull  ? "null" : mysteryBlock.getCurrentMines() + "",
                /* 4  - Block Mines DESC       */ blockNull  ? "null" : (mysteryBlock.getRequiredMines() - mysteryBlock.getCurrentMines()) + "",
                /* 5  - Block Required Mines   */ blockNull  ? "null" : mysteryBlock.getRequiredMines() + "",
                /* 6  - Player Current Mines   */ blockNull  ? "null" : playerNull ?  "0" : mysteryBlock.getMineMap().getOrDefault(player.getName(),0) +"",
                /* 7  - Player Mine Speed      */ blockNull  ? "null" : playerNull ? "-1" : mysteryBlock.getAntiCheatHandler().getMineMap().getOrDefault(player, Sets.newHashSet()).size() +"",
                /* 8  - Block Cooldown FORMAT  */ blockNull  ? "null" : under ? Utils.translateTime(time) + "" : "00:00:00",
                /* 9  - Block Cooldown SHORT   */ blockNull  ? "null" : under ? Utils.setTimeSecondsToString(time / 1000) + "" : "0s",
                /* 10 - Block Cooldown PLAIN   */ blockNull  ? "null" : under ? ((int) (time / 1000)) + "" : "0"
                );
    }

    public static void doAction(MysteryBlocksPluginImpl plugin, MysteryBlock block, String actionType, String action, String playerName, String... add) {
        Player player = Bukkit.getPlayer(playerName);
        boolean cache = false;

        if (action.startsWith("$")) {
            action = action.substring(1);

            if (!playerName.equalsIgnoreCase("null") && player == null) {
                cache = true;
            }
        }

        if (playerName.startsWith("$")) {
            int i = 1;

            playerName = playerName.substring(2);

            for (String playerData : playerName.split("=")) {
                String[] playerDataArgs = playerData.split("\\|");

                action = action.replace("{pos-" + i + "-name}", playerDataArgs[0]);
                action = action.replace("{pos-" + i++ + "-value}", playerDataArgs[1]);
            }
        }

        action = Utils.parseBlockPlaceholders(block, player, action);

        switch (actionType) {
            case "[COMMAND]": {
                if (cache) {
                    cacheAction(plugin, playerName, actionType, action);
                } else {
                    if (player != null) {
                        if (action.startsWith("$")) {
                            action = action.substring(1);
                        }

                        if (action.startsWith("@")) {
                            String[] args = action.split("@", 3);

                            if (new Random().nextInt(100) + 1 < Integer.parseInt(args[1])) {
                                action = args[2];
                            } else {
                                break;
                            }
                        }

                        player.performCommand(action);
                    }
                }

                break;
            }

            case "[OP-COMMAND]": {
                if (player == null && action.startsWith("$")) {
                    cacheAction(plugin, playerName, actionType, action);
                } else if (player != null) {
                    if (action.startsWith("$")) {
                        action = action.substring(1);
                    }

                    if (action.startsWith("@")) {
                        String[] args = action.split("@", 3);

                        if (new Random().nextInt(100) + 1 < Integer.parseInt(args[1])) {
                            action = args[2];
                        } else {
                            break;
                        }
                    }

                    player.setOp(true);
                    player.performCommand(action);
                    player.setOp(false);
                }

                break;
            }

            case "[CONSOLE-COMMAND]": {
                if (cache) {
                    cacheAction(plugin, playerName, actionType, action);
                } else {
                    if (action.startsWith("$")) {
                        action = action.substring(1);
                    }

                    if (action.startsWith("@")) {
                        String[] args = action.split("@", 3);

                        if (new Random().nextInt(100) + 1 < Integer.parseInt(args[1])) {
                            action = args[2];
                        } else {
                            break;
                        }
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action);
                }

                break;
            }

            case "[MESSAGE]": {
                if (cache) {
                    cacheAction(plugin, playerName, actionType, action);
                } else if (player != null) {
                    if (action.startsWith("$")) {
                        action = action.substring(1);
                    }

                    if (action.startsWith("@")) {
                        String[] args = action.split("@", 3);

                        if (new Random().nextInt(100) + 1 < Integer.parseInt(args[1])) {
                            action = args[2];
                        } else {
                            break;
                        }
                    }

                    player.sendMessage(Utils.cc(action));
                }

                break;
            }

            case "[WARN]": {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("firemysteryblocks.warn")) {
                        p.sendMessage(Utils.cc(action));
                    }
                }

                break;
            }

            case "[BROADCAST]": {
                if (cache) {
                    cacheAction(plugin, playerName, actionType, action);
                } else {
                    if (action.startsWith("$")) {
                        action = action.substring(1);
                    }

                    if (action.startsWith("@")) {
                        String[] args = action.split("@", 3);

                        if (new Random().nextInt(100) + 1 < Integer.parseInt(args[1])) {
                            action = args[2];
                        } else {
                            break;
                        }
                    }

                    Bukkit.broadcastMessage(Utils.cc(action));
                }

                break;
            }

            case "[SOUND]": {
                if (cache) {
                    cacheAction(plugin, playerName, actionType, action);
                } else if (player != null) {
                    if (action.startsWith("$")) {
                        action = action.substring(1);
                    }

                    if (action.startsWith("@")) {
                        String[] args = action.split("@", 3);

                        if (new Random().nextInt(100) + 1 < Integer.parseInt(args[1])) {
                            action = args[2];
                        } else {
                            break;
                        }
                    }

                    String[] soundArgs = action.split("-");

                    Sound sound = null;
                    float volume = 1;
                    float pitch = 1;

                    Boolean er = false;
                    Exception exception = null;

                    if (soundArgs.length >= 1) {
                        try {
                            sound = Sound.valueOf(soundArgs[0]);
                        } catch (Exception e) {
                            er = true;
                            exception = e;
                        }
                    }

                    if (soundArgs.length >= 2) {
                        volume = Float.parseFloat(soundArgs[1]);
                    }

                    if (soundArgs.length >= 3) {
                        pitch = Float.parseFloat(soundArgs[2]);
                    }

                    if (er || sound == null) {
                        plugin.debug("Unknown sound: \"" + action + "\"");

                        if (plugin.isDebugEnabled()) {
                            exception.printStackTrace();
                        }
                    } else {
                        if (playerName.equalsIgnoreCase("null")) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.playSound(player.getLocation(), sound, volume, pitch);
                            }
                        } else {
                            player.playSound(player.getLocation(), sound, volume, pitch);
                        }
                    }
                }

                break;
            }

            case "[ACTIONBAR]": {
                if (cache) {
                    cacheAction(plugin, playerName, actionType, action);
                } else if (player != null) {
                    if (action.startsWith("$")) {
                        action = action.substring(1);
                    }

                    if (action.startsWith("@")) {
                        String[] args = action.split("@", 3);

                        if (new Random().nextInt(100) + 1 < Integer.parseInt(args[1])) {
                            action = args[2];
                        } else {
                            break;
                        }
                    }

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Utils.cc(action)));
                }

                break;
            }
        }
    }

    public static void cacheAction(MysteryBlocksPluginImpl plugin, String playerName, String action, String value) {
        if (playerName.equalsIgnoreCase(Language.EMPTY.getMessage())) {
            return;
        }

        ArrayList<String> values = Lists.newArrayList();

        if (plugin.getData().exist(playerName.toLowerCase())) {
            values.addAll(plugin.getData().getStringList(playerName.toLowerCase()));
        }

        values.add(action + " | " + value);

        plugin.getData().set(playerName.toLowerCase(), values);
        plugin.getData().save();
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "/" + location.getX() + "/" + location.getY() + "/" + location.getZ();
    }

    public static ArrayList<String> copyMatches(String word, List<String> withMatch) {
        ArrayList<String> a = new ArrayList<String>();

        for (String string : withMatch) {
            if (string.length() < word.length()) {
                continue;
            }

            if (string.regionMatches(true, 0, word, 0, word.length())) {
                a.add(string);
            }
        }

        return a;
    }

    public static String translateTime(Long millis) {
        String s = "";
        Long amount = 0L;

        amount /= 3600000L;
        millis %= 3600000L;
        s = s + ((amount < 10L) ? "0" : "") + amount + ":";

        amount /= 60000L;
        millis %= 60000L;
        s = s + ((amount < 10L) ? "0" : "") + amount + ":";

        amount = millis / 1000L;
        s = s + ((amount < 10L) ? "0" : "") + amount;

        return s;
    }

    public static String setTimeSecondsToString(Long seconds) {
        int days = ((int) Math.floor(seconds / 86400));
        seconds = seconds % 86400;

        int hours = ((int) Math.floor(seconds / 3600));
        seconds = seconds % 3600;

        int minutes = ((int) Math.floor(seconds / 60));
        seconds = seconds % 60;

        String d = "";
        String h = "";
        String m = "";
        String s = "";

        if (seconds == 0) {
            s = "";
        } else {
            s = seconds + "s";
        }

        if (minutes == 0) {
            m = "";
        } else {
            m = minutes + "m ";
        }

        if (hours == 0) {
            h = "";
        } else {
            h = hours + "h ";
        }

        if (days == 0) {
            d = "";
        } else {
            d = days + "d ";
        }

        return d + h + m + s;
    }

    public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90.0f) % 360.0f;

        rotation += 180.0;

        if (rotation < 0.0) {
            rotation += 360.0;
        }

        if (0.0 <= rotation && rotation < 22.5) {
            return "W";
        }
        if (22.5 <= rotation && rotation < 67.5) {
            return "NW";
        }
        if (67.5 <= rotation && rotation < 112.5) {
            return "N";
        }
        if (112.5 <= rotation && rotation < 157.5) {
            return "NE";
        }
        if (157.5 <= rotation && rotation < 202.5) {
            return "E";
        }
        if (202.5 <= rotation && rotation < 247.5) {
            return "SE";
        }
        if (247.5 <= rotation && rotation < 292.5) {
            return "S";
        }
        if (292.5 <= rotation && rotation < 337.5) {
            return "SW";
        }
        if (337.5 <= rotation && rotation < 360.0) {
            return "W";
        }
        return "none";
    }

    public static ItemStack getItemFromSection(ConfigurationSection section) {
        ItemStack stack = new ItemStack(Material.valueOf(section.getString("Material", "BEDROCK")));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Utils.cc(section.getString("DisplayName", "&cError")));
        meta.setLore(Utils.ccl(section.getStringList("Lore")));
        stack.setItemMeta(meta);

        return stack;
    }
}

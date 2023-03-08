package cz.devfire.mysteryblocks.Util;

import com.google.common.collect.Lists;
import com.iridium.iridiumcolorapi.IridiumColorAPI;
import cz.devfire.mysteryblocks.Block.Handler.History.Object.History;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.Placeholders.PlaceholderHandler;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    private Utils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String stripBrackets(String string) {
        return string.replace("[", "").replace("]", "");
    }

    public static String stripColors(String string) {
        return ChatColor.stripColor(string);
    }

    public static void log(String string) {
        Bukkit.getConsoleSender().sendMessage(Utils.cc(string));
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

    public static String putLocationToString(Location location) {
        return putLocationToString(location,false);
    }

    public static String putLocationToString(Location location, boolean full) {
        return location.getWorld().getName() +"/"+ location.getX() +"/"+ location.getY() +"/"+ location.getZ() + (full ? ("/"+ location.getYaw() +"/"+ location.getPitch()) : "");
    }

    public static String parseArgs(String string, String... args) {
        return parseArgs(string, Arrays.asList(args));
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
        return parseArgs(stringList, Arrays.asList(args));
    }

    public static List<String> parseArgs(List<String> stringList, Collection<String> args) {
        return stringList.stream().map(string -> parseArgs(string, args)).collect(Collectors.toList());
    }

    public static List<String> replaceAll(List<String> list, String from, String to) {
        return list.stream().map(line -> line.replace(from, to)).collect(Collectors.toList());
    }

    public static String cc(String string) {
        if (getServerVersionID() <= 12) {
            return ChatColor.translateAlternateColorCodes('&', string);
        }

        return string == null || string.isEmpty() ? string : IridiumColorAPI.process(string);
    }

    public static List<String> ccl(List<String> stringList) {
        return stringList.stream().map(Utils::cc).collect(Collectors.toList());
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

    public static List<String> copyMatches(String word, List<String> withMatch) {
        ArrayList<String> match = Lists.newArrayList();

        for (String string : withMatch) {
            if (string.length() < word.length()) continue;

            if (string.regionMatches(true,0, word,0, word.length())) {
                match.add(string);
            }
        }

        return match;
    }

    public static String translateTimeToTimer(long millis) {
        String s = "";

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) (millis % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((millis % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        s = s + ((hours < 10L) ? "0" : "") + hours + ":";
        s = s + ((minutes < 10L) ? "0" : "") + minutes + ":";
        s = s + ((seconds < 10L) ? "0" : "") + seconds;

        return s;
    }

    public static String translateTimeToString(long millis) {
        String s = "";

        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) (millis / 1000 / 60) % 60;
        int hours = (int) (millis / 1000 / 60 / 60) % 24;
        int days = (int) (millis / 1000 / 60 / 60 / 24);

        s = s + (days == 0 ? "" : days +"d ");
        s = s + (hours == 0 ? "" : hours +"h ");
        s = s + (minutes == 0 ? "" : minutes +"m ");;
        s = s + (seconds == 0 ? "" : seconds +"s ");

        if (s.isEmpty()) {
            s = "0s";
        }

        return s.trim();
    }

    public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90.0f) % 360.0f;

        rotation += 180.0;
        if (rotation < 0.0) {
            rotation += 360.0;
        }

        if (0.0 <= rotation && rotation < 22.5) return "W";
        if (22.5 <= rotation && rotation < 67.5) return "NW";
        if (67.5 <= rotation && rotation < 112.5) return "N";
        if (112.5 <= rotation && rotation < 157.5) return "NE";
        if (157.5 <= rotation && rotation < 202.5) return "E";
        if (202.5 <= rotation && rotation < 247.5) return "SE";
        if (247.5 <= rotation && rotation < 292.5) return "S";
        if (292.5 <= rotation && rotation < 337.5) return "SW";
        if (337.5 <= rotation && rotation < 360.0) return "W";

        return "-1";
    }

    public static ItemStack getItemFromSection(ConfigurationSection section) {
        ItemStack stack = new ItemStack(Material.AIR);
        stack.setType(Material.valueOf(section.getString("Material", "BEDROCK")));
        stack.setAmount(section.getInt("Amount",1));

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Utils.cc(section.getString("DisplayName", "&cError")));
        meta.setLore(Utils.ccl(section.getStringList("Lore")));
        meta.setCustomModelData(section.getInt("ModelData"));
        stack.setItemMeta(meta);

        return stack;
    }

    public static List<String> parseBlockPlaceholders(MysteryBlock mysteryBlock, String playerName, List<String> lines) {
        ArrayList<String> newLines = Lists.newArrayList();

        for (String line : lines) {
            if (line.trim().length() == 0) {
                newLines.add(line);
            } else {
                newLines.add(Utils.parseBlockPlaceholders(mysteryBlock,null, line));
            }
        }

        return newLines;
    }

    public static String parseBlockPlaceholders(MysteryBlock mysteryBlock, String playerName, String line) {
        boolean blockIsNull = mysteryBlock == null;
        boolean playerIsNull = playerName == null;

        DecimalFormat decimal = new DecimalFormat("#,###");
        long time = blockIsNull ? 0 : mysteryBlock.getCooldownHandler().getETA();
        long remaining = blockIsNull || !mysteryBlock.getScheduleHandler().isAutoDestroyEnabled() ? 0 : Math.abs(System.currentTimeMillis() - mysteryBlock.getLastReset() - mysteryBlock.getScheduleHandler().getDestroyTime());

        if (mysteryBlock != null) {
            for (int i = 1; i < 10; i++) {
                String[] mapListArgs = mysteryBlock.getMineList().size() < i ? new String[]{} : mysteryBlock.getMineList().get(i - 1).split("\\|");
                line = line.replace("{pos-" + i + "-name}", "" + (mysteryBlock.getMineList().size() < i ? Language.EMPTY.getMessage() : mapListArgs[0]));
                line = line.replace("{pos-" + i + "-value}", "" + (mysteryBlock.getMineList().size() < i ? 0 : mapListArgs[1]));
            }

            if (mysteryBlock.getHistoryHandler().isEnabled()) {
                for (int i = 0; i < 10; i++) {
                    History history = mysteryBlock.getHistoryHandler().getHistory(i);

                    for (int j = 0; j < mysteryBlock.getHistoryHandler().getSaveCount(); j++) {
                        line = line.replace("{history-" + (i+1) + "-pos-" + (j+1) + "-name}", history.getPosition(j).getFirst());
                        line = line.replace("{history-" + (i+1) + "-pos-" + (j+1) + "-value}",history.getPosition(j).getSecond() +"");
                    }

                    line = line.replace("{history-" + (i+1) + "-date}", history.getDateString(mysteryBlock.getHistoryHandler().getDateFormat()));
                }
            }
        }

        return Utils.parseArgs(line,
                /* 0  - Block Name                 */ blockIsNull  ? "null" : mysteryBlock.getName(),
                /* 1  - Block Material             */ blockIsNull  ? "null" : mysteryBlock.getMaterial().name(),
                /* 2  - Player Name                */ playerIsNull ? "null" : playerName,
                /* 3  - Block Mines ASC            */ blockIsNull  ? "null" : decimal.format(mysteryBlock.getCurrentMines()),
                /* 4  - Block Mines DESC           */ blockIsNull  ? "null" : decimal.format(mysteryBlock.getRequiredMines() - mysteryBlock.getCurrentMines()),
                /* 5  - Block Required Mines       */ blockIsNull  ? "null" : decimal.format(mysteryBlock.getRequiredMines()),
                /* 6  - Player Current Mines       */ blockIsNull  ? "null" : playerIsNull ? "0" : decimal.format(mysteryBlock.getMineMap().getOrDefault(playerName,0)),
                /* 7  - Player Mine Speed          */ blockIsNull  ? "null" : playerIsNull ? "0" : decimal.format(mysteryBlock.getAntiCheatHandler().getSpeed(playerName)),
                /* 8  - Block Cooldown FORMAT      */ blockIsNull  ? "null" : translateTimeToTimer(time),
                /* 9  - Block Cooldown SHORT       */ blockIsNull  ? "null" : translateTimeToString(time),
                /* 10 - Block Cooldown PLAIN       */ blockIsNull  ? "null" : ((int) (time / 1000)) +"",
                /* 11 - Percentage String          */ blockIsNull  ? "null" : createPercentageString(mysteryBlock.getCurrentMines(), mysteryBlock.getRequiredMines()),
                /* 12 - Percentage Count           */ blockIsNull  ? "null" : Math.round(((mysteryBlock.getCurrentMines() / (float) mysteryBlock.getRequiredMines()) * 100) * 100F) / 100F +"",
                /* 13 - Schedule next              */ blockIsNull  ? "null" : new SimpleDateFormat("HH:mm").format(new Date(mysteryBlock.getScheduleHandler().next().getTime())),
                /* 14 - Schedule remaining FORMAT  */ blockIsNull  ? "null" : translateTimeToTimer(remaining),
                /* 15 - Schedule remaining SHORT   */ blockIsNull  ? "null" : translateTimeToString(remaining),
                /* 16 - Schedule remaining PLAIN   */ blockIsNull  ? "null" : ((int) (remaining / 1000)) +""
        );
    }

    public static String createPercentageString(int current, int required) {
        String line = "";
        float percentage = (current / (float) required) * 100;
        int n = PlaceholderHandler.getPercentageTotal();

        for (int i = 1; i <= n; i++) {
            if (percentage < (i * (100 / n)) - (100 / n) / 2F) {
                line += PlaceholderHandler.getPercentageLow();
            } else if (percentage < i * (100 / n)) {
                line += PlaceholderHandler.getPercentageHalf();
            } else {
                line += PlaceholderHandler.getPercentageFull();
            }
        }

        return line;
    }

    public static boolean isItemBypassed(ItemStack itemStack, List<String> bypassList) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            for (String bypass : bypassList) {
                String[] bypassArgs = bypass.split(";;");

                if (bypassArgs.length > 0 && itemMeta.hasDisplayName()) {
                    String originMatch = bypassArgs[0].replaceAll("§.", "").trim();
                    String targetMatch = itemMeta.getDisplayName().replaceAll("§.", "").trim();

                    if (originMatch.equalsIgnoreCase(targetMatch) || originMatch.matches(targetMatch)) {
                        if (bypassArgs.length > 1 && itemMeta.hasLore()) {
                            originMatch = bypassArgs[1].replaceAll("§.", "");
                            targetMatch = itemMeta.getLore().stream().map(line -> line.replaceAll("§.","")).collect(Collectors.joining("\\n"));

                            if (originMatch.equalsIgnoreCase(targetMatch) || originMatch.matches(targetMatch)) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    public static ItemStack getPlayerItemInHand(Player player) {
        ItemStack tool = null;

        if (Utils.getServerVersionID() >= 12) {
            return player.getInventory().getItemInMainHand();
        } else {
            tool = player.getItemInHand();
        }

        return tool;
    }

    public static ItemStack setPlayerItemInHand(Player player, ItemStack itemStack) {
        if (Utils.getServerVersionID() >= 12) {
            player.getInventory().setItemInMainHand(itemStack);
        } else {
            player.setItemInHand(itemStack);
        }

        return itemStack;
    }
}

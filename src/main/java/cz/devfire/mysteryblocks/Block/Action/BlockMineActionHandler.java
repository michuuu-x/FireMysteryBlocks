package cz.devfire.mysteryblocks.Block.Action;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Action.Enum.BlockActionSection;
import cz.devfire.mysteryblocks.Block.Action.Enum.BlockActionType;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.ActionBar;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockMineActionHandler extends AbstractBlockHandler {
    private final ArrayList<String> onReset = Lists.newArrayList();
    private final ArrayList<String> onMine = Lists.newArrayList();
    private final ArrayList<String> onDestroyGlobal = Lists.newArrayList();
    private final ArrayList<String> onDestroyEveryone = Lists.newArrayList();
    private final HashMap<Integer, ArrayList<String>> onDestroyPerPlace = Maps.newHashMap();

    public BlockMineActionHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("Action"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = true;

        if (enabled) {
            onReset.addAll(section.getStringList("OnReset"));
            onMine.addAll(section.getStringList("OnMine"));
            onDestroyGlobal.addAll(section.getStringList("OnDestroy.Global"));
            onDestroyEveryone.addAll(section.getStringList("OnDestroy.EveryPlace"));

            for (String place : section.getConfigurationSection("OnDestroy.PerPlace").getKeys(false)) {
                if (place.contains("~")) {
                    String[] placeArgs = place.split("~");

                    for (int i = Integer.parseInt(placeArgs[0]); i < Integer.parseInt(placeArgs[1]); i++) {
                        onDestroyPerPlace.put(i, Lists.newArrayList(section.getStringList("OnDestroy.PerPlace." + place)));
                    }
                } else {
                    onDestroyPerPlace.put(Integer.parseInt(place), Lists.newArrayList(section.getStringList("OnDestroy.PerPlace." + place)));
                }
            }
        }

        return true;
    }

    public void perform(List<String> actions, String playerName) {
        actions.forEach(action -> parseAction(action, playerName));
    }

    public void perform(BlockActionSection actionSection, String playerName) {
        switch (actionSection) {
            case RESET: {
                onReset.forEach(action -> parseAction(action,null));
                break;
            }

            case MINE: {
                onMine.forEach(action -> parseAction(action, playerName));
                break;
            }

            case DESTROY_GLOBAL: {
                onDestroyGlobal.forEach(action -> parseAction(action,null));
                break;
            }

            case DESTROY_EVERY_PLACE: {
                for (String player : mysteryBlock.getMineMap().keySet()) {
                    onDestroyEveryone.forEach(action -> parseAction(action, player));
                }

                break;
            }

            case DESTROY_PER_PLACE: {
                for (Integer place : onDestroyPerPlace.keySet()) {
                    List<String> placeActions = onDestroyPerPlace.get(place);

                    if (mysteryBlock.getMineList().size() > place - 1) {
                        String finalPlayerName = mysteryBlock.getMineList().get(place - 1).split("\\|")[0];
                        placeActions.forEach(action -> parseAction(action, finalPlayerName));
                    }
                }

                break;
            }
        }
    }

    public void parseAction(String actionLine, String playerName) {
        String[] actionLineArgs = actionLine.split(" ");

        BlockActionType actionType = BlockActionType.valueOf(Utils.stripBrackets(actionLineArgs[0]).replace("-", "_"));
        String actionString = actionLine.replace(actionLineArgs[0] +" ", "");
        actionString = Utils.parseBlockPlaceholders(mysteryBlock, playerName, actionString);

        double delay = 0;
        double percentage = -1;

        // Cache
        if (actionString.startsWith("$")) {
            actionString = actionString.substring(1);

            if (playerName != null && Bukkit.getPlayer(playerName) == null) {
                ArrayList<String> values = Lists.newArrayList(mysteryBlock.getName() +" || ["+ actionType.name().replace("_","-") +"] "+ actionString);

                if (plugin.getCache().exist(playerName.toLowerCase())) {
                    values.addAll(plugin.getCache().getStringList(playerName.toLowerCase()));
                }

                plugin.getCache().set(playerName.toLowerCase(), values);
                plugin.getCache().save();
                return;
            }
        }

        // Delay
        if (actionString.startsWith("@")) {
            String[] delayArgs = actionString.split("@",3);
            delay = Double.parseDouble(delayArgs[1]);

            actionString = actionString.substring(1);
            while (!actionString.startsWith("@")) {
                actionString = actionString.substring(1);
            }
            actionString = actionString.substring(1);
        }

        // Percentage
        if (actionString.startsWith("%")) {
            String[] percentageArgs = actionString.split("%",3);
            percentage = Double.parseDouble(percentageArgs[1]);

            actionString = actionString.substring(1);
            while (!actionString.startsWith("%")) {
                actionString = actionString.substring(1);
            }
            actionString = actionString.substring(1);
        }

        if (percentage == -1 || (100 * new Random().nextDouble()) <= percentage) {
            if (delay == 0D) {
                performAction(actionType, actionString, playerName == null ? null : Bukkit.getPlayer(playerName));
            } else {
                String finalActionString = actionString;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        performAction(actionType, finalActionString, playerName == null ? null : Bukkit.getPlayer(playerName));
                    }
                }.runTaskLater(plugin, Math.round(20 / 1000F * delay));
            }
        }
    }

    public void performAction(BlockActionType actionType, String actionString, Player player) {
        switch (actionType) {
            case COMMAND: {
                if (player != null) {
                    player.performCommand(actionString);
                }

                break;
            }

            case OP_COMMAND: {
                if (player != null) {
                    player.setOp(true);
                    player.performCommand(actionString);
                    player.setOp(false);
                }

                break;
            }

            case CONSOLE_COMMAND: {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actionString);
                break;
            }

            case MESSAGE: {
                if (player != null) {
                    player.sendMessage(Utils.cc(actionString));
                }

                break;
            }

            case WARN: {
                Bukkit.broadcast(Utils.cc(actionString),"firemysteryblocks.warn");
                break;
            }

            case TITLE: {
                if (actionString.contains("\n")) {
                    String[] actionStringArgs = actionString.split("\n");

                    player.sendTitle(actionStringArgs[0], actionStringArgs.length == 1 ? "" : actionStringArgs[1]);
                } else {
                    player.sendMessage(actionString, "");
                }

                break;
            }

            case BROADCAST: {
                Bukkit.broadcastMessage(Utils.cc(actionString));
                break;
            }

            case SOUND: {
                String[] soundArgs = actionString.split("-");

                Sound sound = null;
                float volume = 1;
                float pitch = 1;

                boolean er = false;
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
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] &cUnknown sound: \"" + actionString + "\"");

                    if (plugin.isPluginEnabled()) {
                        exception.printStackTrace();
                    }
                } else {
                    if (player == null) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), sound, volume, pitch);
                        }
                    } else {
                        player.playSound(player.getLocation(), sound, volume, pitch);
                    }
                }

                break;
            }

            case ACTIONBAR: {
                if (player == null) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        ActionBar.sendActionBar(p, Utils.cc(actionString));
                    }
                } else {
                    ActionBar.sendActionBar(player, Utils.cc(actionString));
                }

                break;
            }
        }
    }
}

package cz.devfire.mysteryblocks.Block.Handler.Action;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Handler.Action.Enum.BlockActionSection;
import cz.devfire.mysteryblocks.Block.Handler.Action.Enum.BlockActionType;
import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.Block.Handler.Action.Method.*;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Getter
public class BlockMineActionHandler extends AbstractBlockHandler {
    private final ArrayList<String> onReset = Lists.newArrayList();
    private final ArrayList<String> onMine = Lists.newArrayList();
    private final ArrayList<String> onDestroyGlobal = Lists.newArrayList();
    private final ArrayList<String> onDestroyEveryone = Lists.newArrayList();
    private final HashMap<Integer, ArrayList<String>> onDestroyPerPlace = Maps.newHashMap();
    private final HashMap<BlockActionType, ActionMethod> methods = Maps.newHashMap();

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

            methods.put(BlockActionType.ACTIONBAR, new ActionActionbarMethod(plugin));
            methods.put(BlockActionType.BROADCAST, new ActionBroadcastMethod(plugin));
            methods.put(BlockActionType.COMMAND, new ActionCommandMethod());
            methods.put(BlockActionType.CONSOLE_COMMAND, new ActionConsoleCommandMethod());
            methods.put(BlockActionType.EFFECT, new ActionEffectMethod());
            methods.put(BlockActionType.MESSAGE, new ActionMessageMethod());
            methods.put(BlockActionType.OP_COMMAND, new ActionOPCommandMethod());
            methods.put(BlockActionType.SOUND, new ActionSoundMethod(plugin));
            methods.put(BlockActionType.TITLE, new ActionTitleMethod(plugin));
            methods.put(BlockActionType.WARN, new ActionWarnMethod());
        }

        return true;
    }

    public void perform(List<String> actions, String playerName) {
        actions.forEach(action -> parseAction(action, playerName));
    }

    public void perform(BlockActionSection actionSection, String playerName) {
        switch (actionSection) {
            case RESET: {
                perform(onReset,null);
                break;
            }

            case MINE: {
                perform(onMine, playerName);
                break;
            }

            case DESTROY_GLOBAL: {
                perform(onDestroyGlobal,null);
                break;
            }

            case DESTROY_EVERY_PLACE: {
                for (String player : mysteryBlock.getMineMap().keySet()) {
                    perform(onDestroyEveryone, player);
                }

                break;
            }

            case DESTROY_PER_PLACE: {
                for (Integer place : onDestroyPerPlace.keySet()) {
                    List<String> placeActions = onDestroyPerPlace.get(place);

                    if (mysteryBlock.getMineList().size() > place - 1) {
                        String finalPlayerName = mysteryBlock.getMineList().get(place - 1).split("\\|")[0];
                        perform(placeActions, finalPlayerName);
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
            String finalActionString = actionString;

            BukkitRunnable run = new BukkitRunnable() {
                @Override
                public void run() {
                    methods.get(actionType).perform(finalActionString, playerName == null ? null : Bukkit.getPlayer(playerName));
                }
            };

            run.runTaskLater(plugin, Math.round((20 / 1000F) * delay));
        }
    }
}

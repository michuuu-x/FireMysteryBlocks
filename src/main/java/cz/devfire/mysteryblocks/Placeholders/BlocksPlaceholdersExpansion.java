package cz.devfire.mysteryblocks.Placeholders;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

public class BlocksPlaceholdersExpansion extends PlaceholderExpansion {
    private final MysteryBlocksPluginImpl plugin;

    public BlocksPlaceholdersExpansion(MysteryBlocksPluginImpl plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "firemysteryblocks";
    }

    @Override
    public String getAuthor() {
        return "Firestone82";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        String[] args = placeholder.split("_");

        if (player == null || args.length == 0 || plugin.getBlockHandler().getBlocks().size() == 0 || args.length < 2) {
            return null;
        }

        try {
            MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(args[1]);
            if (mysteryBlock == null) return null;

            switch (args[0].toUpperCase()) {
                case "REQUIRED": {
                    return mysteryBlock.getRequiredMines() + "";
                }

                case "TOTAL": {
                    switch (args[2].toUpperCase()) {
                        case "ASC":
                            return mysteryBlock.getCurrentMines() + "";
                        case "DESC":
                            return (mysteryBlock.getRequiredMines() - mysteryBlock.getCurrentMines()) + "";
                    }

                    break;
                }

                case "DESTROYS": {
                    return mysteryBlock.getTotalDestroys() + "";
                }

                case "COOLDOWN": {
                    switch (args[2].toUpperCase()) {
                        case "ACTIVE": {
                            return (mysteryBlock.getCooldownHandler().getCurrent() != 0) + "";
                        }

                        case "CURRENT": {
                            long time = mysteryBlock.getCooldownHandler().getTime();
                            long current = mysteryBlock.getCooldownHandler().getCurrent();

                            switch (args[3].toUpperCase()) {
                                case "FORMATTED": {
                                    if (time < 0 || time == 0) {
                                        return "00:00:00";
                                    } else {
                                        return Utils.translateTime(time);
                                    }
                                }

                                case "SHORT": {
                                    if (time < 0 || time == 0) {
                                        return "0s";
                                    } else {
                                        return Utils.setTimeSecondsToString(time / 1000);
                                    }
                                }

                                case "PLAIN": {
                                    if (time < 0 || time == 0) {
                                        return 0 + "";
                                    } else {
                                        return ((int) (time / 1000)) + "";
                                    }
                                }
                            }

                            break;
                        }
                    }
                }

                case "GET": {
                    switch (args[2].toUpperCase()) {
                        case "PLAYER": {
                            return "" + mysteryBlock.getMineMap().getOrDefault(player.getName(),0);
                        }

                        case "POSITION": {
                            LinkedHashMap<String, Integer> list = Maps.newLinkedHashMap(mysteryBlock.getMineMap());
                            list = Utils.sortMapByValue(list, false);

                            switch (args[4].toUpperCase()) {
                                case "NAME": {
                                    return "" + list.keySet().toArray()[Integer.parseInt(args[3]) - 1];
                                }

                                case "MINES": {
                                    return "" + mysteryBlock.getMineMap().getOrDefault((String) list.keySet().toArray()[Integer.parseInt(args[3]) - 1], 0);
                                }
                            }

                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.debug("Unknown placeholder: \"" + placeholder + "\"");

            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        return null;
    }
}

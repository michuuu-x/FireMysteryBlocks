package cz.devfire.mysteryblocks.Placeholders;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
    private final MysteryBlocksPlugin plugin;

    public PlaceholderExpansion(MysteryBlocksPlugin plugin) {
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
        DecimalFormat decimal = new DecimalFormat("#,###");

        if (player == null || args.length == 0 || plugin.getBlockHandler().getBlocks().size() == 0 || args.length < 2) {
            return null;
        }

        try {
            MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(args[1]);
            if (mysteryBlock == null) return null;

            switch (args[0].toUpperCase()) {
                case "REQUIRED": {
                    return decimal.format(mysteryBlock.getRequiredMines());
                }

                case "TOTAL": {
                    switch (args[2].toUpperCase()) {
                        case "ASC": {
                            return decimal.format(mysteryBlock.getCurrentMines());
                        }

                        case "DESC": {
                            return decimal.format(mysteryBlock.getRequiredMines() - mysteryBlock.getCurrentMines());
                        }
                    }

                    break;
                }

                case "DESTROYS": {
                    return decimal.format(mysteryBlock.getTotalDestroys());
                }

                case "PROGRESS": {
                    switch (args[2].toUpperCase()) {
                        case "BAR": {
                            return Utils.createPercentageString((int) mysteryBlock.getCurrentMines(), mysteryBlock.getRequiredMines());
                        }

                        case "PERCENTAGE": {
                            return Math.round(((mysteryBlock.getCurrentMines() / (float) mysteryBlock.getRequiredMines()) * 100) * 100F) / 100F +"";
                        }
                    }

                    break;
                }

                case "SCHEDULE": {
                    switch (args[2].toUpperCase()) {
                        case "PREV": {
                            String format = args.length > 3 ? args[3] : "YYYY-MM-dd HH:mm:ss";
                            return new SimpleDateFormat(format).format(mysteryBlock.getScheduleHandler().prev());
                        }

                        case "NEXT": {
                            String format = args.length > 3 ? args[3] : "YYYY-MM-dd HH:mm:ss";
                            return new SimpleDateFormat(format).format(mysteryBlock.getScheduleHandler().next());
                        }

                        case "REMAINING": {
                            switch (args[3].toUpperCase()) {
                                case "FORMATTED": {
                                    return Utils.translateTimeToTimer(Math.abs(System.currentTimeMillis() - mysteryBlock.getScheduleHandler().prev().getTime() - mysteryBlock.getScheduleHandler().getDestroyTime()));
                                }

                                case "SHORT": {
                                    return Utils.translateTimeToString(Math.abs(System.currentTimeMillis() - mysteryBlock.getScheduleHandler().prev().getTime() - mysteryBlock.getScheduleHandler().getDestroyTime()));
                                }

                                case "PLAIN": {
                                    return ((int) (Math.abs(System.currentTimeMillis() - mysteryBlock.getScheduleHandler().prev().getTime() - mysteryBlock.getScheduleHandler().getDestroyTime()) / 1000)) + "";
                                }
                            }
                        }
                    }
                }

                case "REGENERATED": {
                    return decimal.format(mysteryBlock.getRegenerationHandler().getAmount());
                }

                case "COOLDOWN": {
                    switch (args[2].toUpperCase()) {
                        case "ACTIVE": {
                            return mysteryBlock.getCooldownHandler().isUnder() +"";
                        }

                        case "CURRENT": {
                            long time = mysteryBlock.getCooldownHandler().getETA();

                            switch (args[3].toUpperCase()) {
                                case "FORMATTED": {
                                    return Utils.translateTimeToTimer(time);
                                }

                                case "SHORT": {
                                    return Utils.translateTimeToString(time);
                                }

                                case "PLAIN": {
                                    return ((int) (time / 1000)) + "";
                                }
                            }

                            break;
                        }
                    }
                }

                case "GET": {
                    switch (args[2].toUpperCase()) {
                        case "PLAYER": {
                            return decimal.format(mysteryBlock.getMineMap().getOrDefault(player.getName(),0));
                        }

                        case "POSITION": {
                            int pos = Integer.parseInt(args[3]) - 1;

                            switch (args[4].toUpperCase()) {
                                case "NAME": {
                                    if (pos >= mysteryBlock.getMineMap().size()) {
                                        return Language.EMPTY.getMessage();
                                    } else {
                                        return "" + mysteryBlock.getMineMap().keySet().toArray()[pos];
                                    }
                                }

                                case "MINES": {
                                    if (pos >= mysteryBlock.getMineMap().size()) {
                                        return "0";
                                    } else {
                                        return decimal.format(mysteryBlock.getMineMap().getOrDefault((String) mysteryBlock.getMineMap().keySet().toArray()[pos],0));
                                    }
                                }
                            }

                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("Unknown placeholder: \"" + placeholder + "\"");
            e.printStackTrace();
        }

        return null;
    }
}

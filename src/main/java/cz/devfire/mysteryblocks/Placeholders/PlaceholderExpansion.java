package cz.devfire.mysteryblocks.Placeholders;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

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
            MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(args[0]);
            if (mysteryBlock == null) return null;

            switch (args[1].toUpperCase()) {
                case "REQUIRED": {
                    return decimal.format(mysteryBlock.getRequiredMines());
                }

                case "CURRENT": {
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

                case "PLAYER": {
                    return String.valueOf(mysteryBlock.getMineMap().getOrDefault(player.getName(), 0));
                }

                case "POSITION": {
                    int pos = Integer.parseInt(args[2]) - 1;

                    switch (args[3].toUpperCase()) {
                        case "NAME": {
                            if (pos >= mysteryBlock.getMineMap().size()) {
                                return Language.EMPTY.getMessage();
                            }

                            return String.valueOf(mysteryBlock.getMineMap().keySet().toArray()[pos]);
                        }

                        case "MINES": {
                            if (pos >= mysteryBlock.getMineMap().size()) {
                                return "0";
                            }

                            return decimal.format(mysteryBlock.getMineMap().getOrDefault((String) mysteryBlock.getMineMap().keySet().toArray()[pos],0));
                        }
                    }

                    break;
                }

                case "PROGRESS": {
                    switch (args[2].toUpperCase()) {
                        case "BAR": {
                            return Utils.createPercentageString((int) mysteryBlock.getCurrentMines(), mysteryBlock.getRequiredMines());
                        }

                        case "PERCENTAGE": {
                            return String.valueOf(Math.round(((mysteryBlock.getCurrentMines() / (float) mysteryBlock.getRequiredMines()) * 100) * 100F) / 100F);
                        }
                    }

                    break;
                }

                case "DESTROYS": {
                    return decimal.format(mysteryBlock.getTotalDestroys());
                }

                case "COOLDOWN": {
                    if (!mysteryBlock.getCooldownHandler().isEnabled()) break;

                    switch (args[2].toUpperCase()) {
                        case "ACTIVE": {
                            return String.valueOf(mysteryBlock.getCooldownHandler().isUnder());
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
                                    return String.valueOf((int) (time / 1000));
                                }
                            }

                            break;
                        }
                    }

                    break;
                }

                case "SCHEDULE": {
                    if (!mysteryBlock.getScheduleHandler().isEnabled()) break;

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
                                    return String.valueOf((int) (Math.abs(System.currentTimeMillis() - mysteryBlock.getScheduleHandler().prev().getTime() - mysteryBlock.getScheduleHandler().getDestroyTime()) / 1000));
                                }
                            }

                            break;
                        }
                    }

                    break;
                }

                case "HISTORY": {
                    if (!mysteryBlock.getHologramHandler().isEnabled()) break;

                    switch (args[2].toUpperCase()) {
                        case "SIZE": {
                            return decimal.format(mysteryBlock.getHistoryHandler().getHistoryList().size());
                        }

                        case "DATE": {
                            int pos = Integer.parseInt(args[3]) - 1;

                            if (pos >= mysteryBlock.getHistoryHandler().getHistoryList().size()) {
                                return Language.EMPTY.getMessage();
                            }

                            return new SimpleDateFormat(mysteryBlock.getHistoryHandler().getDateFormat()).format(mysteryBlock.getHistoryHandler().getHistory(pos).getDate());
                        }

                        case "POSITION": {
                            int historyPos = Integer.parseInt(args[3]) - 1;
                            int playerPos = Integer.parseInt(args[4]) - 1;

                            if (historyPos >= mysteryBlock.getHistoryHandler().getHistoryList().size()) {
                                return Language.EMPTY.getMessage();
                            }

                            switch (args[5].toUpperCase()) {
                                case "MINES": {
                                    return decimal.format(mysteryBlock.getHistoryHandler().getHistory(historyPos).getPosition(playerPos).getSecond());
                                }

                                case "NAME": {
                                    return mysteryBlock.getHistoryHandler().getHistory(historyPos).getPosition(playerPos).getFirst();
                                }
                            }

                            break;
                        }
                    }

                    break;
                }

                case "REGENERATION": {
                    if (!mysteryBlock.getRegenerationHandler().isEnabled()) break;

                    switch (args[2].toUpperCase()) {
                        case "ACTIVE": {
                            return String.valueOf(mysteryBlock.getRegenerationHandler().isUnder());
                        }

                        case "AMOUNT": {
                            switch (mysteryBlock.getRegenerationHandler().getType()) {
                                case FULL: {
                                    return "0";
                                }

                                case ADD: {
                                    return decimal.format(mysteryBlock.getRequiredTempMines());
                                }

                                case HEAL: {
                                    int mines = 0;
                                    for (String miner : mysteryBlock.getMineMap().keySet()) {
                                        mines += mysteryBlock.getMineMap().get(miner);
                                    }

                                    return decimal.format(mines - mysteryBlock.getCurrentMines());
                                }
                            }

                            break;
                        }

                        case "CURRENT": {
                            long time = mysteryBlock.getRegenerationHandler().getETA();

                            switch (args[3].toUpperCase()) {
                                case "FORMATTED": {
                                    return Utils.translateTimeToTimer(time);
                                }

                                case "SHORT": {
                                    return Utils.translateTimeToString(time);
                                }

                                case "PLAIN": {
                                    return String.valueOf((int) (time / 1000));
                                }
                            }

                            break;
                        }
                    }

                    break;
                }
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §cUnknown placeholder: \"" + placeholder + "\"");

            if (MysteryBlocksPlugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        return null;
    }
}

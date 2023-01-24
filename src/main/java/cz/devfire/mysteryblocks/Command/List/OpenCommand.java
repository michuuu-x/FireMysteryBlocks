package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.BlockGUIHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class OpenCommand implements ICommand {

    @Override
    public String getLabel() {
        return "open";
    }

    @Override
    public String getUsage() {
        return "mb open <name> [player]";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.open";
    }

    @Override
    public String getDescription() {
        return "Open block GUI";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public void perform(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        if (args.length == 1) {
            Language.USAGE.send(sender, getUsage());
        } else {
            String targetBlock = args[1];
            MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(targetBlock);

            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    Language.PLAYER_ONLY_COMMAND.send(sender);
                    return;
                }

                if (mysteryBlock == null) {
                    Language.BLOCK_UNKNOWN.send(sender, targetBlock);
                } else {
                    BlockGUIHandler guiHandler = mysteryBlock.getGUIHandler();

                    if (guiHandler.isEnabled()) {
                        guiHandler.open((Player) sender);
                    } else {
                        Language.BLOCK_GUI_DISABLED.send(sender);
                    }
                }
            } else if (args.length == 3) {
                if (mysteryBlock == null) {
                    Language.BLOCK_UNKNOWN.send(sender, targetBlock);
                } else {
                    BlockGUIHandler guiHandler = mysteryBlock.getGUIHandler();

                    if (guiHandler.isEnabled()) {
                        Player player = Bukkit.getPlayer(args[2]);

                        if (player == null) {
                            Language.NO_PLAYER.send(sender);
                        } else {
                            guiHandler.open(player);
                        }
                    } else {
                        Language.BLOCK_GUI_DISABLED.send(sender);
                    }
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        if (args.length > 2) {
            return Utils.copyMatches(args[2], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }

        if (args.length > 1) {
            return Utils.copyMatches(args[1], plugin.getBlockHandler().getBlocks().stream().map(MysteryBlock::getName).collect(Collectors.toList()));
        }

        return Lists.newArrayList();
    }
}

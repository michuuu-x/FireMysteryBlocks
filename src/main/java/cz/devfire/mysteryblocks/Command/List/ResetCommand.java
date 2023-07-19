package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public final class ResetCommand implements ICommand {

    @Override
    public String getLabel() {
        return "reset";
    }

    @Override
    public String getUsage() {
        return "mb reset <name>";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.command.reset";
    }

    @Override
    public String getDescription() {
        return "Reset existing mysteryblock progress";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public void perform(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        if (args.length == 1) {
            Language.USAGE.send(sender, getUsage());
        } else {
            String targetBlock = args[1];
            MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(targetBlock);

            if (mysteryBlock == null) {
                Language.BLOCK_UNKNOWN.send(sender, targetBlock);
            } else {
                mysteryBlock.reset(true);
                Language.BLOCK_RESET.send(sender, mysteryBlock.getName());
            }
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        if (args.length > 1) {
            return Utils.copyMatches(args[1], plugin.getBlockHandler().getBlocks().stream().map(MysteryBlock::getName).collect(Collectors.toList()));
        }

        return Lists.newArrayList();
    }
}

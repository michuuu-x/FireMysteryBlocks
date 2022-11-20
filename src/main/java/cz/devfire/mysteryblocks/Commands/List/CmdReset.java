package cz.devfire.mysteryblocks.Commands.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import cz.devfire.mysteryblocks.api.Commands.ICommand;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class CmdReset implements ICommand {

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
        return "bmysteryblocks.reset";
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
            String mysteryBlock = args[1];

            for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
                if (block.getName().equalsIgnoreCase(mysteryBlock)) {
                    block.reset(false);
                    Language.BLOCK_RESETED.send(sender, mysteryBlock);

                    return;
                }
            }

            Language.BLOCK_UNKNOWN.send(sender, mysteryBlock);
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        if (args.length > 1) {
            ArrayList<String> names = Lists.newArrayList();

            for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
                names.add(block.getName());
            }

            return Utils.copyMatches(args[1], names);
        }

        return Lists.newArrayList();
    }
}

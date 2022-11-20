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

public final class CmdResetAll implements ICommand {

    @Override
    public String getLabel() {
        return "resetall";
    }

    @Override
    public String getUsage() {
        return "mb resetall";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.resetall";
    }

    @Override
    public String getDescription() {
        return "Reset all mysteryblocks progress";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public void perform(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
            block.reset(false);
        }

        Language.BLOCKS_RESETED.send(sender);
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

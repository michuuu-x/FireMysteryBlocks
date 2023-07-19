package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ResetAllCommand implements ICommand {

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
        return "firemysteryblocks.command.resetall";
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
            block.reset(true);
        }

        Language.BLOCK_RESET_ALL.send(sender);
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

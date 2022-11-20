package cz.devfire.mysteryblocks.Commands.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import cz.devfire.mysteryblocks.api.Commands.ICommand;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class CmdList implements ICommand {

    @Override
    public String getLabel() {
        return "list";
    }

    @Override
    public String getUsage() {
        return "mb list";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.list";
    }

    @Override
    public String getDescription() {
        return "Shows mystery blocks list";
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
        Language.LIST.send(sender);

        for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
            Language.LIST_BLOCK.send(sender,
                    block.getName(), Utils.locationToString(block.getLocation()), block.getMaterial().name(), block.getCurrentMines() + "", block.getRequiredMines() + "");
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

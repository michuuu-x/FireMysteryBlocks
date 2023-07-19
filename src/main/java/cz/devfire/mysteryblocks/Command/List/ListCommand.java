package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ListCommand implements ICommand {

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
        return "firemysteryblocks.command.list";
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
        Language.BLOCK_LIST.send(sender);

        plugin.getBlockHandler().getBlocks().forEach(block -> Language.BLOCK_LIST_ITEM.send(sender,
                block.getName(),
                Utils.putLocationToString(block.getLocation()),
                block.getMaterial().name(),
                block.getCurrentMines() + "",
                block.getRequiredMines() + ""
        ));
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.History.BlockHistoryHandler;
import cz.devfire.mysteryblocks.Block.Handler.History.Object.History;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Pair;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public final class HistoryCommand implements ICommand {

    @Override
    public String getLabel() {
        return "history";
    }

    @Override
    public String getUsage() {
        return "mb history <block> <entry>";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.history";
    }

    @Override
    public String getDescription() {
        return "Shows block history";
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public void perform(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        String targetBlock = args[1];
        int entry = 0;

        try {
            entry = Integer.parseInt(args[2]);
        } catch (Exception e) {
            Language.NEGATIVE.send(sender);
            return;
        }

        MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(targetBlock);
        if (mysteryBlock == null) {
            Language.BLOCK_UNKNOWN.send(sender, targetBlock);
            return;
        }

        BlockHistoryHandler historyHandler = mysteryBlock.getHistoryHandler();
        if (!historyHandler.isEnabled()){
            Language.BLOCK_HISTORY_DISABLED.send(sender);
            return;
        }

        if (historyHandler.getSaveCount() < entry) {
            Language.BLOCK_HISTORY_OVER.send(sender, historyHandler.getSaveCount());
            return;
        }

        History history = historyHandler.getHistory(historyHandler.getSaveCount() - entry);
        if (history.getDate() == null || entry <= 0) {
            Language.BLOCK_HISTORY_EMPTY.send(sender, historyHandler.getSaveCount());
            return;
        }

        Language.BLOCK_HISTORY.send(sender,entry, targetBlock, mysteryBlock.getHistoryHandler().getDateFormat());
        int i = 1;
        for (Pair<String, Integer> pair : history.getList()) {
            Language.BLOCK_HISTORY_LINE.send(sender, i++, pair.getFirst(), pair.getSecond());
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

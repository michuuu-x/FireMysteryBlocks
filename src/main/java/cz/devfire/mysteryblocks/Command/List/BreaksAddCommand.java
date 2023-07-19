package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.Hologram.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class BreaksAddCommand implements ICommand {

    @Override
    public String getLabel() {
        return "breaks-add";
    }

    @Override
    public String getUsage() {
        return "mb breaks-add <block> <number> [player]";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.command.breaks-add";
    }

    @Override
    public String getDescription() {
        return "Add breaks to block";
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    public void perform(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        String targetBlock = args[1];
        int number = 0;

        try {
            number = Integer.parseInt(args[2]);
        } catch (Exception e) {
            Language.NUMBER_NEGATIVE.send(sender);
            return;
        }

        MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(targetBlock);
        if (mysteryBlock == null) {
            Language.BLOCK_UNKNOWN.send(sender, targetBlock);
            return;
        }

        if (number <= 0) {
            Language.NUMBER_NEGATIVE.send(sender);
            return;
        }

        if (mysteryBlock.getCurrentMines() + number > mysteryBlock.getRequiredMines()) {
            Language.NUMBER_NEGATIVE.send(sender);
            return;
        }

        if (args.length == 4) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[3]);

            if (offlinePlayer == null) {
                Language.NO_PLAYER.send(sender);
                return;
            }

            mysteryBlock.getMineMap().put(offlinePlayer.getName(), mysteryBlock.getMineMap().getOrDefault(offlinePlayer.getName(),0) + number);
            Language.BLOCK_DAMAGE_ADDED_AS.send(sender, number, offlinePlayer.getName());
        } else {
            Language.BLOCK_DAMAGE_ADDED.send(sender, number);
        }

        mysteryBlock.setCurrentMines(mysteryBlock.getCurrentMines() + number);
        mysteryBlock.update();

        BlockHologramHandler blockHologramHandler = mysteryBlock.getHologramHandler();
        if (blockHologramHandler.isEnabled() && blockHologramHandler.getHologram() != null) {
            blockHologramHandler.getHologram().update();
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Utils.copyMatches(args[1], plugin.getBlockHandler().getBlocks().stream().map(MysteryBlock::getName).collect(Collectors.toList()));
        }

        if (args.length == 4) {
            return Utils.copyMatches(args[3], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }

        return Lists.newArrayList();
    }
}

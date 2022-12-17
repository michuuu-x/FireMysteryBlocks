package cz.devfire.mysteryblocks.Commands.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import cz.devfire.mysteryblocks.api.Commands.ICommand;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class CmdSet implements ICommand {

    @Override
    public String getLabel() {
        return "set";
    }

    @Override
    public String getUsage() {
        return "mb set <name>";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.set";
    }

    @Override
    public String getDescription() {
        return "Reposition existing mysteryblock";
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
        if (!(sender instanceof Player)) {
            Language.PLAYER_ONLY_COMMAND.send(sender);
            return;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            Language.USAGE.send(player, getUsage());
        } else {
            Block targetBlock = player.getTargetBlock(Sets.newHashSet(), 5);
            String mysteryBlock = args[1];

            if (targetBlock.getType() == Material.AIR) {
                Language.BLOCK_NOT_FOUND.send(player);
                return;
            }

            for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
                if (targetBlock.equals(block.getBlock())) {
                    Language.BLOCK_ALREADY_SET.send(sender, block.getName());
                    return;
                }
            }

            for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
                if (block.getName().equalsIgnoreCase(mysteryBlock)) {
                    block.redefine(targetBlock);
                    Language.BLOCK_SET.send(player, mysteryBlock);

                    return;
                }
            }

            Language.BLOCK_UNKNOWN.send(player, mysteryBlock);
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

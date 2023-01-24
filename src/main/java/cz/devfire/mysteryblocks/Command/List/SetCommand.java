package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class SetCommand implements ICommand {

    @Override
    public String getLabel() {
        return "set";
    }

    @Override
    public String getUsage() {
        return "mb set";
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

        if (args.length == 1) {
            Language.USAGE.send(sender, getUsage());
        } else {
            Player player = (Player) sender;
            Block targetBlock = Utils.getServerVersionID() == 8 ? player.getTargetBlock(Sets.newHashSet(),5) : player.getTargetBlockExact(5);
            String mysteryBlock = args[1];

            if (targetBlock == null || targetBlock.getType() == Material.AIR) {
                Language.BLOCK_NOT_FOUND.send(player);
                return;
            }

            for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
                if (targetBlock.equals(block.getLocation().getBlock())) {
                    Language.BLOCK_ALREADY_SET.send(sender, block.getName());
                    return;
                }
            }

            for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
                if (block.getName().equalsIgnoreCase(mysteryBlock)) {
                    block.redefine(targetBlock.getLocation());

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
            return Utils.copyMatches(args[1], plugin.getBlockHandler().getBlocks().stream().map(MysteryBlock::getName).collect(Collectors.toList()));
        }

        return Lists.newArrayList();
    }
}

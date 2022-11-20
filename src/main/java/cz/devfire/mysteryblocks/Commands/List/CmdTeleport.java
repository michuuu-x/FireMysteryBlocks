package cz.devfire.mysteryblocks.Commands.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import cz.devfire.mysteryblocks.api.Commands.ICommand;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class CmdTeleport implements ICommand {

    @Override
    public String getLabel() {
        return "tp";
    }

    @Override
    public String getUsage() {
        return "mb tp <name>";
    }

    @Override
    public String getPermission() {
        return "bmysteryblocks.teleport";
    }

    @Override
    public String getDescription() {
        return "Teleports you to mystery block";
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
        if (!(sender instanceof Player player)) {
            Language.PLAYER_ONLY_COMMAND.send(sender);
            return;
        }

        if (args.length == 1) {
            Language.USAGE.send(player, getUsage());
        } else {
            String mysteryBlock = args[1];

            for (MysteryBlock block : plugin.getBlockHandler().getBlocks()) {
                if (block.getName().equalsIgnoreCase(mysteryBlock)) {
                    Language.TELEPORTED.send(player, block.getName());
                    player.teleport(block.getLocation().clone().add(0, 3, 0));
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

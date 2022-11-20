package cz.devfire.mysteryblocks.Commands.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import cz.devfire.mysteryblocks.api.Commands.ICommand;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class CmdCreate implements ICommand {

    @Override
    public String getLabel() {
        return "create";
    }

    @Override
    public String getUsage() {
        return "mb create <name>";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.create";
    }

    @Override
    public String getDescription() {
        return "Creates a new mysteryblock";
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
        }

        if (args.length == 1) {
            Language.USAGE.send(sender, getUsage());
        } else {
            String name = args[1];

            if (plugin.getBlockHandler().getBlock(name) != null) {
                Language.BLOCK_NOT_FOUND.send(sender, name);
                return;
            }

            plugin.getBlockHandler().addBlock(name);
            Language.BLOCK_CREATED.send(sender, name);

            if (sender instanceof Player) {
                MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(name);

                if (mysteryBlock != null) {
                    Location location = ((Player) sender).getLocation().getBlock().getLocation().add(0, 1, 0);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                mysteryBlock.redefine(location);
                                ((Player) sender).teleport(((Player) sender).getLocation().getBlock().getLocation().add(0,2,0));
                            } catch (Exception e) { /* */ }
                        }
                    }.runTaskLater(plugin, 2L);
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

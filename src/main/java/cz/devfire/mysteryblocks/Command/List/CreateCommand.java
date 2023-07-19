package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class CreateCommand implements ICommand {

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
        return "firemysteryblocks.command.create";
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
        if (args.length == 1) {
            Language.USAGE.send(sender, getUsage());
        } else {
            String name = args[1];

            if (plugin.getBlockHandler().getBlock(name) != null) {
                Language.BLOCK_EXISTS.send(sender, name);
                return;
            }

            plugin.getBlockHandler().loadBlock(name);
            Language.BLOCK_CREATED.send(sender, name);

            if (sender instanceof Player) {
                MysteryBlock mysteryBlock = plugin.getBlockHandler().getBlock(name);
                Player player = (Player) sender;

                if (mysteryBlock != null) {
                    Location location = player.getLocation().getBlock().getLocation().add(0,1,0);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                mysteryBlock.redefine(location);
                                player.teleport(location.clone().add(0.5,3,0.5));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.runTaskLater(plugin,2L);
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

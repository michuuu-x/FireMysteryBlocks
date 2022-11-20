package cz.devfire.mysteryblocks.api.Commands;

import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {

    String getLabel();

    String getUsage();

    String getPermission();

    String getDescription();

    int getMinArgs();

    int getMaxArgs();

    void perform(MysteryBlocksPlugin plugin, CommandSender sender, String[] args);

    List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args);

}

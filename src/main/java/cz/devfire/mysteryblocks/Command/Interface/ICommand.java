package cz.devfire.mysteryblocks.Command.Interface;

import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
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

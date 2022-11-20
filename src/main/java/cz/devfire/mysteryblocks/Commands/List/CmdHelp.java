package cz.devfire.mysteryblocks.Commands.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.api.Commands.ICommand;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class CmdHelp implements ICommand {

    @Override
    public String getLabel() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "mysteryblocks help";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.help";
    }

    @Override
    public String getDescription() {
        return "Shows this message";
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
        Language.HELP.send(sender);

        for (ICommand command : plugin.getCommandsHandler().getSubCommands()) {
            if (command.getDescription() == null) continue;

            Language.HELP_COMMAND.send(sender, command.getUsage(), command.getDescription());
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class HelpCommand implements ICommand {

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
        return "firemysteryblocks.command.help";
    }

    @Override
    public String getDescription() {
        return "Show this message";
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

        for (ICommand command : plugin.getCommandHandler().getSubCommands()) {
            if (command.getDescription() == null) continue;

            Language.HELP_COMMAND.send(sender, command.getUsage(), command.getDescription());
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

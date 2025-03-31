package cz.devfire.mysteryblocks.Command;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Command.List.*;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandHandler extends AbstractHandler implements CommandExecutor, TabCompleter {
    private final List<ICommand> subCommands = Lists.newArrayList();
    private final String command;

    public CommandHandler(MysteryBlocksPlugin plugin, String pluginCommand) {
        super(plugin);
        this.command = pluginCommand;

        Bukkit.getPluginCommand(pluginCommand).setExecutor(this);
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = true;

        subCommands.add(new HelpCommand());
        subCommands.add(new InfoCommand());
        subCommands.add(new CreateCommand());
        subCommands.add(new DeleteCommand());
        subCommands.add(new ListCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new ResetAllCommand());
        subCommands.add(new ResetCommand());
        subCommands.add(new SetCommand());
        subCommands.add(new TeleportCommand());
        subCommands.add(new FinishCommand());
        subCommands.add(new OpenCommand());
        subCommands.add(new HistoryCommand());
        subCommands.add(new BreaksAddCommand());
        subCommands.add(new HideCommand());
        subCommands.add(new MessageCommand());
        subCommands.add(new BypassCommand());

        return true;
    }

    @Override
    public boolean destroy() {
        subCommands.clear();
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            for (ICommand subCommand : subCommands) {
                if (subCommand.getLabel().equalsIgnoreCase(args[0])) {
                    if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                        Language.NO_PERMISSION.send(sender);
                        return true;
                    }

                    if (args.length < subCommand.getMinArgs() || args.length > subCommand.getMaxArgs()) {
                        Language.USAGE.send(sender, subCommand.getUsage());
                        return true;
                    }

                    subCommand.perform(plugin, sender, args);
                    return true;
                }
            }
        }

        Language.NO_COMMAND.send(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (ICommand subCommand : subCommands) {
                if (subCommand.getLabel().equalsIgnoreCase(args[0])) {
                    if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                        return new ArrayList<>();
                    }

                    return subCommand.tabComplete(plugin, sender, args);
                }
            }
        }

        List<String> list = new ArrayList<>();

        for (ICommand subCommand : subCommands) {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                if (subCommand.getLabel().startsWith(args[0])) {
                    list.add(subCommand.getLabel());
                }
            }
        }

        return list;
    }

}

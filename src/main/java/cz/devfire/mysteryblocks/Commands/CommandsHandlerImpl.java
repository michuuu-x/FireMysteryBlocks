package cz.devfire.mysteryblocks.Commands;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Commands.List.*;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.api.Commands.CommandsHandler;
import cz.devfire.mysteryblocks.api.Commands.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public final class CommandsHandlerImpl implements CommandsHandler, CommandExecutor, TabCompleter {
    private final MysteryBlocksPluginImpl plugin;

    private final List<ICommand> subCommands = Lists.newArrayList();
    private final String command;

    public CommandsHandlerImpl(MysteryBlocksPluginImpl plugin, String pluginCommand) {
        this.plugin = plugin;
        this.command = pluginCommand;

        Bukkit.getPluginCommand(pluginCommand).setExecutor(this);
    }

    public void load() {
        subCommands.clear();

        subCommands.add(new CmdHelp());
        subCommands.add(new CmdInfo());
        subCommands.add(new CmdList());
        subCommands.add(new CmdReload());
        subCommands.add(new CmdTeleport());
        subCommands.add(new CmdCreate());
        subCommands.add(new CmdDelete());
        subCommands.add(new CmdSet());
        subCommands.add(new CmdReset());
        subCommands.add(new CmdResetAll());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            for (ICommand subCommand : subCommands) {
                if (subCommand.getLabel().equalsIgnoreCase(args[0])) {
                    if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                        Language.NO_PERMISSIONS.send(sender);
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
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
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

    public List<ICommand> getSubCommands() {
        return subCommands;
    }
}

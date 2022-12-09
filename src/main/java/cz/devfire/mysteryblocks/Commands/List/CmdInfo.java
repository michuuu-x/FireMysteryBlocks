package cz.devfire.mysteryblocks.Commands.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Commands.ICommand;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class CmdInfo implements ICommand {

    @Override
    public String getLabel() {
        return "info";
    }

    @Override
    public String getUsage() {
        return "mb info";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
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
        sender.sendMessage(Utils.cc("&c&lServer &8&l» &7FireMysteryBlocks&8("+ plugin.getDescription().getVersion() +")&7 made by &eFirestone82"));
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

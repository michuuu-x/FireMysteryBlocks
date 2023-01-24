package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand implements ICommand {

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public String getUsage() {
        return "mb reload";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.reload";
    }

    @Override
    public String getDescription() {
        return "Reloads plugin";
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
        Language.RELOAD.send(sender, plugin.reload());
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

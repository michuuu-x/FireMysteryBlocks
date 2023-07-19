package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Player.Object.MysteryPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class HideCommand implements ICommand {

    @Override
    public String getLabel() {
        return "visibility";
    }

    @Override
    public String getUsage() {
        return "mb visibility";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.command.visibility";
    }

    @Override
    public String getDescription() {
        return "Toggle player visibility next to blocks";
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
        if (!(sender instanceof Player)) {
            Language.PLAYER_ONLY_COMMAND.send(sender);
        }

        Player player = (Player) sender;
        MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(player.getName());

        if (mysteryPlayer.isVisualEnabled()) {
            mysteryPlayer.setVisualEnabled(false);
            Language.BLOCK_VISIBILITY_TOGGLE.send(player, Language.BLOCK_VISIBILITY_TOGGLE_DISABLED);
        } else {
            mysteryPlayer.setVisualEnabled(true);
            Language.BLOCK_VISIBILITY_TOGGLE.send(player, Language.BLOCK_VISIBILITY_TOGGLE_ENABLED);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                mysteryPlayer.save();
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

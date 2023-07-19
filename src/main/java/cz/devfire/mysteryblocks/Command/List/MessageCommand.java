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

public final class MessageCommand implements ICommand {

    @Override
    public String getLabel() {
        return "messages";
    }

    @Override
    public String getUsage() {
        return "mb messages";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.command.messages";
    }

    @Override
    public String getDescription() {
        return "Toggle viewing messages of MysteryBlocks";
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

        if (mysteryPlayer.isMessageEnabled()) {
            mysteryPlayer.setMessageEnabled(false);
            Language.BLOCK_MESSAGE_TOGGLE.send(player, Language.BLOCK_MESSAGE_TOGGLE_DISABLED);
        } else {
            mysteryPlayer.setMessageEnabled(true);
            Language.BLOCK_MESSAGE_TOGGLE.send(player, Language.BLOCK_MESSAGE_TOGGLE_ENABLED);
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

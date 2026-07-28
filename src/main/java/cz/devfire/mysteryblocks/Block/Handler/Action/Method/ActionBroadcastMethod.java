package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Player.Object.MysteryPlayer;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBroadcastMethod implements ActionMethod {
    private final MysteryBlocksPlugin plugin;

    public ActionBroadcastMethod(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    public void perform(String actionString, Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(p.getName());

            if (mysteryPlayer.isMessageEnabled()) {
                p.sendMessage(Utils.mm(Utils.ph(actionString, p)));
            }
        }
    }

}

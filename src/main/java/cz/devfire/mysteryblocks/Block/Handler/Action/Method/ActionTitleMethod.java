package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Player.Object.MysteryPlayer;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionTitleMethod implements ActionMethod {
    private final MysteryBlocksPlugin plugin;

    public ActionTitleMethod(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    public void perform(String actionString, Player player) {
        actionString = Utils.cc(Utils.ph(actionString, player));

        if (actionString.contains("\n")) {
            String[] actionStringArgs = actionString.split("\n");

            if (player == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(actionStringArgs[0], actionStringArgs.length == 1 ? "" : actionStringArgs[1]);
                }
            } else {
                player.sendTitle(actionStringArgs[0], actionStringArgs.length == 1 ? "" : actionStringArgs[1]);
            }
        } else {
            if (player == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(p.getName());

                    if (mysteryPlayer.isMessageEnabled()) {
                        p.sendTitle(actionString, "");
                    }
                }
            } else {
                player.sendTitle(actionString, "");
            }
        }
    }

}

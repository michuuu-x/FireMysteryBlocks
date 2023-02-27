package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionTitleMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
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
                    p.sendTitle(actionString, "");
                }
            } else {
                player.sendTitle(actionString, "");
            }
        }
    }

}

package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBroadcastMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Utils.cc(Utils.ph(actionString, player)));
        }
    }

}

package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.Util.ActionBar;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionActionbarMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
        if (player == null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ActionBar.sendActionBar(p, Utils.cc(Utils.ph(actionString, p)));
            }
        } else {
            ActionBar.sendActionBar(player, Utils.cc(Utils.ph(actionString, player)));
        }
    }

}

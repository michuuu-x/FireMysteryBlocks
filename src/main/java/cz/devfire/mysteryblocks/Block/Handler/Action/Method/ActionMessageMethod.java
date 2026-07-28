package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.entity.Player;

public class ActionMessageMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
        if (player != null) {
            player.sendMessage(Utils.mm(Utils.ph(actionString, player)));
        }
    }

}

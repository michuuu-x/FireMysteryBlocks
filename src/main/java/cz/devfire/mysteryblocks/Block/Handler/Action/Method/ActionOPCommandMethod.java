package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import org.bukkit.entity.Player;

public class ActionOPCommandMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
        String[] commands = actionString.split(";;");

        if (player != null) {
            player.setOp(true);

            for (String command : commands) {
                player.performCommand(command);
            }

            player.setOp(false);
        }
    }

}

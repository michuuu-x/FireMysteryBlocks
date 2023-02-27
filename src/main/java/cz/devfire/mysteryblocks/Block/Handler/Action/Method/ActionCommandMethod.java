package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import org.bukkit.entity.Player;

public class ActionCommandMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
        String[] commands = actionString.split(";;");

        if (player != null) {
            for (String command : commands) {
                player.performCommand(command);
            }
        }
    }

}

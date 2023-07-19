package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionConsoleCommandMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
        String[] commands = actionString.split(";;");

        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

}

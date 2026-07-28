package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Player.Object.MysteryPlayer;
import cz.devfire.mysteryblocks.Util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionTitleMethod implements ActionMethod {
    private final MysteryBlocksPlugin plugin;

    public ActionTitleMethod(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    public void perform(String actionString, Player player) {
        actionString = Utils.ph(actionString, player);

        if (actionString.contains("\n")) {
            String[] actionStringArgs = actionString.split("\n");
            Title title = Title.title(Utils.mm(actionStringArgs[0]), actionStringArgs.length == 1 ? Component.empty() : Utils.mm(actionStringArgs[1]));

            if (player == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.showTitle(title);
                }
            } else {
                player.showTitle(title);
            }
        } else {
            Title title = Title.title(Utils.mm(actionString), Component.empty());

            if (player == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(p.getName());

                    if (mysteryPlayer.isMessageEnabled()) {
                        p.showTitle(title);
                    }
                }
            } else {
                player.showTitle(title);
            }
        }
    }

}

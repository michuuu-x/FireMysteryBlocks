package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Player.Object.MysteryPlayer;
import cz.devfire.mysteryblocks.Util.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionActionbarMethod implements ActionMethod {
    private final MysteryBlocksPlugin plugin;

    public ActionActionbarMethod(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    public void perform(String actionString, Player player) {
        if (player == null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(p.getName());

                if (mysteryPlayer.isMessageEnabled()) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent) new TextComponent(Utils.cc(Utils.ph(actionString, p))));
                }
            }
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent) new TextComponent(Utils.cc(Utils.ph(actionString, player))));
        }
    }

}

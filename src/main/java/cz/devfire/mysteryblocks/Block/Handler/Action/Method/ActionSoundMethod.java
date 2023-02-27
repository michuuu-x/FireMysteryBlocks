package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ActionSoundMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
        String[] soundArgs = actionString.split("-");

        Sound sound = null;
        float volume = 1;
        float pitch = 1;

        boolean er = false;
        Exception exception = null;

        if (soundArgs.length >= 1) {
            try {
                sound = Sound.valueOf(soundArgs[0]);
            } catch (Exception e) {
                er = true;
                exception = e;
            }
        }

        if (soundArgs.length >= 2) {
            volume = Float.parseFloat(soundArgs[1]);
        }

        if (soundArgs.length >= 3) {
            pitch = Float.parseFloat(soundArgs[2]);
        }

        if (er || sound == null) {
            Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] &cUnknown sound: \"" + actionString + "\"");

            if (MysteryBlocksPlugin.isDebugEnabled()) {
                exception.printStackTrace();
            }
        } else {
            if (player == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), sound, volume, pitch);
                }
            } else {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

}

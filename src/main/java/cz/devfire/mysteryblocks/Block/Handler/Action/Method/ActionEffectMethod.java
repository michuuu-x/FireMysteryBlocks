package cz.devfire.mysteryblocks.Block.Handler.Action.Method;

import cz.devfire.mysteryblocks.Block.Handler.Action.Interface.ActionMethod;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ActionEffectMethod implements ActionMethod {

    public void perform(String actionString, Player player) {
        String[] effectArgs = actionString.split("-");

        PotionEffectType type = null;
        int amplifier = 0;
        int duration = 10;

        boolean er = false;
        Exception exception = null;

        if (effectArgs.length >= 1) {
            try {
                type = PotionEffectType.getByName(effectArgs[0]);
            } catch (Exception e) {
                er = true;
                exception = e;
            }
        }

        if (effectArgs.length >= 2) {
            amplifier = Integer.parseInt(effectArgs[1]);
        }

        if (effectArgs.length >= 3) {
            duration = Integer.parseInt(effectArgs[2]);
        }

        if (er || type == null) {
            Bukkit.getConsoleSender().sendMessage(Utils.mm("<dark_red>[FireMysteryBlocks-ERROR] <color:#f01f1f>Unknown effect: \"" + actionString + "\""));

            if (MysteryBlocksPlugin.isDebugEnabled()) {
                exception.printStackTrace();
            }
        } else {
            PotionEffect effect = new PotionEffect(type, amplifier, duration,true,true);

            if (player == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.addPotionEffect(effect);
                }
            } else {
                player.addPotionEffect(effect);
            }
        }
    }

}

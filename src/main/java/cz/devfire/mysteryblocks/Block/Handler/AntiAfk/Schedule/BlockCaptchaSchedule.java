package cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Schedule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method.CaptchaMethod;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class BlockCaptchaSchedule extends BukkitRunnable {
    private final MysteryBlocksPlugin plugin;

    public BlockCaptchaSchedule(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockAntiAfkHandler antiAfkHandler = mysteryBlock.getAntiAfkHandler();

            if (antiAfkHandler.isEnabled() && antiAfkHandler.getMethod() instanceof CaptchaMethod) {
                CaptchaMethod captchaMethod = (CaptchaMethod) antiAfkHandler.getMethod();

                if (captchaMethod.isTimeLimitEnabled()) {
                    HashMap<String, Long> checkingPlayers = Maps.newHashMap(captchaMethod.getCheckingPlayers());

                    for (String playerName : checkingPlayers.keySet()) {
                        long time = checkingPlayers.get(playerName);

                        if (System.currentTimeMillis() > time + captchaMethod.getTimeLimit()) {
                            Player player = Bukkit.getPlayer(playerName);

                            if (player != null) {
                                player.closeInventory();
                            }
                        }
                    }
                }
            }
        }
    }
}

package cz.devfire.mysteryblocks.Player.Listener;

import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import cz.devfire.mysteryblocks.Player.PlayerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCommandListener implements Listener {
    private final PlayerHandler playerHandler;

    public PlayerCommandListener(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        check(event.getMessage());
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        check(event.getCommand());
    }

    public void check(String command) {
        if (command.startsWith("\\")) {
            command = command.substring(1);
        }

        if ((command.equalsIgnoreCase("cmi reload") || command.equalsIgnoreCase("cmi:cmi reload")) && playerHandler.getPlugin().getHologramHandler().isEnabled()) {

            if (playerHandler.getPlugin().getHologramHandler().getHologramProviderType() == HologramProviderType.CMI) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        playerHandler.getPlugin().reload();
                    }
                }.runTaskLater(playerHandler.getPlugin(),10L);
            }
        }
    }
}

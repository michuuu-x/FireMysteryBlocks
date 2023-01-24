package cz.devfire.mysteryblocks.Listener;

import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCommandListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    public PlayerCommandListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
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

        if ((command.equalsIgnoreCase("cmi reload") || command.equalsIgnoreCase("cmi:cmi reload")) && plugin.getHologramHandler().isEnabled()) {

            if (plugin.getHologramHandler().getHologramProviderType() == HologramProviderType.CMI) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        plugin.reload();
                    }
                }.runTaskLater(plugin, 10L);
            }
        }
    }
}

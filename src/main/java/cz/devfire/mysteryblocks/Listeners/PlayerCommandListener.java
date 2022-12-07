package cz.devfire.mysteryblocks.Listeners;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCommandListener implements Listener {
    private final MysteryBlocksPluginImpl plugin;

    public PlayerCommandListener(MysteryBlocksPluginImpl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if ((event.getMessage().equalsIgnoreCase("/cmi reload") ||
                event.getMessage().equalsIgnoreCase("/cmi:cmi reload")) && plugin.getHologramHandler().isEnabled()) {

            if (plugin.getHologramHandler().getProvider() == HologramProvider.CMI) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        plugin.reload();
                    }
                }.runTaskLater(plugin, 10L);
            }
        }
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        if ((event.getCommand().equalsIgnoreCase("cmi reload") ||
                event.getCommand().equalsIgnoreCase("cmi:cmi reload")) && plugin.getHologramHandler().isEnabled()) {

            if (plugin.getHologramHandler().getProvider() == HologramProvider.CMI) {
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

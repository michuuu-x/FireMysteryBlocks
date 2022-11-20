package cz.devfire.mysteryblocks.Placeholders;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Placeholders.PlaceholderHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaceholderHandlerImpl implements PlaceholderHandler {
    private final MysteryBlocksPluginImpl plugin;

    private final BlocksPlaceholdersExpansion expansion;

    public PlaceholderHandlerImpl(MysteryBlocksPluginImpl plugin) {
        this.plugin = plugin;
        this.expansion = new BlocksPlaceholdersExpansion(plugin);
    }

    public void init() {
        Bukkit.getConsoleSender().sendMessage(" §e- Queuing placeholder registry of " + expansion.getIdentifier());

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    expansion.register();
                } catch (Exception e) {
                    if (plugin.isDebugEnabled()) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskLater(plugin, 1);
    }

    public void destroy() {
        try {
            expansion.unregister();
            Bukkit.getConsoleSender().sendMessage(" §e- Unregistering placeholder " + expansion.getIdentifier() + "§.. §aSuccessful");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(" §e- Unregistering placeholder " + expansion.getIdentifier() + "§.. §cFailed");

            if (plugin.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }
}

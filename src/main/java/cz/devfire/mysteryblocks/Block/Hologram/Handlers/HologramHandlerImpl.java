package cz.devfire.mysteryblocks.Block.Hologram.Handlers;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.HologramHandler;
import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;
import org.bukkit.Bukkit;

public class HologramHandlerImpl implements HologramHandler {
    private final MysteryBlocksPluginImpl plugin;

    private HologramProvider provider = null;
    private boolean enabled = false;

    public HologramHandlerImpl(MysteryBlocksPluginImpl plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.enabled = plugin.getConfig().getBoolean("Settings.Holograms.Enabled");

        if (enabled) {
            String providerName = plugin.getConfig().getString("Settings.Holograms.Provider");

            if (providerName.equalsIgnoreCase("NONE")) {
                Bukkit.getConsoleSender().sendMessage(" §e- Hologram provider not selected.. §cHolograms disabled.");
                enabled = false;
            } else {
                try {
                    this.provider = HologramProvider.valueOf(providerName);
                    Bukkit.getConsoleSender().sendMessage(" §e- Hologram provider selected: §6" + provider + "§e.. §aHolograms enabled.");
                } catch (Exception e) {
                    this.enabled = false;
                    Bukkit.getConsoleSender().sendMessage(" §e- Hologram provider §6" + providerName + " §enot found.. §cHolograms disabled.");
                }
            }
        }
    }

    public HologramProvider getProvider() {
        return provider;
    }

    public String getProviderName() {
        return provider.name();
    }

    public boolean isEnabled() {
        return enabled;
    }
}

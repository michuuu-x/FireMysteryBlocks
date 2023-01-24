package cz.devfire.mysteryblocks.Hologram;

import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.configuration.ConfigurationSection;

public class HologramHandler extends AbstractHandler {
    private HologramProviderType hologramProviderType;

    public HologramHandler(MysteryBlocksPlugin plugin) {
        super(plugin);
    }

    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            String providerName = section.getString("Provider");

            try {
                hologramProviderType = HologramProviderType.valueOf(providerName);

                if (hologramProviderType == HologramProviderType.NONE) {
                    Utils.log(" &e- Hologram provider not selected.. &cHolograms disabled.");
                    enabled = false;
                } else {
                    Utils.log(" &e- Hologram provider selected: &6" + hologramProviderType.name() + "&e.. &aHolograms enabled.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.log(" &e- Hologram provider &6" + providerName + " &enot found.. &cHolograms disabled.");
                enabled = false;
            }
        }

        return true;
    }

    public boolean destroy() {
        enabled = false;
        return true;
    }

    public HologramProviderType getHologramProviderType() {
        return hologramProviderType;
    }
}

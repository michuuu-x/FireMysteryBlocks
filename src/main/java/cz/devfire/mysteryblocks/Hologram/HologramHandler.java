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
                    Utils.log(" <yellow>- Hologram provider not selected.. <color:#f01f1f>Holograms disabled.");
                    enabled = false;
                } else {
                    Utils.log(" <yellow>- Hologram provider selected: <gold>" + hologramProviderType.name() + "<yellow>.. <color:#05fa11>Holograms enabled.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.log(" <yellow>- Hologram provider <gold>" + providerName + " <yellow>not found.. <color:#f01f1f>Holograms disabled.");
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

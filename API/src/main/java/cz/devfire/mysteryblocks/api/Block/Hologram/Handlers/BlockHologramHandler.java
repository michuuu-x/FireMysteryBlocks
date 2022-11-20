package cz.devfire.mysteryblocks.api.Block.Hologram.Handlers;

import cz.devfire.mysteryblocks.api.Block.Hologram.BlockHologram;
import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface BlockHologramHandler {
    void init();
    void load();

    boolean isEnabled();

    BlockHologram getHologram();
    List<String> getLines(boolean active);
    double getOffset(boolean active);
    ConfigurationSection getConfig(HologramProvider provider);
}

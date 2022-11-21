package cz.devfire.mysteryblocks.api.Block.Hologram.Handlers;

import cz.devfire.mysteryblocks.api.Block.Hologram.BlockHologram;
import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface BlockHologramHandler {
    void init();
    void load();

    boolean isEnabled();

    BlockHologram getHologram();
    List<String> getLines();
    double getOffset();
    ConfigurationSection getConfig(HologramProvider provider);
    MysteryBlocksPlugin getPlugin();
}

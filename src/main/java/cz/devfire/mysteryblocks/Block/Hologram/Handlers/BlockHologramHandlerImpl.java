package cz.devfire.mysteryblocks.Block.Hologram.Handlers;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Hologram.Providers.CMIHologramProvider;
import cz.devfire.mysteryblocks.Block.Hologram.Providers.DecentHologramsProvider;
import cz.devfire.mysteryblocks.Block.Hologram.Providers.HolographicDisplaysProvider;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Hologram.BlockHologram;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class BlockHologramHandlerImpl implements BlockHologramHandler {
    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;

    private boolean hologramEnabled;
    private BlockHologram hologram;

    private final ArrayList<String> inactiveLines = Lists.newArrayList();
    private double inactiveOffset;

    private final ArrayList<String> activeLines = Lists.newArrayList();
    private double activeOffset;

    public BlockHologramHandlerImpl(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;
    }

    public void init() {
        this.hologramEnabled = mysteryBlock.getConfig().getBoolean("Hologram.Enabled");
        this.hologram = null;

        this.inactiveLines.addAll(mysteryBlock.getConfig().getStringList("Hologram.State.Inactive.Lines"));
        this.inactiveOffset = mysteryBlock.getConfig().getDouble("Hologram.State.Inactive.Y-Offset");

        this.activeLines.addAll(mysteryBlock.getConfig().getStringList("Hologram.State.Active.Lines"));
        this.activeOffset = mysteryBlock.getConfig().getDouble("Hologram.State.Active.Y-Offset");
    }

    public void load() {
        if (hologramEnabled && plugin.getHologramHandler().isEnabled()) {
            switch (plugin.getHologramHandler().getProvider()) {
                case CMI -> this.hologram = new CMIHologramProvider(plugin, mysteryBlock, this);
                case HolographicDisplays -> this.hologram = new HolographicDisplaysProvider(plugin, mysteryBlock, this);
                case DecentHolograms -> this.hologram = new DecentHologramsProvider(plugin, mysteryBlock, this);
            }
        }
    }

    public boolean isEnabled() {
        return hologramEnabled;
    }

    public BlockHologram getHologram() {
        return hologram;
    }

    public List<String> getLines(boolean active) {
        return active ? activeLines : inactiveLines;
    }

    public double getOffset(boolean active) {
        return active ? activeOffset : inactiveOffset;
    }

    public ConfigurationSection getConfig(HologramProvider provider) {
        return mysteryBlock.getConfig().getConfigurationSection("Hologram.Providers."+ provider.name());
    }
}

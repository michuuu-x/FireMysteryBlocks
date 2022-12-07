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
import cz.devfire.mysteryblocks.api.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class BlockHologramHandlerImpl implements BlockHologramHandler {
    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;
    private final ArrayList<String> inactiveLines = Lists.newArrayList();
    private final ArrayList<String> activeLines = Lists.newArrayList();
    private boolean hologramEnabled;
    private BlockHologram hologram;
    private double inactiveOffset;
    private double activeOffset;

    public BlockHologramHandlerImpl(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;

        init();
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
                case CMI: {
                    this.hologram = new CMIHologramProvider(this, mysteryBlock);
                    break;
                }

                case HolographicDisplays: {
                    this.hologram = new HolographicDisplaysProvider(this, mysteryBlock);
                    break;
                }

                case DecentHolograms: {
                    this.hologram = new DecentHologramsProvider(this, mysteryBlock);
                    break;
                }
            }
        }
    }

    public boolean isEnabled() {
        return hologramEnabled;
    }

    public BlockHologram getHologram() {
        return hologram;
    }

    public List<String> getLines() {
        return mysteryBlock.getCooldownHandler().isUnder() ? inactiveLines : activeLines;
    }

    public double getOffset() {
        return mysteryBlock.getCooldownHandler().isUnder() ? inactiveOffset : activeOffset;
    }

    public ConfigurationSection getConfig(HologramProvider provider) {
        return mysteryBlock.getConfig().getConfigurationSection("Hologram.Providers." + provider.name());
    }

    public MysteryBlocksPlugin getPlugin() {
        return plugin;
    }
}

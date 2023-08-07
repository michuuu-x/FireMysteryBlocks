package cz.devfire.mysteryblocks.Block.Handler.Hologram;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import cz.devfire.mysteryblocks.Hologram.Interface.HologramProvider;
import cz.devfire.mysteryblocks.Hologram.Providers.AbstractHologramProvider;
import cz.devfire.mysteryblocks.Hologram.Providers.CMIHologramProvider;
import cz.devfire.mysteryblocks.Hologram.Providers.DecentHologramsProvider;
import cz.devfire.mysteryblocks.Hologram.Providers.HolographicDisplaysProvider;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BlockHologramHandler extends AbstractBlockHandler {
    private final ArrayList<String> inactiveLines = Lists.newArrayList();
    private double inactiveOffset = 0;

    private final ArrayList<String> activeLines = Lists.newArrayList();
    private double activeOffset;

    private HologramProvider hologram;

    public BlockHologramHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("Hologram"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            this.inactiveLines.addAll(section.getStringList("State.Inactive.Lines"));
            this.inactiveOffset = section.getDouble("State.Inactive.Y-Offset");

            this.activeLines.addAll(section.getStringList("State.Active.Lines"));
            this.activeOffset = section.getDouble("State.Active.Y-Offset");

            if (plugin.getHologramHandler().isEnabled()) {
                switch (plugin.getHologramHandler().getHologramProviderType()) {
                    case CMI: {
                        this.hologram = new CMIHologramProvider(this, mysteryBlock);
                        break;
                    }

                    case DecentHolograms: {
                        this.hologram = new DecentHologramsProvider(this, mysteryBlock);
                        break;
                    }

                    case HolographicDisplays: {
                        this.hologram = new HolographicDisplaysProvider(this, mysteryBlock);
                        break;
                    }

                    case NONE: {
                        this.hologram = new AbstractHologramProvider(this, mysteryBlock,"NONE") {
                            @Override
                            public void create() { /* */ }

                            @Override
                            public void update() { /* */ }

                            @Override
                            public void destroy() { /* */ }
                        };
                    }

                    default: {
                        enabled = false;
                    }
                }
            }
        }

        return true;
    }

    public List<String> getLines() {
        return mysteryBlock.getCooldownHandler().isUnder() ? inactiveLines : activeLines;
    }

    public double getOffset() {
        return mysteryBlock.getCooldownHandler().isUnder() ? inactiveOffset : activeOffset;
    }

    public ConfigurationSection getConfig(HologramProviderType provider) {
        return mysteryBlock.getConfig().getConfigurationSection("Hologram.Providers." + provider.name());
    }
}

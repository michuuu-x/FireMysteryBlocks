package cz.devfire.mysteryblocks.Util;

import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractHandler {
    protected final MysteryBlocksPlugin plugin;
    protected boolean enabled;

    public AbstractHandler(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }

    public boolean init() {
        return init(null);
    }

    public abstract boolean init(ConfigurationSection section);

    public abstract boolean destroy();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MysteryBlocksPlugin getPlugin() {
        return plugin;
    }
}

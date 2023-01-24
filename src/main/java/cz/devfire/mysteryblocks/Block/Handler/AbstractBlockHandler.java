package cz.devfire.mysteryblocks.Block.Handler;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractBlockHandler extends AbstractHandler {
    protected final MysteryBlock mysteryBlock;

    public AbstractBlockHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin);
        this.mysteryBlock = mysteryBlock;
    }

    public boolean init() {
        return init(null);
    }

    public abstract boolean init(ConfigurationSection section);

    public boolean destroy() {
        enabled = false;
        return true;
    }
}

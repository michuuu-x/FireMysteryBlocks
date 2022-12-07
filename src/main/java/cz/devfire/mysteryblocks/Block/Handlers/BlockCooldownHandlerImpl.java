package cz.devfire.mysteryblocks.Block.Handlers;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockCooldownHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.Material;

public class BlockCooldownHandlerImpl implements BlockCooldownHandler {
    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;

    private boolean enabled;
    private Material material;
    private long required;
    private long current;

    public BlockCooldownHandlerImpl(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;

        load();
    }

    @Override
    public void load() {
        enabled = mysteryBlock.getConfig().getBoolean("Cooldown.Enabled");
        material = Material.valueOf(mysteryBlock.getConfig().getString("Cooldown.Material"));
        required = mysteryBlock.getConfig().getLong("Cooldown.Time");
        current = 0;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isUnder() {
        return current + required - System.currentTimeMillis() > 0;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public long getCurrent() {
        return current;
    }

    @Override
    public void setCurrent(long current) {
        this.current = current;
    }

    @Override
    public long getTime() {
        return current + required - System.currentTimeMillis();
    }

    @Override
    public long getRequired() {
        return required;
    }
}

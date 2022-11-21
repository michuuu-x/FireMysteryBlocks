package cz.devfire.mysteryblocks.Block.AntiAfk;

import cz.devfire.mysteryblocks.Block.AntiAfk.Methods.CaptchaMethod;
import cz.devfire.mysteryblocks.Block.AntiAfk.Methods.KnockbackMethod;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.AntiAfkMethod;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.Methods.AntiAfkType;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;

public class BlockAntiAfkHandlerImpl implements BlockAntiAfkHandler {
    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;

    private boolean enabled = false;
    private double chance = 0.0;
    private AntiAfkType type = AntiAfkType.NONE;
    private AntiAfkMethod method = null;

    public BlockAntiAfkHandlerImpl(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;
    }

    @Override
    public void load() {
        this.enabled = mysteryBlock.getConfig().getBoolean("AntiAFK.Enabled");

        if (enabled) {
            try {
                this.type = AntiAfkType.valueOf(mysteryBlock.getConfig().getString("AntiAFK.Selected"));
            } catch (Exception e) {
                this.enabled = false;
            }
        }

        switch (type) {
            case KNOCKBACK -> method = new KnockbackMethod(this,plugin, mysteryBlock);
            case CAPTCHA -> method = new CaptchaMethod(this,plugin, mysteryBlock);
        }

        this.chance = mysteryBlock.getConfig().getDouble("AntiAFK.Chance");
    }

    @Override
    public AntiAfkType getType() {
        return type;
    }

    @Override
    public AntiAfkMethod getMethod() {
        return method;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public double getChance() {
        return chance;
    }
}

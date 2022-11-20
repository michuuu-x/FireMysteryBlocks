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
            case KNOCKBACK -> method = new KnockbackMethod(plugin, mysteryBlock);
            case CAPTCHA -> method = new CaptchaMethod(plugin, mysteryBlock);
        }
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
}

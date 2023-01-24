package cz.devfire.mysteryblocks.Block.AntiAfk;

import cz.devfire.mysteryblocks.Block.AntiAfk.Enum.AntiAfkType;
import cz.devfire.mysteryblocks.Block.AntiAfk.Interface.AntiAfkMethod;
import cz.devfire.mysteryblocks.Block.AntiAfk.Method.CaptchaMethod;
import cz.devfire.mysteryblocks.Block.AntiAfk.Method.KnockbackMethod;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;

public class BlockAntiAfkHandler extends AbstractBlockHandler {
    private double chance = 0D;
    private AntiAfkType type = AntiAfkType.NONE;
    private AntiAfkMethod method = null;

    public BlockAntiAfkHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("AntiAFK"));
    }

    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            chance = section.getDouble("Chance");

            try {
                type = AntiAfkType.valueOf(section.getString("Selected"));
            } catch (IllegalArgumentException e) {
                type = AntiAfkType.NONE;
                enabled = false;
            }

            switch (type) {
                case KNOCKBACK: { method = new KnockbackMethod(this, plugin, mysteryBlock); break; }
                case CAPTCHA: { method = new CaptchaMethod(this, plugin, mysteryBlock); break; }
            }
        }

        return true;
    }

    public AntiAfkMethod getMethod() {
        return method;
    }

    public AntiAfkType getType() {
        return type;
    }

    public double getChance() {
        return chance;
    }
}

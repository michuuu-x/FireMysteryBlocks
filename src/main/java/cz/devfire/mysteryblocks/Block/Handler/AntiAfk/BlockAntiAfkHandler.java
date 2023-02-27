package cz.devfire.mysteryblocks.Block.Handler.AntiAfk;

import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Enum.AntiAfkType;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Interface.AntiAfkMethod;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method.AbstractAntiAfkMethod;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method.CaptchaMethod;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method.KnockbackMethod;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
                case KNOCKBACK: {
                    method = new KnockbackMethod(this, plugin, mysteryBlock);
                    break;
                }

                case CAPTCHA: {
                    method = new CaptchaMethod(this, plugin, mysteryBlock);
                    break;
                }

                case NONE: {
                    method = new AbstractAntiAfkMethod(plugin,this, mysteryBlock) {
                        @Override
                        public void check(Player player) { /* */ }
                    };
                }
            }
        }

        return true;
    }

    @Override
    public boolean destroy() {
        method.destroy();
        return super.destroy();
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

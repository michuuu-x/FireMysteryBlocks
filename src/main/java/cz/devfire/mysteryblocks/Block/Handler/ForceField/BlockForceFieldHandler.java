package cz.devfire.mysteryblocks.Block.Handler.ForceField;

import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class BlockForceFieldHandler extends AbstractBlockHandler {
    private double radius;
    private boolean knockbackEnabled;

    public BlockForceFieldHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("ForceField"));
    }

    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            radius = section.getDouble("Radius");
            knockbackEnabled = section.getBoolean("Knockback");
        }

        return true;
    }
}

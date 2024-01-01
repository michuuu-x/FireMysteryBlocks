package cz.devfire.mysteryblocks.Block.Handler.ForceField;

import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Getter
public class BlockForceFieldHandler extends AbstractBlockHandler {
    private double radius;
    private boolean knockbackEnabled;
    private boolean protectedEnabled;

    public BlockForceFieldHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("ForceField"));
    }

    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            radius = section.getDouble("Radius");
            knockbackEnabled = section.getBoolean("Knockback");
            protectedEnabled = section.getBoolean("Protected");
        }

        return true;
    }

    public boolean isInside(Player player, MysteryBlock mysteryBlock) {
        if (mysteryBlock.getBlock() == null || mysteryBlock.getLocation() == null) return false;

        double targetX = mysteryBlock.getBlock().getLocation().getX() + 0.5;
        double targetY = mysteryBlock.getBlock().getLocation().getY();
        double targetZ = mysteryBlock.getBlock().getLocation().getZ() + 0.5;

        double playerX = player.getLocation().getX();
        double playerY = player.getLocation().getY();
        double playerZ = player.getLocation().getZ();

        double distance = Math.sqrt(Math.pow(targetX - playerX, 2) + Math.pow(targetZ - playerZ, 2));
        return distance <= radius && (playerY <= targetY + 1.5 && playerY >= targetY - 1.5);
    }
}

package cz.devfire.mysteryblocks.Block.Handler;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class BlockCooldownHandler extends AbstractBlockHandler {
    private Material material = Material.BEDROCK;
    private long requiredTime = -1;
    private long currentTime = 0;

    public BlockCooldownHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("Cooldown"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            material = Material.valueOf(section.getString("Material"));
            requiredTime = section.getLong("Time");
            currentTime = 0;
        }

        return true;
    }

    public boolean isUnder() {
        return currentTime + requiredTime - System.currentTimeMillis() > 0;
    }

    public Material getMaterial() {
        return material;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getETA() {
        return isUnder() ? (currentTime + requiredTime - System.currentTimeMillis()) : 0;
    }

    public long getRequiredTime() {
        return requiredTime;
    }
}

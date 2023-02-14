package cz.devfire.mysteryblocks.Block.Handler;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.text.SimpleDateFormat;

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

        if (true) {
            material = Material.valueOf(section.getString("Material"));
            requiredTime = section.getLong("Time");
            currentTime = 0;
        }

        return true;
    }

    public boolean isUnder() {
        if (mysteryBlock.getScheduleHandler().isEnabled()) {
            return currentTime != 0 && currentTime + (mysteryBlock.getScheduleHandler().next().getTime() - currentTime) - System.currentTimeMillis() > 0;
        }

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
        if (mysteryBlock.getScheduleHandler().isEnabled()) {
            return isUnder() ? (currentTime + (mysteryBlock.getScheduleHandler().next().getTime() - currentTime) - System.currentTimeMillis()) : 0;
        }

        return isUnder() ? (currentTime + requiredTime - System.currentTimeMillis()) : 0;
    }

    public long getRequiredTime() {
        return requiredTime;
    }
}

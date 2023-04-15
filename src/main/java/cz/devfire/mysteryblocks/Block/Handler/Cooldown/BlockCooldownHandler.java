package cz.devfire.mysteryblocks.Block.Handler.Cooldown;

import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

@Getter
@Setter
public class BlockCooldownHandler extends AbstractBlockHandler {
    private Material cooldownMaterial = Material.BEDROCK;
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
            cooldownMaterial = Material.valueOf(section.getString("Material"));
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

    public long getETA() {
        if (isUnder()) {
            if (mysteryBlock.getScheduleHandler().isEnabled()) {
                return currentTime + (mysteryBlock.getScheduleHandler().next().getTime() - currentTime) - System.currentTimeMillis();
            }

            return currentTime + requiredTime - System.currentTimeMillis();
        } else {
            return 0;
        }
    }
}

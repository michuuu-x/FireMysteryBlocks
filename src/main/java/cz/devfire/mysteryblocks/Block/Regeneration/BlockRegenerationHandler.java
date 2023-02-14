package cz.devfire.mysteryblocks.Block.Regeneration;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Block.Regeneration.Enum.RegenerationType;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BlockRegenerationHandler extends AbstractBlockHandler {
    private final ArrayList<String> actions = Lists.newArrayList();
    private RegenerationType type = null;
    private long time;
    private int lastMines = 0;
    private int lastAdd = 0;

    public BlockRegenerationHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("Regeneration"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            time = section.getLong("Time");

            try {
                type = RegenerationType.valueOf(section.getString("Method"));
            } catch (IllegalArgumentException e) {
                type = RegenerationType.ADD;
                enabled = false;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (type == RegenerationType.ADD) {
                        lastAdd = mysteryBlock.getCurrentMines();
                        lastMines = lastAdd;
                        mysteryBlock.setTempRequired(lastAdd);

                        if (mysteryBlock.getHologramHandler().isEnabled() && mysteryBlock.getHologramHandler().getHologram() != null) {
                            mysteryBlock.getHologramHandler().getHologram().update();
                        }
                    }
                }
            }.runTaskLaterAsynchronously(plugin,20);

            actions.addAll(section.getStringList("Actions"));
        }

        return true;
    }

    public void perform() {
        switch (type) {
            case ADD: {
                mysteryBlock.setTempRequired(mysteryBlock.getRequiredTempMines() + (mysteryBlock.getCurrentMines() - lastMines));
                mysteryBlock.setLastMine(0);

                lastAdd += (mysteryBlock.getCurrentMines() - lastMines);
                lastMines = mysteryBlock.getCurrentMines();

                if (mysteryBlock.getHologramHandler().isEnabled() && mysteryBlock.getHologramHandler().getHologram() != null) {
                    mysteryBlock.getHologramHandler().getHologram().update();
                }

                break;
            }

            case FULL: {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        mysteryBlock.reset(true);
                    }
                }.runTask(plugin);

                break;
            }
        }

        mysteryBlock.getMineActionHandler().perform(actions,null);
    }

    public long getTime() {
        return time;
    }

    public int getAmount() {
        return lastAdd;
    }
}
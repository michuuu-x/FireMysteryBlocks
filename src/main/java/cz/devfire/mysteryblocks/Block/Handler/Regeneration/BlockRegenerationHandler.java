package cz.devfire.mysteryblocks.Block.Handler.Regeneration;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Handler.Regeneration.Enum.RegenerationType;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

@Setter
@Getter
public class BlockRegenerationHandler extends AbstractBlockHandler {
    private long regenerationTime;
    private boolean progressiveEnabled = false;
    private long lastProcess = 0;
    private long millisPerProcess = 0;
    private double progressivePercentage = 0;
    private RegenerationType type = null;
    private final ArrayList<String> onStartActions = Lists.newArrayList();
    private final ArrayList<String> onEndActions = Lists.newArrayList();

    private int healingAmount = 0;

    public BlockRegenerationHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("Regeneration"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            regenerationTime = section.getLong("Time");
            progressiveEnabled = section.getBoolean("Progressive.Enabled");
            millisPerProcess = section.getLong("Progressive.Time");
            progressivePercentage = section.getDouble("Progressive.Percentage");

            healingAmount = (int) Math.ceil((mysteryBlock.getRequiredMines() / 100D) * progressivePercentage);
            if (healingAmount <= 0) healingAmount = 1;

            try {
                type = RegenerationType.valueOf(section.getString("Type"));
            } catch (IllegalArgumentException e) {
                type = RegenerationType.FULL;
                enabled = false;
            }

            if (type == RegenerationType.FULL) {
                progressiveEnabled = false;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (mysteryBlock.getHologramHandler().isEnabled() && mysteryBlock.getHologramHandler().getHologram() != null) {
                        mysteryBlock.getHologramHandler().getHologram().update();
                    }
                }
            }.runTaskLater(plugin,20);

            onStartActions.addAll(section.getStringList("Actions.Start"));
            onEndActions.addAll(section.getStringList("Actions.End"));
        }

        return true;
    }

    public void perform() {
        if (mysteryBlock.getLastMine() != 0) {
            mysteryBlock.setLastMine(0);
            lastProcess = 0;
        }

        if (progressiveEnabled && lastProcess == 0) {
            mysteryBlock.getMineActionHandler().perform(onStartActions,null);
        }

        switch (type) {
            case FULL: {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        mysteryBlock.reset(true);
                    }
                }.runTask(plugin);

                break;
            }

            case HEAL: {
                if (progressiveEnabled) {
                    if (healingAmount >= mysteryBlock.getCurrentMines()) {
                        lastProcess = 0;

                        mysteryBlock.setCurrentMines(0);
                    } else {
                        lastProcess = System.currentTimeMillis();

                        mysteryBlock.setCurrentMines(mysteryBlock.getCurrentMines() - healingAmount);
                    }
                } else {
                    mysteryBlock.setCurrentMines(0);
                    mysteryBlock.getMineActionHandler().perform(onEndActions,null);
                }

                break;
            }

            case ADD: {
                if (progressiveEnabled) {
                    if (mysteryBlock.getRequiredTempMines() + healingAmount >= mysteryBlock.getCurrentMines()) {
                        lastProcess = 0;

                        mysteryBlock.setRequiredTempMines(mysteryBlock.getCurrentMines());
                    } else {
                        lastProcess = System.currentTimeMillis();

                        mysteryBlock.setRequiredTempMines(mysteryBlock.getRequiredTempMines() + healingAmount);
                    }
                } else {
                    mysteryBlock.setRequiredTempMines(mysteryBlock.getCurrentMines());
                    mysteryBlock.getMineActionHandler().perform(onEndActions,null);
                }

                break;
            }
        }

        if (lastProcess == 0) {
            mysteryBlock.getMineActionHandler().perform(onEndActions,null);
        }

        if (type != RegenerationType.FULL) {
            if (mysteryBlock.getHologramHandler().isEnabled() && mysteryBlock.getHologramHandler().getHologram() != null) {
                mysteryBlock.getHologramHandler().getHologram().update();
            }
        }
    }

    public boolean isUnder() {
        return progressiveEnabled && lastProcess != 0;
    }

    public long getETA() {
        if (mysteryBlock.getLastMine() == 0) return 0;
        return regenerationTime - (System.currentTimeMillis() - mysteryBlock.getLastMine()) + 1000;
    }
}
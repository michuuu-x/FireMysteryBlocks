package cz.devfire.mysteryblocks.Block.Handler;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BlockScheduleHandler extends AbstractBlockHandler {
    private final ArrayList<Date> dates = Lists.newArrayList();

    private TimeZone timeZone = TimeZone.getDefault();
    private boolean autoDestroy = false;
    private long destroyTime = 0;
    private boolean actions = false;

    public BlockScheduleHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("Schedule"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            mysteryBlock.getCooldownHandler().setEnabled(true);

            timeZone = TimeZone.getTimeZone(section.getString("TimeZone"));

            for (String date : section.getStringList("Table")) {
                DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

                try {
                    Calendar now = Calendar.getInstance();
                    now.setTimeZone(timeZone);

                    now.set(Calendar.HOUR_OF_DAY, dateFormatter.parse(date +":00").getHours());
                    now.set(Calendar.MINUTE, dateFormatter.parse(date +":00").getMinutes());
                    now.set(Calendar.SECOND, 0);

                    if (now.get(Calendar.HOUR_OF_DAY) < Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                        now.add(Calendar.DAY_OF_MONTH,1);
                    }

                    dates.add(now.getTime());
                } catch (ParseException e) {
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] &cUnknown date: \"" + date + "\"");

                    if (plugin.isPluginEnabled()) {
                        e.printStackTrace();
                    }
                }
            }

            autoDestroy = section.getBoolean("AutoDestroy.Enabled");
            destroyTime = section.getLong("AutoDestroy.Time");
            actions = section.getBoolean("AutoDestroy.Actions");
        }

        return true;
    }

    public Date next() {
        Calendar now = Calendar.getInstance(timeZone);

        Date closest = null;
        for (Date date : dates) {
            if (date.after(now.getTime())) {
                if (closest == null || date.before(closest)) {
                    closest = date;
                }
            }
        }

        return closest == null ? new Date() : closest;
    }

    public Date prev() {
        Calendar now = Calendar.getInstance(timeZone);

        Date closest = null;
        for (Date date : dates) {
            if (date.before(now.getTime())) {
                if (closest == null || date.after(closest)) {
                    closest = date;
                }
            }
        }

        return closest == null ? new Date() : closest;
    }

    public boolean isAutoDestroy() {
        return autoDestroy;
    }

    public Long getDestroyTime() {
        return destroyTime;
    }

    public ArrayList<Date> getDates() {
        return dates;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public boolean isActions() {
        return actions;
    }
}
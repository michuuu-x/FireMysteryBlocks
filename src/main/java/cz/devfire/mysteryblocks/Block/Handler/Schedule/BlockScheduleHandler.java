package cz.devfire.mysteryblocks.Block.Handler.Schedule;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

@Getter
public class BlockScheduleHandler extends AbstractBlockHandler {
    private final ArrayList<LocalTime> times = Lists.newArrayList();
    private final ArrayList<LocalDateTime> nextDates = Lists.newArrayList();
    private final ArrayList<LocalDateTime> prevDates = Lists.newArrayList();

    private TimeZone timeZone = TimeZone.getDefault();
    private boolean autoDestroyEnabled = false;
    private long destroyTime = 0;
    private boolean actionsEnabled = false;

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
                    String[] args = date.split(":");
                    times.add(LocalTime.of(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage("§4[FireMysteryBlocks-ERROR] §cUnknown date: \"" + date + "\"");

                    if (plugin.isPluginEnabled()) {
                        e.printStackTrace();
                    }
                }
            }

            convert();

            autoDestroyEnabled = section.getBoolean("AutoDestroy.Enabled");
            destroyTime = section.getLong("AutoDestroy.Time");
            actionsEnabled = section.getBoolean("AutoDestroy.Actions");
        }

        return true;
    }

    public void convert() {
        LocalDateTime now = LocalDateTime.now();

        for (LocalTime time : times) {
            LocalDateTime prevDateTime = LocalDateTime.of(LocalDate.now(), time);
            LocalDateTime nextDateTime = LocalDateTime.of(LocalDate.now(), time);

            if (prevDateTime.isAfter(now)) {
                prevDateTime = prevDateTime.minusDays(1);
            }

            if (nextDateTime.isBefore(now)) {
                nextDateTime = nextDateTime.plusDays(1);
            }

            prevDates.add(prevDateTime);
            nextDates.add(nextDateTime);
        }

        Collections.sort(nextDates);
        Collections.sort(prevDates, Collections.reverseOrder());

//        for (LocalDateTime date : nextDates) {
//            Bukkit.getConsoleSender().sendMessage("Next date and time: " + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
//        }
    }

    public Date next() {
        for (LocalDateTime dateTime : Lists.newArrayList(nextDates)) {
            if (dateTime.isAfter(LocalDateTime.now())) {
                Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
                return Date.from(instant);
            }
        }

        return new Date();
    }

    public Date prev() {
        for (LocalDateTime dateTime : Lists.newArrayList(prevDates)) {
            if (dateTime.isBefore(LocalDateTime.now())) {
                Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
                return Date.from(instant);
            }
        }

        return new Date();
    }
}
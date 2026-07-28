package cz.devfire.mysteryblocks.Placeholders;

import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class PlaceholderHandler extends AbstractHandler {
    private PlaceholderExpansion expansion;

    @Getter private static int percentageTotal = 5;
    @Getter private static String percentageLow = "<color:#f01f1f>■";
    @Getter private static String percentageHalf = "<color:#05fa11>■";
    @Getter private static String percentageFull = "<dark_green>⬛";

    public PlaceholderHandler(MysteryBlocksPlugin plugin) {
        super(plugin);
    }

    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        percentageTotal = section.getInt("Percentage.Total");
        percentageLow = section.getString("Percentage.Tiles.Low");
        percentageHalf = section.getString("Percentage.Tiles.Half");
        percentageFull = section.getString("Percentage.Tiles.Full");

        if (enabled && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.expansion = new PlaceholderExpansion(plugin);
            Utils.log(" <yellow>- Queuing placeholder registry of " + expansion.getIdentifier());

            new BukkitRunnable() {
                @Override
                public void run() {
                    expansion.register();
                }
            }.runTaskLater(plugin, 1);
        } else if (enabled) {
            enabled = false;
            Utils.log(" <yellow>- Placeholders provider not found.. <color:#f01f1f>Placeholders disabled.");
        }

        return true;
    }

    public boolean destroy() {
        try {
            expansion.unregister();
            Bukkit.getConsoleSender().sendMessage(Utils.mm(" <yellow>- Unregistering placeholder " + expansion.getIdentifier() + ".. <color:#05fa11>Successful"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Utils.mm(" <yellow>- Unregistering placeholder " + expansion.getIdentifier() + ".. <color:#f01f1f>Failed"));
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

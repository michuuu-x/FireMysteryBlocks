package cz.devfire.mysteryblocks.Placeholders;

import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class PlaceholderHandler extends AbstractHandler {
    private final PlaceholderExpansion expansion;

    @Getter private static int percentageTotal = 5;
    @Getter private static String percentageLow = "&c■";
    @Getter private static String percentageHalf = "&a■";
    @Getter private static String percentageFull = "&2⬛";

    public PlaceholderHandler(MysteryBlocksPlugin plugin) {
        super(plugin);
        this.expansion = new PlaceholderExpansion(plugin);
    }

    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        percentageTotal = section.getInt("Percentage.Total");
        percentageLow = section.getString("Percentage.Tiles.Low");
        percentageHalf = section.getString("Percentage.Tiles.Half");
        percentageFull = section.getString("Percentage.Tiles.Full");

        if (enabled && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Utils.log(" §e- Queuing placeholder registry of " + expansion.getIdentifier());

            new BukkitRunnable() {
                @Override
                public void run() {
                    expansion.register();
                }
            }.runTaskLater(plugin, 1);
        } else if (enabled) {
            enabled = false;
            Utils.log(" &e- Placeholders provider not found.. &cPlaceholders disabled.");
        }

        return true;
    }

    public boolean destroy() {
        try {
            expansion.unregister();
            Bukkit.getConsoleSender().sendMessage(" §e- Unregistering placeholder " + expansion.getIdentifier() + ".. §aSuccessful");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(" §e- Unregistering placeholder " + expansion.getIdentifier() + ".. §cFailed");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

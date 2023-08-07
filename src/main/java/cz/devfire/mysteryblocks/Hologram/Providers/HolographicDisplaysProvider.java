package cz.devfire.mysteryblocks.Hologram.Providers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.Hologram.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class HolographicDisplaysProvider extends AbstractHologramProvider {
    private final ArrayList<TextLine> lines = Lists.newArrayList();
    private Hologram hologram;

    public HolographicDisplaysProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock) {
        super(hologramHandler, mysteryBlock,"FMB-"+ mysteryBlock.getName());
    }

    @Override
    public void create() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location location = mysteryBlock.getLocation();
                hologram = HologramsAPI.createHologram(hologramHandler.getPlugin(), location);

                ConfigurationSection settings = hologramHandler.getConfig(HologramProviderType.HolographicDisplays);
                hologram.setAllowPlaceholders(settings.getBoolean("AllowPlaceholders", false));
            }
        }.runTask(hologramHandler.getPlugin());
    }

    @Override
    public void update() {
        if (hologram == null) return;

        if (updating) return;
        updating = true;

        Location newLoc = mysteryBlock.getLocation().clone().add(0.5D, 2D + hologramHandler.getOffset(), 0.5D);
        if (!hologram.getLocation().equals(newLoc)) {
            hologram.teleport(newLoc);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!checkStatic()) {
                    List<String> hologramLines = getLines();

                    if (lines.isEmpty()) {
                        for (String line : hologramLines) {
                            lines.add(hologram.appendTextLine(Utils.cc(line)));
                        }
                    } else {
                        int i = 0;

                        while (i < hologramLines.size()) {
                            if (i < lines.size()) {
                                lines.get(i).setText(Utils.cc(hologramLines.get(i)));
                            } else {
                                lines.add(hologram.appendTextLine(Utils.cc(hologramLines.get(i))));
                            }

                            i++;
                        }

                        int to = Lists.newArrayList(lines).size();
                        for (int x = i; x < to; x++) {
                            hologram.removeLine(i);
                            lines.remove(i);
                        }
                    }
                }

                updating = false;
            }
        }.runTaskAsynchronously(hologramHandler.getPlugin());
    }

    @Override
    public void destroy() {
        if (hologram != null) {
            hologram.delete();
            hologram = null;
        }
    }
}

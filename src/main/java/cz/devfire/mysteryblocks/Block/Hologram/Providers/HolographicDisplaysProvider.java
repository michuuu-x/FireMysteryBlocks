package cz.devfire.mysteryblocks.Block.Hologram.Providers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Hologram.BlockHologram;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class HolographicDisplaysProvider extends BaseHologramProvider {
    private Location location;
    private Hologram hologram;

    private final ArrayList<TextLine> lines = Lists.newArrayList();

    public HolographicDisplaysProvider(BlockHologramHandler handler, MysteryBlock mysteryBlock) {
        super(handler, mysteryBlock,"FMB-"+ mysteryBlock.getName());
    }

    public void create() {
        location = mysteryBlock.getLocation().clone().add(0.5,2 + hologramHandler.getOffset(),0.5);
        hologram = HologramsAPI.createHologram(hologramHandler.getPlugin(), location);

        ConfigurationSection settings = hologramHandler.getConfig(HologramProvider.HolographicDisplays);
        hologram.setAllowPlaceholders(settings.getBoolean("AllowPlaceholders",false));
    }

    public void recreate() {
        destroy();
        create();
    }

    public void update() {
        List<String> hologramLines = getLines();

        location = mysteryBlock.getLocation().add(0.5,2 + hologramHandler.getOffset(),0.5);
        hologram.getLocation().setY(location.getY());

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

    public void destroy() {
        if (hologram != null) {
            hologram.delete();
            hologram = null;
        }
    }
}

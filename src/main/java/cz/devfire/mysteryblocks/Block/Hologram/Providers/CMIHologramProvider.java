package cz.devfire.mysteryblocks.Block.Hologram.Providers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Hologram.BlockHologram;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.List;

public class CMIHologramProvider implements BlockHologram {
    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;
    private final BlockHologramHandler hologramHandler;

    private final String name;

    private final CMI cmi = CMI.getInstance();
    private CMILocation location;
    private CMIHologram hologram;

    public CMIHologramProvider(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock, BlockHologramHandler hologramHandler) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;
        this.hologramHandler = hologramHandler;
        this.name = "FMB-" + mysteryBlock.getName();
    }

    public void create() {
        cmi.getHologramManager().addHologram(hologram);
        update();
    }

    public void recreate() {
        if (hologram != null) {
            hologram.remove();
        }

        ConfigurationSection settings = hologramHandler.getHologramConfiguration();

        this.location = new CMILocation(mysteryBlock.getLocation().clone().add(0.5, 2 + hologramHandler.getHologramOffset(), 0.5));
        this.hologram = new CMIHologram(name, location);

        if (settings.isConfigurationSection("CMI")) {
            this.hologram.setPermissionRequirement(settings.getBoolean("CMI.RequirePerms", false));
            this.hologram.setLOSInterval(settings.getDouble("CMI.LOSInterval", 0D));
            this.hologram.setDownOrder(settings.getBoolean("CMI.TextDownOrder", false));
            this.hologram.setSpacing(settings.getDouble("CMI.TextSpacing", 0.25D));
            this.hologram.setIconSpacing(settings.getDouble("CMI.IconSpacing", 0.5D));
            this.hologram.setShowRange(settings.getInt("CMI.ShowRange", 8));
            this.hologram.setUpdateRange(settings.getInt("CMI.UpdateRange", 8));
            this.hologram.setUpdateIntervalSec(settings.getDouble("CMI.UpdateInterval", 0D));
        }

        create();
    }

    public void update() {
        List<String> hologramLines = Lists.newArrayList();

        if (mysteryBlock.isCooldownEnabled()) {
            long time = mysteryBlock.getCooldownCurrent() + mysteryBlock.getCooldownRequired() - System.currentTimeMillis();

            LinkedHashMap<String, Integer> list = Maps.newLinkedHashMap(mysteryBlock.getMineMap());
            list = Utils.sortMapByValue(list, false);

            for (String line : time < 0 ? hologramHandler.getHologramActiveLines() : hologramHandler.getHologramInactiveLines()) {
                line = Utils.parseArgs(line,
                        mysteryBlock.getName(),
                        mysteryBlock.getCurrentMines() + "",
                        (mysteryBlock.getRequiredMines() - mysteryBlock.getCurrentMines()) + "",
                        mysteryBlock.getRequiredMines() + "",
                        time > 0 ? Utils.translateTime(time) + "" : "00:00:00",
                        time > 0 ? Utils.setTimeSecondsToString(time / 1000) + "" : "0s",
                        time > 0 ? ((int) (time / 1000)) + "" : "0");

                if (line.contains("{pos")) {
                    for (int i = 0; i < 10; i++) {
                        String pos = i + 1 > list.size() ? Language.EMPTY.getMessage() : (String) list.keySet().toArray()[i];

                        line = line.replace("{pos-" + (i + 1) + "-name}", pos);
                        line = line.replace("{pos-" + (i + 1) + "-value}", "" + list.getOrDefault(pos, 0));
                    }
                }

                hologramLines.add(line);
            }
        } else {
            LinkedHashMap<String, Integer> list = Maps.newLinkedHashMap(mysteryBlock.getMineMap());
            list = Utils.sortMapByValue(list,false);

            for (String line : hologramHandler.getHologramActiveLines()) {
                line = Utils.parseArgs(line,
                        mysteryBlock.getName(),
                        mysteryBlock.getCurrentMines() + "",
                        (mysteryBlock.getRequiredMines() - mysteryBlock.getCurrentMines()) + "",
                        mysteryBlock.getRequiredMines() + "",
                        "00:00:00",
                        "0s",
                        "0");

                for (int i = 0; i < 10; i++) {
                    String pos = i > list.size() ? Language.EMPTY.getMessage() : (String) list.keySet().toArray()[i];

                    line = line.replace("{pos-" + (i + 1) + "-name}", pos);
                    line = line.replace("{pos-" + (i + 1) + "-value}", "" + list.getOrDefault(pos, 0));
                }

                hologramLines.add(line);
            }
        }

        for (int i = hologramLines.size(); i < hologram.getLines().size(); i++) {
            hologramLines.add(null);
        }

        hologram.setLines(hologramLines);
        hologram.update();
    }

    public void destroy() {
        cmi.getHologramManager().removeHolo(hologram);
    }
}

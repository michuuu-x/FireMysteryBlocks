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
import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;

public class CMIHologramProvider extends BaseHologramProvider {
    private final CMI cmi = CMI.getInstance();
    private CMILocation location;
    private CMIHologram hologram;

    public CMIHologramProvider(BlockHologramHandler handler, MysteryBlock mysteryBlock) {
        super(handler, mysteryBlock,"FMB-" + mysteryBlock.getName());
    }

    public void create() {
        location = new CMILocation(mysteryBlock.getLocation());
        hologram = new CMIHologram(name,location);

        ConfigurationSection settings = hologramHandler.getConfig(HologramProvider.CMI);
        hologram.setPermissionRequirement(settings.getBoolean("RequirePerms",false));
        hologram.setLOSInterval(settings.getDouble("LOSInterval",0D));
        hologram.setDownOrder(settings.getBoolean("TextDownOrder",false));
        hologram.setSpacing(settings.getDouble("TextSpacing",0.25D));
        hologram.setIconSpacing(settings.getDouble("IconSpacing",0.5D));
        hologram.setShowRange(settings.getInt("ShowRange",8));
        hologram.setUpdateRange(settings.getInt("UpdateRange",8));
        hologram.setUpdateIntervalSec(settings.getDouble("UpdateInterval",0D));

        cmi.getHologramManager().addHologram(hologram);
    }

    public void update() {
        List<String> hologramLines = getLines();

        for (int i = hologramLines.size(); i < hologram.getLines().size(); i++) {
            hologramLines.add(null);
        }

        location = new CMILocation(mysteryBlock.getLocation().clone().add(0.5D,2D + hologramHandler.getOffset(),0.5D));
        hologram.setLoc(location);
        hologram.setLines(hologramLines);
        hologram.update();
    }

    public void destroy() {
        if (hologram != null) {
            cmi.getHologramManager().removeHolo(hologram);
            hologram = null;
        }
    }
}

package cz.devfire.mysteryblocks.Hologram.Providers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import cz.devfire.mysteryblocks.Block.Handler.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class CMIHologramProvider extends AbstractHologramProvider {
    private final CMI cmi = CMI.getInstance();
    private CMILocation location;
    private CMIHologram hologram;

    public CMIHologramProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock) {
        super(hologramHandler, mysteryBlock,"FMB-"+ mysteryBlock.getName());
    }

    public void create() {
        location = new CMILocation(mysteryBlock.getLocation());
        hologram = new CMIHologram(name, location);

        ConfigurationSection settings = hologramHandler.getConfig(HologramProviderType.CMI);
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

        CMILocation newLoc = new CMILocation(mysteryBlock.getLocation().clone().add(0.5D,2D + hologramHandler.getOffset(),0.5D));
        if (!hologram.getLocation().equals(newLoc)) {
            hologram.setLoc(location);
        }

        if (checkStatic()) return;

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

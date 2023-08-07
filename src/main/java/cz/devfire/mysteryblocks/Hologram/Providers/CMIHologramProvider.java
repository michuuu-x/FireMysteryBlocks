package cz.devfire.mysteryblocks.Hologram.Providers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import cz.devfire.mysteryblocks.Block.Handler.Hologram.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CMIHologramProvider extends AbstractHologramProvider {
    private final CMI cmi = CMI.getInstance();
    private CMIHologram hologram;

    public CMIHologramProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock) {
        super(hologramHandler, mysteryBlock,"FMB-"+ mysteryBlock.getName());
    }

    public void create() {
        CMILocation location = new CMILocation(mysteryBlock.getLocation());
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
        hologram.update();
    }

    public void update() {
        if (hologram == null) return;

        if (updating) return;
        updating = true;

        CMILocation newLoc = new CMILocation(mysteryBlock.getLocation().clone().add(0.5D,2D + hologramHandler.getOffset(),0.5D));
        if (!hologram.getLocation().equals(newLoc)) {
            hologram.setLoc(newLoc);
            hologram.refresh();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!checkStatic()) {
                    List<String> hologramLines = getLines();

                    for (int i = hologramLines.size(); i < hologram.getLines().size(); i++) {
                        hologramLines.add(null);
                    }

                    hologram.setLines(hologramLines);
                    hologram.update();
                }

                updating = false;
            }
        }.runTaskAsynchronously(hologramHandler.getPlugin());
    }

    public void destroy() {
        if (hologram != null) {
            cmi.getHologramManager().removeHolo(hologram);
            hologram = null;
        }
    }
}

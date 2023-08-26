package cz.devfire.mysteryblocks.Hologram.Providers;

import cz.devfire.mysteryblocks.Block.Handler.Hologram.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class DecentHologramsProvider extends AbstractHologramProvider {
    private Hologram hologram;

    public DecentHologramsProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock) {
        super(hologramHandler, mysteryBlock,"FMB-"+ mysteryBlock.getName());
    }

    public void create() {
        Location location = mysteryBlock.getLocation();
        hologram = DHAPI.createHologram(name, location);

        ConfigurationSection settings = hologramHandler.getConfig(HologramProviderType.DecentHolograms);
        hologram.setPermission(settings.getBoolean("RequirePerms") ? "decentholograms.hologram." + name : null);
        hologram.setUpdateRange(settings.getInt("UpdateRange", 48));
        hologram.setDownOrigin(settings.getBoolean("TextDownOrder", false));
        hologram.setAlwaysFacePlayer(settings.getBoolean("AlwaysFacePlayer", false));
        hologram.setDisplayRange(settings.getInt("ShowRange", 48));
        hologram.setFacing((float) settings.getDouble("Facing", 0));
        hologram.setUpdateInterval(settings.getInt("UpdateInterval", 2));
    }

    public void update() {
        if (hologram == null) return;

        if (updating) return;
        updating = true;

        Location newLoc = mysteryBlock.getLocation().clone().add(0.5D, 2D + hologramHandler.getOffset(), 0.5D);
        if (!hologram.getLocation().equals(newLoc)) {
            hologram.setLocation(newLoc);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (hologram == null) return;

                if (!checkStatic()) {
                    DHAPI.setHologramLines(hologram, getLines());
                }

                updating = false;
            }
        }.runTaskAsynchronously(hologramHandler.getPlugin());
    }

    public void destroy() {
        if (hologram != null) {
            DHAPI.removeHologram(name);
            hologram = null;
        }
    }
}

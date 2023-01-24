package cz.devfire.mysteryblocks.Hologram.Providers;

import cz.devfire.mysteryblocks.Block.Handler.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class DecentHologramsProvider extends AbstractHologramProvider {
    private Location location;
    private Hologram hologram;

    public DecentHologramsProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock) {
        super(hologramHandler, mysteryBlock,"FMB-"+ mysteryBlock.getName());
    }

    public void create() {
        location = mysteryBlock.getLocation();
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
        List<String> hologramLines = getLines();

        Location newLoc = mysteryBlock.getLocation().clone().add(0.5D, 2D + hologramHandler.getOffset(), 0.5D);
        if (!hologram.getLocation().equals(newLoc)) {
            hologram.setLocation(newLoc);
        }

        if (checkStatic()) return;

        DHAPI.setHologramLines(hologram, hologramLines);
    }

    public void destroy() {
        if (hologram != null) {
            DHAPI.removeHologram(name);
            hologram = null;
        }
    }
}

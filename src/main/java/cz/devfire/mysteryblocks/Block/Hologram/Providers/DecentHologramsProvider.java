package cz.devfire.mysteryblocks.Block.Hologram.Providers;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class DecentHologramsProvider extends BaseHologramProvider {
    private Location location;
    private Hologram hologram;

    public DecentHologramsProvider(BlockHologramHandler handler, MysteryBlock mysteryBlock) {
        super(handler, mysteryBlock,"FMB-"+ mysteryBlock.getName());
    }

    public void create() {
        location = mysteryBlock.getLocation().add(0.5,2 + hologramHandler.getOffset(), 0.5);
        hologram = DHAPI.createHologram(name, location);

        ConfigurationSection settings = hologramHandler.getConfig(HologramProvider.DecentHolograms);
        hologram.setPermission(settings.getBoolean("RequirePerms") ? "decentholograms.hologram."+ name : null);
        hologram.setUpdateRange(settings.getInt("UpdateRange",48));
        hologram.setDownOrigin(settings.getBoolean("TextDownOrder",false));
        hologram.setAlwaysFacePlayer(settings.getBoolean("AlwaysFacePlayer",false));
        hologram.setDisplayRange(settings.getInt("ShowRange",48));
        hologram.setFacing((float) settings.getDouble("Facing",0));
        hologram.setUpdateInterval(settings.getInt("UpdateInterval",2));

        hologram.updateAll();
    }

    public void recreate() {
        destroy();
        create();
    }

    public void update() {
        List<String> hologramLines = getLines();

        location = mysteryBlock.getLocation().add(0.5,2 + hologramHandler.getOffset(),0.5);
        hologram.setLocation(location);
        DHAPI.setHologramLines(hologram, hologramLines);
        // hologram.update();
    }

    public void destroy() {
        if (hologram != null) {
            DHAPI.removeHologram(name);
            hologram = null;
        }
    }
}

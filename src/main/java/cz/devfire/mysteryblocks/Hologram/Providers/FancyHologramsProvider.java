package cz.devfire.mysteryblocks.Hologram.Providers;

import cz.devfire.mysteryblocks.Block.Handler.Hologram.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Hologram.Enum.HologramProviderType;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

import java.util.Arrays;

public class FancyHologramsProvider extends AbstractHologramProvider {
    private final HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
    private Hologram hologram;

    // Rotation
    private float pitch = 0;
    private float yaw = 0;

    public FancyHologramsProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock) {
        super(hologramHandler, mysteryBlock,"FMB-"+ mysteryBlock.getName());
    }

    public void create() {
        TextHologramData hologramData = new TextHologramData(name, mysteryBlock.getLocation());
        hologramData.setPersistent(false);

        ConfigurationSection settings = hologramHandler.getConfig(HologramProviderType.FancyHolograms);

        // Rotation
        this.pitch = (float) settings.getDouble("Rotation.Pitch", 0);
        this.yaw = (float) settings.getDouble("Rotation.Yaw", 0);

        // Billboard
        hologramData.setBillboard(Display.Billboard.valueOf(settings.getString("Billboard.Type", DisplayHologramData.DEFAULT_BILLBOARD.name()).toUpperCase()));
        hologramData.setTextAlignment(TextDisplay.TextAlignment.valueOf(settings.getString("Billboard.Alignment", "CENTER").toUpperCase()));
        hologramData.setSeeThrough(settings.getBoolean("Billboard.SeeThrough", true));

        Integer[] backgroundColor = Arrays.stream(settings.getString("Billboard.BackgroundColor", "0,0,0,100").split(","))
                .map(val -> Integer.parseInt(val.trim()))
                .toArray(Integer[]::new);
        hologramData.setBackground(Color.fromARGB(backgroundColor[3], backgroundColor[0], backgroundColor[1], backgroundColor[2]));

        // Shadow
        hologramData.setTextShadow(settings.getBoolean("Shadow.Enabled", false));
        hologramData.setShadowRadius((float) settings.getDouble("Shadow.Radius", DisplayHologramData.DEFAULT_SHADOW_RADIUS));
        hologramData.setShadowStrength((float) settings.getDouble("Shadow.Strength", DisplayHologramData.DEFAULT_SHADOW_STRENGTH));

        // Update
        hologramData.setTextUpdateInterval(settings.getInt("UpdateInterval", -1));

        // Visibility
        hologramData.setVisibility(DisplayHologramData.DEFAULT_VISIBILITY);
        hologramData.setVisibilityDistance(settings.getInt("VisibilityDistance", DisplayHologramData.DEFAULT_VISIBILITY_DISTANCE));

        hologram = manager.create(hologramData);
        manager.addHologram(hologram);
    }

    public void update() {
        if (hologram == null) return;

        if (updating) return;
        updating = true;

        Location newLoc = mysteryBlock.getLocation().clone().add(0.5D,hologramHandler.getOffset(),0.5D);
        newLoc.setPitch(pitch);
        newLoc.setYaw(yaw);

        if (!hologram.getData().getLocation().equals(newLoc)) {
            hologram.getData().setLocation(newLoc);
        }

        if (!checkStatic()) {
            HologramData hologramData = hologram.getData();
            ((TextHologramData) hologramData).setText(getLines());
        }

        updating = false;
    }

    public void destroy() {
        if (hologram != null) {
            manager.removeHologram(hologram);
            hologram = null;
        }
    }
}

package cz.devfire.mysteryblocks.Block.Hologram.Providers;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

import java.util.List;

public class DecentHologramsProvider extends BaseHologramProvider {
    protected final MysteryBlocksPluginImpl plugin;
    protected final BlockHologramHandler handler;

    private Hologram hologram;

    public DecentHologramsProvider(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock, BlockHologramHandler handler) {
        super(mysteryBlock,"FMB-"+ mysteryBlock.getName());
        this.plugin = plugin;
        this.handler = handler;
    }

    public void create() {
        this.hologram = DHAPI.createHologram(name, mysteryBlock.getLocation().add(0.5, 2 + handler.getOffset(true),0.5));

        update();
    }

    @Override
    public void recreate() {
        if (hologram != null) {
            destroy();
        }

        create();
    }

    public void update() {
        List<String> hologramLines = parseLines(!mysteryBlock.isUnderCooldown());

        hologram.setLocation(mysteryBlock.getLocation().add());
        DHAPI.setHologramLines(hologram, hologramLines);
    }

    public void destroy() {
        DHAPI.removeHologram(name);
    }
}

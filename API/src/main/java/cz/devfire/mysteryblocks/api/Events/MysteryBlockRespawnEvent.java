package cz.devfire.mysteryblocks.api.Events;

import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MysteryBlockRespawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final MysteryBlock mysteryBlock;

    public MysteryBlockRespawnEvent(MysteryBlock mysteryBlock) {
        this.mysteryBlock = mysteryBlock;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public MysteryBlock getMysteryBlock() {
        return mysteryBlock;
    }
}

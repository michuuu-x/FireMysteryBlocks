package cz.devfire.mysteryblocks.Listener.Event;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MysteryBlockDestroyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final MysteryBlock mysteryBlock;

    public MysteryBlockDestroyEvent(MysteryBlock mysteryBlock) {
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

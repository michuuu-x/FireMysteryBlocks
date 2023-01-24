package cz.devfire.mysteryblocks.Listener.Event;

import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MysteryBlockMineEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final MysteryBlock mysteryBlock;
    private final Player player;

    public MysteryBlockMineEvent(MysteryBlock mysteryBlock, Player player) {
        this.mysteryBlock = mysteryBlock;
        this.player = player;
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

    public Player getPlayer() {
        return player;
    }
}

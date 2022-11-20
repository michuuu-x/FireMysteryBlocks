package cz.devfire.mysteryblocks.api.Block;

import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.Location;

import java.util.Collection;

public interface BlockHandler {
    boolean addBlock(String name);

    void removeBlock(MysteryBlock block);

    Collection<MysteryBlock> getBlocks();

    MysteryBlock getBlock(String name);

    MysteryBlock getBlockAt(Location location);
}

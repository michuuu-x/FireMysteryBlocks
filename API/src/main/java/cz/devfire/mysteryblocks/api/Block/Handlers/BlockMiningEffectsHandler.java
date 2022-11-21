package cz.devfire.mysteryblocks.api.Block.Handlers;

import java.util.HashMap;

public interface BlockMiningEffectsHandler {
    void load();

    boolean isEnabled();

    HashMap<String, Integer> getList();
}

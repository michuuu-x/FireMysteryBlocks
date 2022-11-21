package cz.devfire.mysteryblocks.api.Block.Handlers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface BlockAntiCheatHandler extends Runnable {
    void load();

    boolean isEnabled();

    long getCheckMillis();

    HashMap<Integer, List<String>> getActions();

    HashMap<Player, Set<Long>> getMineMap();

    @Override
    void run();
}

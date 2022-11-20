package cz.devfire.mysteryblocks.api.Block.AntiAfk;

import org.bukkit.entity.Player;

public interface AntiAfkMethod {

    /**
     * Performs method on player
     *
     * @param player That will be checked.
     */
    void check(Player player);

    /**
     * Tests if player can be checked
     *
     * @param player That will be checked
     * @return true if player can be checked
     */
    boolean canCheck(Player player);

}

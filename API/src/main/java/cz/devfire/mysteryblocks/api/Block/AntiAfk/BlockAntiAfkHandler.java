package cz.devfire.mysteryblocks.api.Block.AntiAfk;

import cz.devfire.mysteryblocks.api.Block.AntiAfk.Methods.AntiAfkType;

public interface BlockAntiAfkHandler {

    /**
     * Load antiAfk for block
     */
    void load();

    /**
     * Get type of current provider
     *
     * @return Enum of current provider
     */
    AntiAfkType getType();

    /**
     * Get antiafk provider
     *
     * @return Current provider if enabled
     */
    AntiAfkMethod getMethod();

    /**
     * Check if antiAfk is enabled
     *
     * @return State of antiAfk
     */
    boolean isEnabled();

    /**
     * Get chance of antiAfk
     *
     * @return Chance of antiAfk
     */
    double getChance();
}

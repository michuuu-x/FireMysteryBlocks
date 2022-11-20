package cz.devfire.mysteryblocks.api.Block.Hologram.Handlers;

import cz.devfire.mysteryblocks.api.Block.Hologram.Providers.HologramProvider;

public interface HologramHandler {

    /**
     * Get type of current provider
     *
     * @return Enum of current provider
     */
    HologramProvider getProvider();

    /**
     * Get string name of current provider
     *
     * @return String name of current provider
     */
    String getProviderName();

    /**
     * Check if holograms are enabled
     *
     * @return State of holograms
     */
    boolean isEnabled();

}

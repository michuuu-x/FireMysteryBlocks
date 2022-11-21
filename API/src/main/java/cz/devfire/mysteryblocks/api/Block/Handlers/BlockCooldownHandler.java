package cz.devfire.mysteryblocks.api.Block.Handlers;

import org.bukkit.Material;

public interface BlockCooldownHandler {
    void load();

    boolean isEnabled();

    boolean isUnder();

    Material getMaterial();

    long getCurrent();

    long getTime();

    long getRequired();

    void setCurrent(long current);
}

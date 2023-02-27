package cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Interface;

import org.bukkit.entity.Player;

public interface AntiAfkMethod {
    void init();
    void destroy();

    void check(Player player);
    boolean canCheck(Player player);
}

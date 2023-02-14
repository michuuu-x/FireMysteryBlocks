package cz.devfire.mysteryblocks.Block.AntiAfk.Interface;

import org.bukkit.entity.Player;

public interface AntiAfkMethod {
    void check(Player player);
    boolean canCheck(Player player);
}

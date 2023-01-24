package cz.devfire.mysteryblocks.Block.AntiAfk.Method;

import cz.devfire.mysteryblocks.Block.AntiAfk.Interface.AntiAfkMethod;
import cz.devfire.mysteryblocks.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.entity.Player;

import java.util.Random;

public abstract class AbstractAntiAfkMethod implements AntiAfkMethod {
    protected final MysteryBlocksPlugin plugin;
    protected final BlockAntiAfkHandler handler;
    protected final MysteryBlock mysteryBlock;

    public AbstractAntiAfkMethod(MysteryBlocksPlugin plugin, BlockAntiAfkHandler handler, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.handler = handler;
        this.mysteryBlock = mysteryBlock;
    }

    public boolean canCheck(Player player) {
        return (100 * new Random().nextDouble()) <= handler.getChance();
    }
}

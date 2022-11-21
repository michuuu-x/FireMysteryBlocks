package cz.devfire.mysteryblocks.Block.AntiAfk.Methods;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.AntiAfkMethod;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.entity.Player;

import java.util.Random;

public abstract class BaseAntiAfkMethod implements AntiAfkMethod {
    protected final MysteryBlocksPluginImpl plugin;
    protected final BlockAntiAfkHandler handler;
    protected final MysteryBlock mysteryBlock;

    public BaseAntiAfkMethod(MysteryBlocksPluginImpl plugin, BlockAntiAfkHandler handler, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.handler = handler;
        this.mysteryBlock = mysteryBlock;
    }

    public MysteryBlocksPluginImpl getPlugin() {
        return plugin;
    }

    public boolean canCheck(Player player) {
        return (100 * new Random().nextDouble()) <= handler.getChance();
    }
}

package cz.devfire.mysteryblocks.api;

import cz.devfire.mysteryblocks.api.Block.BlockHandler;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.HologramHandler;
import cz.devfire.mysteryblocks.api.Commands.CommandsHandler;
import cz.devfire.mysteryblocks.api.Placeholders.PlaceholderHandler;
import org.bukkit.plugin.Plugin;

public interface MysteryBlocksPlugin extends Plugin {
    long reload();

    void debug(String string);

    boolean isDebugEnabled();

    boolean isPluginEnabled();

    HologramHandler getHologramHandler();

    BlockHandler getBlockHandler();

    CommandsHandler getCommandsHandler();

    PlaceholderHandler getPlaceholderHandler();
}

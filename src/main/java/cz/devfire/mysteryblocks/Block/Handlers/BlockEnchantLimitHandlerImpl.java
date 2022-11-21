package cz.devfire.mysteryblocks.Block.Handlers;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.api.Block.Handlers.BlockEnchantLimitHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;

import java.util.HashMap;

public class BlockEnchantLimitHandlerImpl implements BlockEnchantLimitHandler {
    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;

    private boolean enabled;
    private final HashMap<String, Integer> list = Maps.newHashMap();

    public BlockEnchantLimitHandlerImpl(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;

        load();
    }

    @Override
    public void load() {
        this.enabled = mysteryBlock.getConfig().getBoolean("EnchantLimit.Enabled");

        for (String effect : mysteryBlock.getConfig().getStringList("EnchantLimit.List")) {
            String[] effectArgs = effect.split(":");

            this.list.put(effectArgs[0].toUpperCase(), Integer.parseInt(effectArgs[1]));
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public HashMap<String, Integer> getList() {
        return list;
    }
}

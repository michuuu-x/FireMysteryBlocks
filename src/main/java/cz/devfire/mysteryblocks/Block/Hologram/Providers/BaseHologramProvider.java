package cz.devfire.mysteryblocks.Block.Hologram.Providers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Hologram.BlockHologram;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.List;

public abstract class BaseHologramProvider implements BlockHologram {
    protected final BlockHologramHandler hologramHandler;
    protected final MysteryBlock mysteryBlock;
    protected final String name;

    private final boolean vaultEnabled;
    private final String prefixInjection;
    private final String suffixInjection;

    public BaseHologramProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock, String name) {
        this.hologramHandler = hologramHandler;
        this.mysteryBlock = mysteryBlock;
        this.name = name;

        this.vaultEnabled = mysteryBlock.getConfig().getBoolean("Hologram.NameInjection.Enabled");
        this.prefixInjection = mysteryBlock.getConfig().getString("Hologram.NameInjection.Inject.Prefix");
        this.suffixInjection = mysteryBlock.getConfig().getString("Hologram.NameInjection.Inject.Suffix");
    }

    public String getName() {
        return name;
    }

    public void recreate() {
        destroy();
        create();
        update();
    }

    public List<String> getLines() {
        LinkedHashMap<String, Integer> list = Utils.sortMapByValue(Maps.newHashMap(mysteryBlock.getMineMap()), false);
        List<String> hologramLines = Lists.newArrayList();

        for (String line : hologramHandler.getLines()) {
            line = Utils.parseBlockPlaceholders(mysteryBlock,null, line);

            if (line.contains("{pos-") && (line.contains("-name}") || line.contains("-value}"))) {
                for (int i = 0; i < 10; i++) {
                    String playerName = i + 1 > list.size() ? Language.EMPTY.getMessage() : (String) list.keySet().toArray()[i];
                    String parsedPlayerName = playerName;

                    if (vaultEnabled && Bukkit.getPluginManager().isPluginEnabled("Vault") && list.size() > i) {
                        parsedPlayerName = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(playerName),prefixInjection + playerName + suffixInjection +"§r");
                    }

                    line = line.replace("{pos-" + (i + 1) + "-name}", parsedPlayerName);
                    line = line.replace("{pos-" + (i + 1) + "-value}", "" + list.getOrDefault(playerName, 0));
                }
            }

            hologramLines.add(line);
        }

        return hologramLines;
    }
}

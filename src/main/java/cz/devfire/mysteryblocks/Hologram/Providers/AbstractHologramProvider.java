package cz.devfire.mysteryblocks.Hologram.Providers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.BlockHologramHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.Hologram.Interface.HologramProvider;
import cz.devfire.mysteryblocks.Util.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractHologramProvider implements HologramProvider {
    protected final BlockHologramHandler hologramHandler;
    protected final MysteryBlock mysteryBlock;
    protected final String name;

    private final boolean vaultEnabled;
    private final String prefixInjection;
    private final String suffixInjection;

    protected final boolean staticEnabled;
    protected int state = 0;

    public AbstractHologramProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock, String name) {
        this.hologramHandler = hologramHandler;
        this.mysteryBlock = mysteryBlock;
        this.name = name;

        this.vaultEnabled = mysteryBlock.getConfig().getBoolean("Hologram.NameInjection.Enabled");
        this.prefixInjection = mysteryBlock.getConfig().getString("Hologram.NameInjection.Inject.Prefix");
        this.suffixInjection = mysteryBlock.getConfig().getString("Hologram.NameInjection.Inject.Suffix");

        this.staticEnabled = mysteryBlock.getConfig().getBoolean("Hologram.Static.Enabled");
        this.state = 0;
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
        List<String> hologramLines = Lists.newArrayList();

        for (String line : hologramHandler.getLines()) {
            if (line.contains("{pos-") && (line.contains("-name}") || line.contains("-value}"))) {
                for (int i = 0; i < 10; i++) {
                    String playerName = i + 1 > mysteryBlock.getMineMap().size() ? Language.EMPTY.getMessage() : (String) mysteryBlock.getMineMap().keySet().toArray()[i];
                    String parsedPlayerName = playerName;

                    if (vaultEnabled && Bukkit.getPluginManager().isPluginEnabled("Vault") && mysteryBlock.getMineMap().size() > i) {
                        parsedPlayerName = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(playerName),prefixInjection + playerName + suffixInjection +"§r");
                    }

                    line = line.replace("{pos-" + (i + 1) + "-name}", parsedPlayerName);
                    line = line.replace("{pos-" + (i + 1) + "-value}", "" + mysteryBlock.getMineMap().getOrDefault(playerName, 0));
                }
            }

            line = Utils.parseBlockPlaceholders(mysteryBlock,null, line);
            hologramLines.add(line);
        }

        return hologramLines;
    }

    public boolean checkStatic() {
        if (staticEnabled) {
            if (state == 0) {
                state = mysteryBlock.getCooldownHandler().isEnabled() ? mysteryBlock.getCooldownHandler().isUnder() ? 2 : 1 : 1;
            } else {
                if (state == 2 && !mysteryBlock.getCooldownHandler().isUnder()) {
                    state = 1;
                } else if (state == 1 && mysteryBlock.getCooldownHandler().isUnder()) {
                    state = 2;
                } else {
                    return true;
                }
            }
        }

        return false;
    }
}

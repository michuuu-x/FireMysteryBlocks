package cz.devfire.mysteryblocks.Block.Hologram.Providers;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Other.Files.Language;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.Hologram.BlockHologram;
import cz.devfire.mysteryblocks.api.Block.Hologram.Handlers.BlockHologramHandler;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;

import java.util.LinkedHashMap;
import java.util.List;

public abstract class BaseHologramProvider implements BlockHologram {
    protected final BlockHologramHandler hologramHandler;
    protected final MysteryBlock mysteryBlock;
    protected final String name;

    public BaseHologramProvider(BlockHologramHandler hologramHandler, MysteryBlock mysteryBlock, String name) {
        this.hologramHandler = hologramHandler;
        this.mysteryBlock = mysteryBlock;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getLines() {
        LinkedHashMap<String, Integer> list = Utils.sortMapByValue(mysteryBlock.getMineMap(),false);
        List<String> hologramLines = Lists.newArrayList();

        long time = mysteryBlock.isCooldownEnabled() ? mysteryBlock.getCooldown() : 0;
        boolean under = time > 0;

        for (String line : getLines()) {
            line = Utils.parseArgs(line,
                    mysteryBlock.getName(),
                    mysteryBlock.getCurrentMines() + "",
                    (mysteryBlock.getRequiredMines() - mysteryBlock.getCurrentMines()) + "",
                    mysteryBlock.getRequiredMines() + "",
                    under ? Utils.translateTime(time) + "" : "00:00:00",
                    under ? Utils.setTimeSecondsToString(time / 1000) + "" : "0s",
                    under ? ((int) (time / 1000)) + "" : "0");

            if (line.contains("{pos-") && (line.contains("-name}") || line.contains("-value}"))) {
                for (int i = 0; i < 10; i++) {
                    String pos = i + 1 > list.size() ? Language.EMPTY.getMessage() : (String) list.keySet().toArray()[i];

                    line = line.replace("{pos-" + (i + 1) + "-name}", pos);
                    line = line.replace("{pos-" + (i + 1) + "-value}","" + list.getOrDefault(pos,0));
                }
            }

            hologramLines.add(line);
        }

        return hologramLines;
    }
}

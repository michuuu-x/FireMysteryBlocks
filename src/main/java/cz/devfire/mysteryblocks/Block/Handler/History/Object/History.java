package cz.devfire.mysteryblocks.Block.Handler.History.Object;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.History.BlockHistoryHandler;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.Util.Pair;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class History {
    private final Date date;
    private final LinkedList<Pair<String, Integer>> list = Lists.newLinkedList();

    public History() {
        date = null;
    }

    public History(String line) {
        String[] lineArgs = line.split("-");

        date = Date.from(Instant.ofEpochMilli(Long.parseLong(lineArgs[0])));

        for (String group : lineArgs[1].split(",")) {
            String[] groupArgs = group.split("\\|");
            list.add(new Pair<>(groupArgs[0], Integer.parseInt(groupArgs[1])));
        }
    }

    public Pair<String, Integer> getPosition(int pos) {
        return pos >= list.size() ? new Pair<>(Language.EMPTY.getMessage(), 0) : list.get(pos);
    }

    public LinkedList<Pair<String, Integer>> getList() {
        return list;
    }

    @Override
    public String toString() {
        return date == null ? "0" : date.getTime() +"-"+ list.stream().map(pair -> pair.getFirst() +"|"+ pair.getSecond()).collect(Collectors.joining(","));
    }

    public Date getDate() {
        return date;
    }

    public String getDateString(String format) {
        return date == null ? Language.EMPTY.getMessage() : new SimpleDateFormat(format).format(date);
    }
}

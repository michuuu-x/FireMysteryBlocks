package cz.devfire.mysteryblocks.Block.Handler.Visibility;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Player.Object.MysteryPlayer;
import cz.devfire.mysteryblocks.Util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockVisibilityHandler extends AbstractBlockHandler {
    private final HashMap<Player, Pair<Long, ArrayList<Player>>> playerMap = Maps.newHashMap();
    private final HashMap<Player, Long> cooldownMap = Maps.newHashMap();
    private int visibilityRadius = 5;

    public BlockVisibilityHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("Visibility"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            visibilityRadius = section.getInt("Radius");
        }

        return true;
    }

    public void check() {
        for (Player player : Lists.newArrayList(playerMap.keySet())) {
            if (player == null || !player.isOnline()) {
                playerMap.remove(player);
                continue;
            }

            Pair<Long, ArrayList<Player>> pair = playerMap.get(player);
            if (pair.getSecond().isEmpty()) {
                playerMap.remove(player);
                continue;
            }

            if (System.currentTimeMillis() - pair.getFirst() > 1000 || !player.getWorld().equals(mysteryBlock.getLocation().getWorld()) || player.getLocation().distance(mysteryBlock.getLocation()) > visibilityRadius) {
                showAll(player);
                continue;
            }

            int countBefore = pair.getSecond().size();

            for (Player p : Lists.newArrayList(pair.getSecond())) {
                if (p == null || !p.isOnline()) {
                    pair.getSecond().remove(p);
                    continue;
                }

                if (p.getWorld() != mysteryBlock.getLocation().getWorld() || (p.getWorld() == mysteryBlock.getLocation().getWorld() && p.getLocation().distance(mysteryBlock.getLocation()) > visibilityRadius)) {
                    player.showPlayer(plugin, p);
                    pair.getSecond().remove(p);
                }
            }

            if (countBefore > 0 && pair.getSecond().size() == 0) {
                MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(player.getName());

                if (mysteryPlayer.isMessageEnabled()) {
                    if (cooldownMap.getOrDefault(player, 0L) + 10000 < System.currentTimeMillis()) {
                        Language.BLOCK_VISIBILITY_STOP.send(player);
                        cooldownMap.put(player, System.currentTimeMillis());
                    }
                }
            }
        }

        for (Player player : Lists.newArrayList(cooldownMap.keySet())) {
            if (cooldownMap.get(player) + 30000 < System.currentTimeMillis()) {
                cooldownMap.remove(player);
            }
        }
    }

    public void hideAll(Player player) {
        Pair<Long, ArrayList<Player>> pair = playerMap.getOrDefault(player, new Pair<>(0L, Lists.newArrayList()));
        List<Player> playerList = Lists.newArrayList();

        int countBefore = pair.getSecond().size();

        Location location = mysteryBlock.getLocation();
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player))
                .filter(p -> p.getWorld().equals(location.getWorld()))
                .filter(p -> p.getLocation().distance(location) <= visibilityRadius)
                .filter(p -> player.canSee(p))
                .filter(p -> !playerList.contains(p))
                .forEach(playerList::add);

        playerList.forEach(p -> player.hidePlayer(plugin, p));

        pair.setFirst(System.currentTimeMillis());
        pair.getSecond().addAll(playerList);

        if (countBefore == 0 && pair.getSecond().size() > 0) {
            MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(player.getName());

            if (mysteryPlayer.isMessageEnabled()) {
                if (cooldownMap.getOrDefault(player, 0L) + 10000 < System.currentTimeMillis()) {
                    Language.BLOCK_VISIBILITY_START.send(player);
                    cooldownMap.put(player, System.currentTimeMillis());
                }
            }
        }

        playerMap.put(player, pair);
    }

    public void showAll(Player player) {
        if (playerMap.containsKey(player)) {
            MysteryPlayer mysteryPlayer = plugin.getPlayerHandler().getPlayer(player.getName());

            if (mysteryPlayer.isMessageEnabled()) {
                if (cooldownMap.getOrDefault(player, 0L) + 10000 < System.currentTimeMillis()) {
                    Language.BLOCK_VISIBILITY_STOP.send(player);
                    cooldownMap.put(player, System.currentTimeMillis());
                }
            }

            playerMap.get(player).getSecond().forEach(p -> player.showPlayer(plugin, p));
            playerMap.remove(player);
        }
    }
}
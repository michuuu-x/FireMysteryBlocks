package cz.devfire.mysteryblocks.Player;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Player.Object.MysteryPlayer;
import cz.devfire.mysteryblocks.Util.AbstractHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class PlayerHandler extends AbstractHandler {
    private final HashMap<String, MysteryPlayer> playerHashMap = Maps.newHashMap();

    public PlayerHandler(MysteryBlocksPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean init(ConfigurationSection section) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadPlayer(player.getName());
        }

        return true;
    }

    public boolean destroy() {
        playerHashMap.values().forEach(MysteryPlayer::save);
        return true;
    }

    public MysteryPlayer getPlayer(String playerName) {
        return playerHashMap.get(playerName.toLowerCase());
    }

    public void loadPlayer(String playerName) {
        MysteryPlayer player = new MysteryPlayer(this, playerName);
        player.load();

        playerHashMap.put(playerName.toLowerCase(), player);
    }

    public void savePlayer(String playerName) {
        playerHashMap.remove(playerName.toLowerCase());
    }
}

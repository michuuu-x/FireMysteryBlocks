package cz.devfire.mysteryblocks.Block.Handler.Click;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Block.Handler.AbstractBlockHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BlockClickHandler extends AbstractBlockHandler {
    private final ArrayList<String> actionList = Lists.newArrayList();

    public BlockClickHandler(MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, mysteryBlock);
        init(mysteryBlock.getConfig().getConfigurationSection("Click"));
    }

    @Override
    public boolean init(ConfigurationSection section) {
        enabled = section.getBoolean("Enabled");

        if (enabled) {
            actionList.addAll(section.getStringList("Actions"));
        }

        return true;
    }

    public void perform(Player player) {
        mysteryBlock.getMineActionHandler().perform(actionList, player.getName());
    }
}
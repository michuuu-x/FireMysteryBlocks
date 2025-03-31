package cz.devfire.mysteryblocks.Command.List;

import com.google.common.collect.Lists;
import cz.devfire.mysteryblocks.Command.Interface.ICommand;
import cz.devfire.mysteryblocks.Files.Language;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.List;

public final class BypassCommand implements ICommand {

    @Override
    public String getLabel() {
        return "bypass";
    }

    @Override
    public String getUsage() {
        return "mb bypass";
    }

    @Override
    public String getPermission() {
        return "firemysteryblocks.command.bypass";
    }

    @Override
    public String getDescription() {
        return "Prints item info for bypass string";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public void perform(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Language.PLAYER_ONLY_COMMAND.send(sender);
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            Language.NO_ITEM.send(player);
            return;
        }

        if (item.hasItemMeta()) {
            StringBuilder builder = new StringBuilder();
            assert item.getItemMeta() != null;

            if (item.getItemMeta().hasDisplayName()) {
                String displayName = item.getItemMeta().getDisplayName();
                displayName = displayName.replaceAll("§.", "");

                builder.append(displayName);
                builder.append(";;");
            } else {
                builder.append(";;");
            }

            if (item.getItemMeta().hasLore()) {
                assert item.getItemMeta().getLore() != null;

                String lore = String.join("\\n", item.getItemMeta().getLore());
                lore = lore.replaceAll("§.", "");

                builder.append(lore);
            }

            Language.BYPASS_STRING.send(player, builder.toString());
        } else {
            Language.NO_ITEMMETA_ITEM.send(player);
            return;
        }
    }

    @Override
    public List<String> tabComplete(MysteryBlocksPlugin plugin, CommandSender sender, String[] args) {
        return Lists.newArrayList();
    }
}

package cz.devfire.mysteryblocks.Block.AntiAfk.Methods;

import cz.devfire.mysteryblocks.MysteryBlocksPluginImpl;
import cz.devfire.mysteryblocks.Other.Utils;
import cz.devfire.mysteryblocks.api.Block.AntiAfk.AntiAfkMethod;
import cz.devfire.mysteryblocks.api.Block.Objects.MysteryBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.geom.Point2D;
import java.util.Random;

public class KnockbackMethod implements AntiAfkMethod {

    private final MysteryBlocksPluginImpl plugin;
    private final MysteryBlock mysteryBlock;

    private final double percentage;
    private final float power;

    public KnockbackMethod(MysteryBlocksPluginImpl plugin, MysteryBlock mysteryBlock) {
        this.plugin = plugin;
        this.mysteryBlock = mysteryBlock;

        this.percentage = mysteryBlock.getConfig().getInt("AntiAFK.Methods.Knockback.Chance", 10);
        this.power = Float.parseFloat(mysteryBlock.getConfig().getString("AntiAFK.Methods.Knockback.Power", "1"));
    }

    public void check(Player player) {
        final Entity entity = player.isInsideVehicle() ? player.getVehicle() : player;

        if (entity.getLocation().getBlock().getLocation().add(0, -1, 0).getBlock().equals(mysteryBlock.getBlock())) {
            final Point2D.Double point2D = new Point2D.Double(0, 0);

            switch (Utils.getCardinalDirection(player)) {
                case "W" -> point2D.setLocation(-1, 0);
                case "NW" -> point2D.setLocation(-0.5, 0.5);
                case "N" -> point2D.setLocation(0, -1);
                case "NE" -> point2D.setLocation(0, -0.5);
                case "E" -> point2D.setLocation(1, 0);
                case "SE" -> point2D.setLocation(0.5, 0.5);
                case "S" -> point2D.setLocation(0, 1);
                case "SW" -> point2D.setLocation(-0.5, 0);
            }

            entity.setVelocity(player.getVelocity().setY(0.475D));

            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.setVelocity(player.getLocation().getDirection().normalize().setX(point2D.getX()).setZ(point2D.getY()).multiply(player.isSneaking() ? 1 : 0.75).setY(0.4D));
                }
            }.runTaskLater(plugin, 2);
        } else {
            if (player.isSneaking()) {
                player.setVelocity(player.getVelocity().setY(0.475D));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entity.setVelocity(player.getLocation().getDirection().normalize().multiply((power * player.getLocation().getPitch() > 50 ? 1.5 : 0.65) * (-1)).setY(0.4));
                    }
                }.runTaskLater(plugin, 2);
            } else {
                entity.setVelocity(player.getLocation().getDirection().normalize().multiply((power * player.getLocation().getPitch() > 50 ? 1.5 : 1) * (-1)).setY(0.4));
            }
        }
    }

    public boolean canCheck(Player player) {
        return (100 * new Random().nextDouble()) <= percentage;
    }
}

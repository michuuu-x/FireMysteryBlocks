package cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.geom.Point2D;
import java.util.HashMap;

@Getter
@Setter
public class KnockbackMethod extends AbstractAntiAfkMethod {
    private final HashMap<String, Long> checkingPlayers = Maps.newHashMap();

    private final float power;
    private final boolean rotation;
    private final boolean move;

    public KnockbackMethod(BlockAntiAfkHandler handler, MysteryBlocksPlugin plugin, MysteryBlock mysteryBlock) {
        super(plugin, handler, mysteryBlock);

        this.power = Float.parseFloat(mysteryBlock.getConfig().getString("AntiAFK.Methods.Knockback.Power","1"));
        this.rotation = Boolean.parseBoolean(mysteryBlock.getConfig().getString("AntiAFK.Methods.Knockback.Rotation","false"));
        this.move = Boolean.parseBoolean(mysteryBlock.getConfig().getString("AntiAFK.Methods.Knockback.Moving","false"));
    }

    public void check(Player player) {
        final Entity entity = player.isInsideVehicle() ? player.getVehicle() : player;

        if (move) {
            if (checkingPlayers.containsKey(player.getName())) {
                Long time = checkingPlayers.get(player.getName());

                if (System.currentTimeMillis() < time + 2000) {
                    return;
                }
            }
        }

        if (entity.getLocation().getBlock().getLocation().subtract(0,1,0).getBlock().equals(mysteryBlock.getLocation().getBlock())) {
            final Point2D.Double point2D = new Point2D.Double(0, 0);

            switch (Utils.getCardinalDirection(player)) {
                case "W":  { point2D.setLocation(-1.0,0.0); break; }
                case "NW": { point2D.setLocation(-0.5,0.5); break; }
                case "N":  { point2D.setLocation(0.0,-1.0); break; }
                case "NE": { point2D.setLocation(0.0,-0.5); break; }
                case "E":  { point2D.setLocation(1.0, 0.0); break; }
                case "SE": { point2D.setLocation(0.5, 0.5); break; }
                case "S":  { point2D.setLocation(0.0, 1.0); break; }
                case "SW": { point2D.setLocation(-0.5,0.0); break; }
            }

            entity.setVelocity(player.getVelocity().setY(0.475D));

            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.setVelocity(player.getLocation().getDirection().normalize().setX(point2D.getX()).setZ(point2D.getY()).multiply(player.isSneaking() ? 1 : 0.75).setY(0.4D));

                    if (rotation) {
                        entity.setRotation(entity.getLocation().getYaw() + 180, entity.getLocation().getPitch());
                    }
                }
            }.runTaskLater(plugin,2);
        } else {
            if (player.isSneaking()) {
                player.setVelocity(player.getVelocity().setY(0.475D));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entity.setVelocity(player.getLocation().getDirection().normalize().multiply((power * player.getLocation().getPitch() > 50 ? 1.5 : 0.65) * (-1)).setY(0.4));

                        if (rotation) {
                            entity.setRotation(entity.getLocation().getYaw() + 180, entity.getLocation().getPitch());
                        }
                    }
                }.runTaskLater(plugin,2);
            } else {
                entity.setVelocity(player.getLocation().getDirection().normalize().multiply((power * player.getLocation().getPitch() > 50 ? 1.5 : 1) * (-1)).setY(0.4));

                if (rotation) {
                    entity.setRotation(entity.getLocation().getYaw() + 180, entity.getLocation().getPitch());
                }
            }
        }
    }
}

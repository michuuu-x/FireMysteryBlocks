package cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.BlockAntiAfkHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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

        double targetX = mysteryBlock.getLocation().getX() + 0.5;
        double targetY = mysteryBlock.getLocation().getY();
        double targetZ = mysteryBlock.getLocation().getZ() + 0.5;

        double entityX = entity.getLocation().getX();
        double entityY = entity.getLocation().getY();
        double entityZ = entity.getLocation().getZ();

        Vector vector = new Vector(targetX - entityX,0, targetZ - entityZ);

        if (targetX - entityX == 0 && targetZ - entityZ == 0) {
            switch (Utils.getCardinalDirection(player)) {
                case "N": vector.setX(0.5); break;
                case "S": vector.setX(-0.5); break;
                case "E": vector.setZ(0.5); break;
                case "W": vector.setZ(-0.5); break;
                case "NE": vector.setX(0.5); vector.setZ(0.5); break;
                case "NW": vector.setX(0.5); vector.setZ(-0.5); break;
                case "SE": vector.setX(-0.5); vector.setZ(0.5); break;
                case "SW": vector.setX(-0.5); vector.setZ(-0.5); break;
            }
        }

        // Check if player is on the block and in radius 0.75 from x or z
        if (targetY == entityY - 1 && (Math.abs(targetX - entityX) <= 0.8 || Math.abs(targetZ - entityZ) <= 0.8)) {
            entity.setVelocity(player.getVelocity().setY(0.475D));

            new BukkitRunnable() {
                @Override
                public void run() {
                    vector.normalize();
                    vector.setY(-0.4);
                    vector.multiply(player.isOnGround() ? -0.75 : -0.5);
                    entity.setVelocity(vector);
                }
            }.runTaskLater(mysteryBlock.getPlugin(),2);
        } else {
            vector.normalize();
            vector.setY(-0.4);
            vector.multiply(player.isOnGround() ? -0.75 : -0.5);
            entity.setVelocity(vector);
        }

        if (rotation) {
            entity.setRotation(entity.getLocation().getYaw() + 180, entity.getLocation().getPitch());
        }
    }
}

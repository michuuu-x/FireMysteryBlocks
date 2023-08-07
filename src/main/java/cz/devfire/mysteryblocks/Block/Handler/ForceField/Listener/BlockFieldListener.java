package cz.devfire.mysteryblocks.Block.Handler.ForceField.Listener;

import com.google.common.collect.Maps;
import cz.devfire.mysteryblocks.Block.Handler.AntiAfk.Method.KnockbackMethod;
import cz.devfire.mysteryblocks.Block.Handler.ForceField.BlockForceFieldHandler;
import cz.devfire.mysteryblocks.Block.Object.MysteryBlock;
import cz.devfire.mysteryblocks.MysteryBlocksPlugin;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BlockFieldListener implements Listener {
    private final MysteryBlocksPlugin plugin;

    private final HashMap<Entity, Long> lastCheck = Maps.newHashMap();

    public BlockFieldListener(MysteryBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = player.isInsideVehicle() ? player.getVehicle() : player;

        for (MysteryBlock mysteryBlock : plugin.getBlockHandler().getBlocks()) {
            BlockForceFieldHandler forceFieldHandler = mysteryBlock.getForceFieldHandler();

            if (forceFieldHandler.isEnabled()) {
                if (entity == null) return;
                if (mysteryBlock.getBlock() == null) return;

                double targetX = mysteryBlock.getBlock().getLocation().getX() + 0.5;
                double targetY = mysteryBlock.getBlock().getLocation().getY();
                double targetZ = mysteryBlock.getBlock().getLocation().getZ() + 0.5;

                double entityX = entity.getLocation().getX();
                double entityY = entity.getLocation().getY();
                double entityZ = entity.getLocation().getZ();

                double radius = forceFieldHandler.getRadius();
                double distance = Math.sqrt(Math.pow(targetX - entityX, 2) + Math.pow(targetZ - entityZ, 2));

                if (distance <= radius && (entityY <= targetY + 1.5 && entityY >= targetY - 1.5)) {
                    if (lastCheck.containsKey(entity) && lastCheck.get(entity) + 100 > System.currentTimeMillis()) return;

                    Vector vector = new Vector(targetX - entityX,-0.25, targetZ - entityZ);

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

                    vector.normalize();
                    vector.multiply(player.isOnGround() ? -0.75 : -0.5);

                    if (forceFieldHandler.isKnockbackEnabled()) {
                        entity.setVelocity(vector);
                    } else {
                        if (vector.getY() < 0) vector.setY(0);

                        entity.teleport(entity.getLocation().add(vector));
                    }

                    lastCheck.put(entity, System.currentTimeMillis());
                }
            }
        }
    }
}

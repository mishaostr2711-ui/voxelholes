package com.example.voxelholes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@EventBusSubscriber(modid = VoxelHoles.MODID)
public class HoleHandler {

    @SubscribeEvent
    public static void onImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!(projectile.level() instanceof ServerLevel level)) return;
        if (!(event.getRayTraceResult() instanceof BlockHitResult hit)) return;
        if (hit.getType() == HitResult.Type.MISS) return;

        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType());
        if (id.getNamespace().equals("minecraft") && !Config.INCLUDE_VANILLA.get()) return;

        // flight direction (fallback: opposite of the hit face)
        Vec3 motion = projectile.getDeltaMovement();
        Vec3 dir;
        if (motion.lengthSqr() > 1.0E-6) {
            dir = motion.normalize();
        } else {
            dir = new Vec3(-hit.getDirection().getStepX(), -hit.getDirection().getStepY(), -hit.getDirection().getStepZ());
        }

        Entity owner = projectile.getOwner();
        Vec3 start = hit.getLocation().add(dir.scale(0.1));
        int r = Config.RADIUS.get();

        // 1) crater around the hit point
        BlockPos center = BlockPos.containing(start);
        boolean rough = Config.ROUGH_EDGES.get();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    double dist2 = dx * dx + dy * dy + dz * dz;
                    double limit = r * r + 0.5;
                    if (rough && dist2 > 0.5) {
                        limit += (level.random.nextDouble() - 0.5) * r * 1.5;
                    }
                    if (dist2 > limit) continue;
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    breakBlock(level, pos, owner);
                }
            }
        }

        // 2) narrow bullet tunnel along the flight direction
        int depth = Config.PENETRATION.get();
        BlockPos last = center;
        for (int i = 1; i <= depth + r; i++) {
            BlockPos p = BlockPos.containing(start.add(dir.scale(i)));
            if (p.equals(last)) continue;
            last = p;
            BlockState state = level.getBlockState(p);
            if (state.isAir()) continue;
            if (!breakBlock(level, p, owner)) break; // hit something unbreakable: stop the tunnel
        }
    }

    /** @return true if the block was destroyed or was already air */
    private static boolean breakBlock(ServerLevel level, BlockPos pos, Entity owner) {
        if (!level.isInWorldBounds(pos)) return false;
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return true;
        if (Config.SKIP_BLOCK_ENTITIES.get() && state.hasBlockEntity()) return false;
        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0 || hardness > Config.MAX_HARDNESS.get()) return false;
        if (owner instanceof Player player && !level.mayInteract(player, pos)) return false;
        return level.destroyBlock(pos, Config.DROP_ITEMS.get(), owner);
    }
}

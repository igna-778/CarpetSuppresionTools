package igna778.suppressiontool.utils;

import com.mojang.logging.LogUtils;
import igna778.suppressiontool.mixin.ServerEntityManagerAccesor;
import igna778.suppressiontool.mixin.ServerWorldAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public class EntityUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static long getSizeUUID(ServerWorld world){
        return ((ServerEntityManagerAccesor) ((ServerWorldAccessor) world).getEntityManager()).getEntityUuids().size();
    }

    /**
     * Spawn Entities in hidden chunks
     * @return success of the function
     */
    public static boolean spawnHiddenEntities(ServerWorld world, BlockPos pos, int amount) {
        try {
            if (world.getChunkManager().isChunkLoaded(pos.getX(), pos.getZ()))
                return false;
            for (int i = 0; i < amount; i++) {
                SnowballEntity snowballEntity = new SnowballEntity(EntityType.SNOWBALL, world);
                snowballEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
                world.spawnEntity(snowballEntity);
            }
            return true;
        } catch (OutOfMemoryError e){
            LOGGER.error("Run out of memory while spawning snowballs in hidden chunks",e);
            throw e;
        }
    }
}

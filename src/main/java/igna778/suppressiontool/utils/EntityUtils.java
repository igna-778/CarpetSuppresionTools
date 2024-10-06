package igna778.suppressiontool.utils;

import igna778.suppressiontool.mixin.ServerEntityManagerAccesor;
import igna778.suppressiontool.mixin.ServerWorldAccessor;
import net.minecraft.server.world.ServerWorld;

public class EntityUtils {

    public static long getSizeUUID(ServerWorld world){
        return ((ServerEntityManagerAccesor) ((ServerWorldAccessor) world).getEntityManager()).getEntityUuids().size();
    }


}

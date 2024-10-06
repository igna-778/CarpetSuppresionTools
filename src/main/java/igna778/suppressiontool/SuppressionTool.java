package igna778.suppressiontool;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.logging.HUDController;
import carpet.logging.LoggerRegistry;
import igna778.suppressiontool.mixin.ServerEntityManagerAccesor;
import igna778.suppressiontool.mixin.ServerWorldAccessor;
import igna778.suppressiontool.utils.EntityUuidLogger;
import igna778.suppressiontool.utils.STSettings;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SuppressionTool implements ModInitializer, CarpetExtension {

    @Override
    public void onInitialize() {
        //CARPET STUFF
        CarpetServer.manageExtension(this);
        registerLoggers();

        HUDController.register(server -> {
            if (!STSettings.__EntityUUID) return;
            int total = 0;

            int ow = ((ServerEntityManagerAccesor) ((ServerWorldAccessor) server.getWorld(World.OVERWORLD)).getEntityManager()).getEntityUuids().size();
            int ne = ((ServerEntityManagerAccesor) ((ServerWorldAccessor) server.getWorld(World.NETHER)).getEntityManager()).getEntityUuids().size();
            int end = ((ServerEntityManagerAccesor) ((ServerWorldAccessor) server.getWorld(World.END)).getEntityManager()).getEntityUuids().size();
            for (ServerWorld world : server.getWorlds()) {
                total += ((ServerEntityManagerAccesor) ((ServerWorldAccessor) world).getEntityManager()).getEntityUuids().size();
            }
            int finalTotal = total;
            LoggerRegistry.getLogger("EntityUUID").log(option -> mapOptions(option, finalTotal, ow, ne, end));
        });

    }

    public void onGameStarted()
    {
        CarpetServer.settingsManager.parseSettingsClass(STSettings.class);
    }

    private static Text[] mapOptions(String selected, int total, int ow, int ne, int end) {
        List<String> builder = new ArrayList<>();
        if (selected.equals("all") || selected.equals("total")) builder.add("T: " + total);
        if (selected.equals("all") || selected.equals("overworld")) builder.add("OW: " + ow);
        if (selected.equals("all") || selected.equals("nether")) builder.add("NE: " + ne);
        if (selected.equals("all") || selected.equals("end")) builder.add("END: " + end);
        return new Text[]{Text.of(String.join("; ", builder.toArray(new String[]{})))};
    }

    @Override
    public void registerLoggers() {
        LoggerRegistry.registerLogger("EntityUUID", EntityUuidLogger.create());
    }
}

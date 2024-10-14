package igna778.suppressiontool.client;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.logging.HUDController;
import carpet.logging.LoggerRegistry;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import igna778.suppressiontool.commands.BookCommand;
import igna778.suppressiontool.commands.SnowballCommands;
import igna778.suppressiontool.mixin.ServerEntityManagerAccesor;
import igna778.suppressiontool.mixin.ServerWorldAccessor;
import igna778.suppressiontool.utils.EntityUuidLogger;
import igna778.suppressiontool.utils.MemUtils;
import igna778.suppressiontool.utils.STSettings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class SuppressionToolClient implements ClientModInitializer, CarpetExtension {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("bookcalc")
                    .then(CommandManager.argument("mem", IntegerArgumentType.integer(1))
                            .then(CommandManager.argument("base", IntegerArgumentType.integer(0))
                                    .then(CommandManager.argument("sncount", LongArgumentType.longArg(0))
                                            .executes(BookCommand::bookCalcHelp)
                                    )
                            )
                    )
            );

            dispatcher.register(CommandManager.literal("nextresize")
                    .then(CommandManager.argument("size", LongArgumentType.longArg(1))
                            .executes(SuppressionToolClient::calculateResize)
                    )
            );

            dispatcher.register(CommandManager.literal("snowballcalc")
                    .then(CommandManager.argument("mem", IntegerArgumentType.integer(1))
                            .then(CommandManager.argument("base", IntegerArgumentType.integer(0))
                                    .then(CommandManager.argument("theory", BoolArgumentType.bool())
                                        .executes(SnowballCommands::snowballCalc)
                                    )
                            )
                    )
            );

        });



        //CARPET STUFF
        CarpetServer.manageExtension(this);
        registerLoggers();

        HUDController.register(server -> {
            if (!STSettings.entityUUID) return;
            int total = 0;

            int ow = ((ServerEntityManagerAccesor) ((ServerWorldAccessor) server.getWorld(World.OVERWORLD)).getEntityManager()).getEntityUuids().size();
            int ne = ((ServerEntityManagerAccesor) ((ServerWorldAccessor) server.getWorld(World.NETHER)).getEntityManager()).getEntityUuids().size();
            int end = ((ServerEntityManagerAccesor) ((ServerWorldAccessor) server.getWorld(World.END)).getEntityManager()).getEntityUuids().size();
            for (ServerWorld world : server.getWorlds()) {
                total += ((ServerEntityManagerAccesor) ((ServerWorldAccessor) world).getEntityManager()).getEntityUuids().size();
            }
            int finalTotal = total;
            LoggerRegistry.getLogger("entityUUID").log(option -> mapOptions(option, finalTotal, ow, ne, end));
        });
    }

    private static int calculateResize(CommandContext<ServerCommandSource> context){
        ServerCommandSource source = context.getSource();
        long size = LongArgumentType.getLong(context, "size");
        long result = MemUtils.calculateResize(size);
        source.sendFeedback(() -> Text.literal("Next resize at: "+result), false);
        return 0;
    }


    //Carpet Stuff

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
    public void registerLoggers() {
        LoggerRegistry.registerLogger("entityUUID", EntityUuidLogger.create());
    }



}

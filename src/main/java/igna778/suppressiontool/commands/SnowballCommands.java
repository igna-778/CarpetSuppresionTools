package igna778.suppressiontool.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sun.jdi.connect.Connector;
import igna778.suppressiontool.utils.EntityUtils;
import igna778.suppressiontool.utils.MemUtils;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SnowballCommands {

    public static final double snowMemRate = (1.5/(1024)); //bytes taken by snowballs in MB / MB/Snowball

    public static int snowballCalc(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int mem = IntegerArgumentType.getInteger(context, "mem");
        int base = IntegerArgumentType.getInteger(context, "base");
        boolean theory = BoolArgumentType.getBool(context,"theory");
        long UUIDSize = theory ? 0 : EntityUtils.getSizeUUID(source.getWorld());
        if(mem < 512) // If less than 512 MB asume GB units
            mem = mem * 1024; // Convert to MB
        mem -= base;
        String resStr = "Te maximum amount of snowballs for the setup is: "+snowballCalc(mem,UUIDSize);
        source.sendFeedback(() -> Text.literal(resStr), false);
        return 0;

    }

    public static long snowballCalc (int mem,long UUIDSize)
    {
        long result = (long) Math.ceil(mem / snowMemRate); // Get the amount of snowballs
        int maxPow = MemUtils.powerOfTwo(result)-1;
        result = MemUtils.calculateResizePow(maxPow) - UUIDSize;
        return result;
    }
    public static int snowballHiddenSpawn(CommandContext<ServerCommandSource> context){
        ServerCommandSource source = context.getSource();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");

        if(EntityUtils.spawnHiddenEntities(source.getWorld(),pos,amount)) {
            String resStr = "Successfully spawned " + amount + " at " + pos.toString();
            source.sendFeedback(() -> Text.literal(resStr), false);
        }else {
            String resStr = "Chunk is not hidden, try to unload the chunk before executing this command again";
            source.sendFeedback(() -> Text.literal(resStr), false);
        }
        return 0;
    }
}

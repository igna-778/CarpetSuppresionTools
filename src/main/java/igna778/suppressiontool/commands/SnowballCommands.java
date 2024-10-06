package igna778.suppressiontool.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sun.jdi.connect.Connector;
import igna778.suppressiontool.utils.EntityUtils;
import igna778.suppressiontool.utils.MemUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SnowballCommands {

    private static final double snowMemRate = (1.5/(1024)); //bytes taken by snowballs in MB / MB/Snowball

    public static int snowballCalc(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int mem = IntegerArgumentType.getInteger(context, "mem");
        int base = IntegerArgumentType.getInteger(context, "base");
        boolean theory = BoolArgumentType.getBool(context,"theory");
        long UUIDSize = theory ? 0 : EntityUtils.getSizeUUID(source.getWorld());
        if(mem < 512) // If less than 512 MB asume GB units
            mem = mem * 1024; // Convert to MB
        mem -= base;
        long result = (long) Math.ceil(mem / snowMemRate); // Get the amount of snowballs
        int maxPow = MemUtils.powerOfTwo(result)-1;
        result = MemUtils.calculateResizePow(maxPow) - UUIDSize;
        String resStr = "Te maximum amount of snowballs for the setup is: "+result;
        source.sendFeedback(() -> Text.literal(resStr), false);
        return 0;
    }
}

package igna778.suppressiontool.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import igna778.suppressiontool.utils.MemUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class BookCommand {

    private static final double bookMemRate = (double) 2777 /(1024*1024); //bytes (for book trolling 1.20 tests) / MB/Book
    private static final double snowMemRate = (1.5/(1024)); //bytes taken by snowballs in MB / MB/Snowball



    /**
     * Calculates the amount of books needed for OOM suppression based on the parameters given
     * PARAM mem the amount of memory available to the server in MB or GM (float for more precision ???)
     * PARAM base the amount of memory already in use by the server
     * PARAM sncout the amount of snowballs the setup uses, NOT REQUIRED
     * */
    public static int bookCalcHelp(CommandContext<ServerCommandSource> context){
        ServerCommandSource source = context.getSource();
        int mem = IntegerArgumentType.getInteger(context, "mem");
        int base = IntegerArgumentType.getInteger(context, "base");
        long sncount = LongArgumentType.getLong(context, "sncount");
        if(mem < 512) // If less than 512 MB asume GB units
            mem = mem * 1024; // Convert to MB
        if(base < 512) // If less than 512 MB asume GB units
            base = base * 1024; // Convert to MB
        // MEM that we have to fill, base +memory base
        if(mem <= 0){
            source.sendFeedback(() -> Text.literal("Memory can't be negative!"), false);
            return 1;
        } else if (base > mem) {
            source.sendFeedback(() -> Text.literal("Memory usage can't be bigger than memory!"), false);
            return 1;
        }
        //Calculate Snowball fill and add to base
        sncount = MemUtils.calculateResize(sncount);
        base += (int) (sncount * snowMemRate);
        //Check setup is still posible
        if (base > mem) {
            source.sendFeedback(() -> Text.literal("To many snowballs for the setup to work!"), false);
            return 1;
        }
        //Calculate result
        String strRes = "The amount of books needed for the setup would be: ";
        //Do linear calculation of books needed
        int res = (int) Math.ceil((mem-base)/bookMemRate); // amount of books
        //Send feedback to player
        source.sendFeedback(() -> Text.literal(strRes+res), false);

        return 0;
    }
}

package igna778.suppressiontool.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import igna778.suppressiontool.utils.ChunkUtils;
import igna778.suppressiontool.utils.MemUtils;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

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

        long result = bookCalc(mem,base,sncount);

        if (result < 0) {
            source.sendFeedback(() -> Text.literal("To many snowballs for the setup to work!"), false);
            return 1;
        }
        String strRes = "The amount of books needed for the setup would be: "+result;
        source.sendFeedback(() -> Text.literal(strRes), false);
        return 0;
    }

    public static long bookCalc(int mem,int base,long sncount){
        //Calculate Snowball fill and add to base
        sncount = MemUtils.calculateResize(sncount);
        base += (int) (sncount * snowMemRate);
        //Check setup is still posible
        if(mem < base)
            return  -1;
        //Do linear calculation of books needed
        int res = (int) Math.ceil((mem-base)/bookMemRate); // amount of books

    }

    public static int generateBookChunk(CommandContext<ServerCommandSource> context){
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = source.getWorld();
        BlockPos pos = BlockPosArgumentType.getBlockPos(context,"pos");
        int amount = IntegerArgumentType.getInteger(context,"amount");
        if(player == null) {
            source.sendFeedback(() -> Text.literal("Command needs to be executed by a player"), true);
            return 1;
        }
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if(!stack.isOf(Items.WRITTEN_BOOK) && !stack.isOf(Items.WRITABLE_BOOK)) {
            source.sendFeedback(() -> Text.literal("Hold a Book on your main hand to execute this command"), true);
            return 1;
        }
        return ChunkUtils.generateBarrelChunk(world,stack,amount,pos) ? 0 : 1;
    }

}

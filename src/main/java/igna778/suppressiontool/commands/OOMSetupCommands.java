package igna778.suppressiontool.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import igna778.suppressiontool.utils.ChunkUtils;
import igna778.suppressiontool.utils.EntityUtils;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class OOMSetupCommands {
    private static final List<ChunkPos> bookChunkPosArr = new ArrayList<>();
    private static final List<ChunkPos> snowChunkPosArr = new ArrayList<>();

    public static int setupAddBookChunk(CommandContext<ServerCommandSource> context){
        BlockPos pos = BlockPosArgumentType.getBlockPos(context,"pos");
        if(bookChunkPosArr.contains(new ChunkPos(pos)))
           return 1;

        bookChunkPosArr.add(new ChunkPos(pos));

        return 0;
    }

    public static int setupRemoveBookChunk(CommandContext<ServerCommandSource> context){
        BlockPos pos = BlockPosArgumentType.getBlockPos(context,"pos");

        bookChunkPosArr.remove(new ChunkPos(pos));

        return 0;
    }

    public static int setupAddSnowChunk(CommandContext<ServerCommandSource> context){
        BlockPos pos = BlockPosArgumentType.getBlockPos(context,"pos");
        if(snowChunkPosArr.contains(new ChunkPos(pos)))
            return 1;

        snowChunkPosArr.add(new ChunkPos(pos));

        return 0;
    }

    public static int setupRemoveSnowChunk(CommandContext<ServerCommandSource> context){
        BlockPos pos = BlockPosArgumentType.getBlockPos(context,"pos");

        snowChunkPosArr.remove(new ChunkPos(pos));

        return 0;
    }

    public static int setupOOM(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = source.getWorld();

        int mem = IntegerArgumentType.getInteger(context, "mem");
        int base = IntegerArgumentType.getInteger(context, "base");
        int setupEntities = IntegerArgumentType.getInteger(context, "setupEntities");
        boolean theory = BoolArgumentType.getBool(context,"theory");

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

        long UUIDSize = theory ? 0 : EntityUtils.getSizeUUID(source.getWorld());

        int snowAmount = (int)SnowballCommands.snowballCalc(mem-base,UUIDSize+setupEntities);

        int bookAmount = (int)BookCommand.bookCalc(mem,base,snowAmount);

        if(bookAmount < 0){
            source.sendFeedback(() -> Text.literal("Unexpected Error occurred while calculating book amount"), true);
            return 1;
        }

        if(player == null) {
            source.sendFeedback(() -> Text.literal("Command needs to be executed by a player"), true);
            return 1;
        }
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if(!stack.isOf(Items.WRITTEN_BOOK) && !stack.isOf(Items.WRITABLE_BOOK)) {
            source.sendFeedback(() -> Text.literal("Hold a Book on your main hand to execute this command"), true);
            return 1;
        }


        if(!ChunkUtils.areChunksHidden(world,snowChunkPosArr)) {
            String resStr = "Snowball Chunk are not hidden, try to unload the chunks before executing this command again";
            source.sendFeedback(() -> Text.literal(resStr), false);
        }

        for (ChunkPos pos : bookChunkPosArr) {
            if(!ChunkUtils.generateBarrelChunk(world,stack,bookAmount, pos.getStartPos())){
                String resStr = "Failed to generate barrels full of books at : " + pos.getStartPos().toString() + "\n Clear up previous chunks before executing this command again";
                source.sendFeedback(() -> Text.literal(resStr), false);
                return 1;
            }
            //ToDo subtract amount to bookAmount and check to see if there are enough chunks
            bookAmount -= 16*16*27;
            if(bookAmount <= 0)
                break;
        }

        int snowPerChunk = snowAmount / snowChunkPosArr.size();

        if (snowPerChunk > 10000){
            String resStr = "Not Enough snowball chunks to create the setup \n Clear up chunks before executing this command again";
            source.sendFeedback(() -> Text.literal(resStr), false);
            return 1;
        }


        for (ChunkPos pos : snowChunkPosArr) {
            EntityUtils.spawnHiddenEntities(world,pos.getCenterAtY(60),snowPerChunk);
        }

        return 0;
    }

}

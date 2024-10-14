package igna778.suppressiontool.mixin;

import igna778.suppressiontool.utils.MemUtils;
import igna778.suppressiontool.utils.STSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 *  A debug tool for Out of Memory Research
 */

@Mixin(JigsawBlock.class)
public class JigsawOOMBlock extends Block {
    public JigsawOOMBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean n) {
        if (STSettings.jigsawOOM && !fromPos.equals(pos.up()) &&
                world.getBlockState(pos.up()).isOf(Blocks.GREEN_CANDLE)) {
            MemUtils.simulateOOM(0);
        }
    }
}
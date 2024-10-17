package igna778.suppressiontool.utils;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class ChunkUtils {

        /**
         * Finds the lowest free 16x3x16 volume (filled with air) in a chunk that is above ground level.
         *
         * @param world  The world where the chunk is located.
         * @param chunkPos The block position of a coordinate of the chunk.
         * @return The Y center position of the lowest free 16x3x16 volume, or -1 if no such volume is found.
         */
        private static int findLowestFreeVolume(World world, ChunkPos chunkPos) {


            // Get the world height limit and ground level (could be customized, here using y=64 as base)
            int worldMinY = 0;
            int worldMaxY = world.getTopY(); // Gets the maximum Y in the world (depends on the world type, typically 256)

            // Iterate from ground level upwards, looking for the lowest free volume
            for (int y = worldMinY; y <= worldMaxY - 3; y++) {
                if (isVolumeFilledWithAir(world, chunkPos, y)) {
                    return y+1; // Return the Y+1 level of the first air-filled 16x3x16 volume
                }
            }

            // If no air-filled volume is found, return -1
            return -1;
        }

        /**
         * Checks if the 16x3x16 volume at a given Y level in the chunk is completely filled with air.
         *
         * @param world    The world where the chunk is located.
         * @param chunkPos The chunk position.
         * @param startY   The Y level to start checking for the 16x3x16 volume.
         * @return true if the volume is completely filled with air, false otherwise.
         */
        private static boolean isVolumeFilledWithAir(World world, ChunkPos chunkPos, int startY) {
            // Loop over each block in the 16x3x16 volume
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = startY; y < startY + 3; y++) {
                        BlockPos blockPos = new BlockPos(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
                        BlockState blockState = world.getBlockState(blockPos);

                        // If any block in the volume is not air, return false
                        if (!blockState.isAir()) {
                            return false;
                        }
                    }
                }
            }

            // If all blocks are air, return true
            return true;
        }



    public static boolean generateBarrelChunk(World world, ItemStack stack, BlockPos pos){

        // Check if we are on the server side
        if (!(world instanceof ServerWorld)) {
            return false;  // Ensure this only runs on the server side
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        int freeYLevel = findLowestFreeVolume(world,chunkPos);


        for (int i = 0; i < 16*16; i++) {
            ItemUtils.placeFullBarrelWithItem(world, chunkPos.getStartPos().add(i%16,freeYLevel,(int)i/16), stack);
        }

        return true;
    }
}

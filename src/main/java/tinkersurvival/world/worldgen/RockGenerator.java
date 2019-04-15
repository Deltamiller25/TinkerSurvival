package tinkersurvival.world.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import tinkersurvival.config.Config;
import tinkersurvival.world.block.BlockRock;
import tinkersurvival.world.TinkerSurvivalWorld;

import static tinkersurvival.world.block.BlockRock.EnumMineralType;
import static tinkersurvival.world.block.BlockRock.EnumMineralType.*;
import static tinkersurvival.world.block.BlockRock.TYPE;

public class RockGenerator {

    @SubscribeEvent
    public void decorateBiome(DecorateBiomeEvent.Post event) {
        if(!Config.Balance.ENABLE_ROCKGEN){
            return;
        }

        World world = event.getWorld();
        Random random = event.getRand();
        int chunkX = event.getPos().getX() >> 4;
        int chunkZ = event.getPos().getZ() >> 4;
        // Generate Surface Loose Rocks
        if (world.provider.getDimension() == 0) {
            for (int i = 0; i < 3; i++) {
                int xCoord = chunkX * 16 + random.nextInt(16) + 8;
                int zCoord = chunkZ * 16 + random.nextInt(16) + 8;
                if (world.getBiome(new BlockPos(xCoord, 1, zCoord)) != Biomes.OCEAN
                        && world.getBiome(new BlockPos(xCoord, 1, zCoord)) != Biomes.DEEP_OCEAN
                        && world.getBiome(new BlockPos(xCoord, 1, zCoord)) != Biomes.FROZEN_OCEAN) {
                    generateRocks(
                        world,
                        random,
                        xCoord,
                        world.getTopSolidOrLiquidBlock(new BlockPos(xCoord, 1, zCoord)).getY() - 1,
                        zCoord
                    );
                }
            }
        }
    }

    private boolean generateRocks(World world, Random random, int i, int j, int k) {
        Block upBl = world.getBlockState(new BlockPos(i, j + 1, k)).getBlock();
        Block atBl = world.getBlockState(new BlockPos(i, j, k)).getBlock();
        Block downBl = world.getBlockState(new BlockPos(i, j - 5, k)).getBlock();

        Material atMat = atBl.getMaterial(atBl.getBlockState().getBaseState());

        if (random.nextDouble() < Config.Balance.ROCKGEN_CHANCE
                && (world.isAirBlock(new BlockPos(i, j + 1, k))
                    || upBl== Blocks.SNOW_LAYER
                    || upBl == Blocks.TALLGRASS
                    || upBl == Blocks.SNOW)
                && (atMat == Material.GRASS
                    || atMat == Material.ROCK
                    || atMat == Material.SAND)
                && atBl.isOpaqueCube(atBl.getDefaultState())) {

            BlockRock.EnumMineralType type;
            if (downBl == Blocks.STONE) {
                switch (downBl.getMetaFromState(world.getBlockState(new BlockPos(i, j - 5, k)))) {
                    case 1:
                        type = GRANITE;
                        break;
                    case 3:
                        type = DIORITE;
                        break;
                    case 5:
                        type = ANDESITE;
                        break;
                    default:
                        type = STONE;
                }
            } else {
                type = STONE;
            }
            world.setBlockState(
                new BlockPos(i, j + 1, k),
                TinkerSurvivalWorld.looseRock.getDefaultState().withProperty(TYPE, type)
            );
        }
        return true;
    }

}

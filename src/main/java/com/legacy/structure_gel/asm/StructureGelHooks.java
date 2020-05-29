package com.legacy.structure_gel.asm;

import com.legacy.structure_gel.access_helpers.StructureAccessHelper;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;

public class StructureGelHooks
{
	/**
	 * Hooks into
	 * {@link LakesFeature#place(IWorld, net.minecraft.world.gen.ChunkGenerator, java.util.Random, net.minecraft.util.math.BlockPos, net.minecraft.world.gen.feature.BlockStateFeatureConfig)}
	 * to allow for more structures than just villages to prevent lakes from
	 * generating.
	 * 
	 * @see StructureGelHooks#addLakeProofStructure(Structure)
	 * @param worldIn
	 * @param chunkPos
	 * @return
	 */
	public static boolean lakeCheckForStructures(IWorld worldIn, ChunkPos chunkPos)
	{
		for (Structure<?> structure : StructureAccessHelper.LAKE_STRUCTURES)
			if (!worldIn.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(structure.getStructureName()).isEmpty())
				return false;

		return true;
	}
}

package com.legacy.structure_gel.asm;

import com.legacy.structure_gel.access_helpers.StructureAccessHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;

public class StructureGelHooks
{
	/**
	 * Hooks into
	 * {@link LakesFeature#func_230362_a_(net.minecraft.world.ISeedReader, StructureManager, net.minecraft.world.gen.ChunkGenerator, java.util.Random, BlockPos, net.minecraft.world.gen.feature.BlockStateFeatureConfig)}
	 * to allow for more structures than just villages to prevent lakes from
	 * generating.
	 * 
	 * @see StructureAccessHelper#addLakeProofStructure(Structure)
	 * @param structureManager
	 * @param pos
	 * @return
	 */
	public static boolean lakeCheckForStructures(StructureManager structureManager, BlockPos pos)
	{
		for (Structure<?> structure : StructureAccessHelper.LAKE_STRUCTURES)
			if (structureManager.func_235011_a_(SectionPos.from(pos), structure).findAny().isPresent())
				return true;
		return false;
	}
}

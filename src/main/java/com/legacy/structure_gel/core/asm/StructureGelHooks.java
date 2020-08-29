package com.legacy.structure_gel.core.asm;

import com.legacy.structure_gel.access_helpers.StructureAccessHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;

public class StructureGelHooks
{
	/**
	 * Hooks into
	 * {@link LakesFeature#func_241855_a(ISeedReader, net.minecraft.world.gen.ChunkGenerator, java.util.Random, BlockPos, net.minecraft.world.gen.feature.BlockStateFeatureConfig)}
	 * to allow for more structures than just villages to prevent lakes from
	 * generating.
	 * 
	 * @see StructureAccessHelper#addLakeProofStructure(Structure)
	 * @param seedReader
	 * @param pos
	 * @return
	 */
	public static boolean lakeCheckForStructures(ISeedReader seedReader, BlockPos pos)
	{
		for (Structure<?> structure : StructureAccessHelper.LAKE_STRUCTURES)
			if (seedReader.func_241827_a(SectionPos.from(pos), structure).findAny().isPresent())
				return true;
		return false;
	}
}

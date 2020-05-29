package com.legacy.structure_gel.asm;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.StructureGelConfig;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;

public class StructureGelHooks
{
	public static List<Structure<?>> LAKE_STRUCTURES = new ArrayList<>();
	static
	{
		StructureGelHooks.LAKE_STRUCTURES.add(Feature.VILLAGE);
		if (StructureGelConfig.COMMON.getExtraLakeProofing())
		{
			StructureGelHooks.LAKE_STRUCTURES.addAll(ImmutableList.of(Feature.PILLAGER_OUTPOST, Feature.JUNGLE_TEMPLE, Feature.DESERT_PYRAMID, Feature.IGLOO, Feature.SWAMP_HUT, Feature.WOODLAND_MANSION));
		}
	}

	/**
	 * Adds the structure to the list of structures that lakes cannot generate in.
	 * 
	 * @see StructureGelHooks#lakeCheckForStructures(IWorld, ChunkPos)
	 * @param structure
	 */
	public static void addLakeProofStructure(Structure<?> structure)
	{
		StructureGelHooks.LAKE_STRUCTURES.add(structure);
	}

	/**
	 * Removes the structure from the list of structures that lakes cannot generate
	 * in.
	 * 
	 * @see StructureGelHooks#lakeCheckForStructures(IWorld, ChunkPos)
	 * @param structure
	 */
	public static void removeLakeProofStructure(Structure<?> structure)
	{
		StructureGelHooks.LAKE_STRUCTURES.remove(structure);
	}

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
		for (Structure<?> structure : LAKE_STRUCTURES)
			if (!worldIn.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(structure.getStructureName()).isEmpty())
				return false;

		return true;
	}
}

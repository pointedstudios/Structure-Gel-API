package com.legacy.structure_gel.access_helpers;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.StructureGelConfig;
import com.legacy.structure_gel.asm.StructureGelHooks;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.Structure;

public class StructureAccessHelper
{
	/**
	 * A list of structures that lakes are not allowed to generate in.
	 */
	public static List<Structure<?>> LAKE_STRUCTURES = new ArrayList<>();
	static
	{
		StructureAccessHelper.LAKE_STRUCTURES.add(Structure.field_236381_q_);
		if (StructureGelConfig.COMMON.getExtraLakeProofing())
		{
			StructureAccessHelper.LAKE_STRUCTURES.addAll(ImmutableList.of(Structure.field_236368_d_, Structure.field_236369_e_, Structure.field_236370_f_, Structure.field_236371_g_, Structure.field_236375_k_));
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
		StructureAccessHelper.LAKE_STRUCTURES.add(structure);
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
		StructureAccessHelper.LAKE_STRUCTURES.remove(structure);
	}
}

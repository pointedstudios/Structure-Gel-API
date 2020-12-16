package com.legacy.structure_gel.access_helpers;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.StructureGelConfig;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods to interact with structure fields. At the moment, it only
 * works for setting up structures that lakes are blocked from generating in.
 *
 * @author David
 */
public class StructureAccessHelper
{
	/**
	 * A list of structures that lakes are not allowed to generate in.
	 */
	public static List<Structure<?>> LAKE_STRUCTURES = new ArrayList<>();

	static
	{
		StructureAccessHelper.LAKE_STRUCTURES.add(Structure.VILLAGE);
		if (StructureGelConfig.COMMON.getExtraLakeProofing())
		{
			StructureAccessHelper.LAKE_STRUCTURES.addAll(ImmutableList.of(Structure.WOODLAND_MANSION, Structure.JUNGLE_PYRAMID, Structure.DESERT_PYRAMID, Structure.IGLOO, Structure.STRONGHOLD));
		}
	}

	/**
	 * Adds the structure to the list of structures that lakes cannot generate in.
	 *
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
	 * @param structure
	 */
	public static void removeLakeProofStructure(Structure<?> structure)
	{
		StructureAccessHelper.LAKE_STRUCTURES.remove(structure);
	}
}

package com.legacy.structure_gel.access_helpers;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;

/**
 * Gives access to various methods to create {@link DimensionSettings}.
 * 
 * @author David
 *
 */
public class DimensionAccessHelper
{
	public static DimensionSettings newDimensionSettings(DimensionStructuresSettings structures, NoiseSettings noise, BlockState defaultBlock, BlockState defaultFluid, int bedrockRoofPos, int bedrockFloorPos, int seaLevel, boolean disableMobGeneration)
	{
		return new DimensionSettings(structures, noise, defaultBlock, defaultFluid, bedrockRoofPos, bedrockFloorPos, seaLevel, disableMobGeneration);
	}

	public static DimensionSettings newFloatingIslandSettings(DimensionStructuresSettings structures, BlockState defaultBlock, BlockState defaultFluid, ResourceLocation name, boolean disableMobGeneration, boolean islandNoiseOverride)
	{
		return DimensionSettings.func_242742_a(structures, defaultBlock, defaultFluid, name, disableMobGeneration, islandNoiseOverride);
	}

	public static DimensionSettings newCavesSettings(DimensionStructuresSettings structures, BlockState defaultBlock, BlockState defaultFluid, ResourceLocation name)
	{
		return DimensionSettings.func_242741_a(structures, defaultBlock, defaultFluid, name);
	}

	public static DimensionSettings newAmplifiedSettings(DimensionStructuresSettings structures, boolean isAmplified, ResourceLocation name)
	{
		return DimensionSettings.func_242743_a(structures, isAmplified, name);
	}
}

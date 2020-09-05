package com.legacy.structure_gel.access_helpers;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.legacy.structure_gel.worldgen.structure.GelStructure;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

/**
 * Gives access to various methods to create {@link DimensionSettings}.
 * 
 * @author David
 *
 */
public class DimensionAccessHelper
{
	/**
	 * Used to create fully custom noise settings, allowing for everything to be
	 * customized.
	 * 
	 * @param structures
	 * @param noise
	 * @param defaultBlock
	 * @param defaultFluid
	 * @param bedrockRoofPos
	 * @param bedrockFloorPos
	 * @param seaLevel
	 * @param disableMobGeneration
	 * @return {@link DimensionSettings}
	 */
	public static DimensionSettings newDimensionSettings(DimensionStructuresSettings structures, NoiseSettings noise, BlockState defaultBlock, BlockState defaultFluid, int bedrockRoofPos, int bedrockFloorPos, int seaLevel, boolean disableMobGeneration)
	{
		return new DimensionSettings(structures, noise, defaultBlock, defaultFluid, bedrockRoofPos, bedrockFloorPos, seaLevel, disableMobGeneration);
	}

	/**
	 * Used to create noise settings akin to the Floating Islands buffet option.
	 * This is also used by the End.
	 * 
	 * @param structures
	 * @param defaultBlock
	 * @param defaultFluid
	 * @param name
	 * @param disableMobGeneration
	 * @param islandNoiseOverride
	 * @return {@link DimensionSettings}
	 */
	public static DimensionSettings newFloatingIslandSettings(DimensionStructuresSettings structures, BlockState defaultBlock, BlockState defaultFluid, ResourceLocation name, boolean disableMobGeneration, boolean islandNoiseOverride)
	{
		return DimensionSettings.func_242742_a(structures, defaultBlock, defaultFluid, name, disableMobGeneration, islandNoiseOverride);
	}

	/**
	 * Used to create noise settings akin to the default Nether's, or the Overworld
	 * Caves buffet option.
	 * 
	 * @param structures
	 * @param defaultBlock
	 * @param defaultFluid
	 * @param name
	 * @return {@link DimensionSettings}
	 */
	public static DimensionSettings newCavesSettings(DimensionStructuresSettings structures, BlockState defaultBlock, BlockState defaultFluid, ResourceLocation name)
	{
		return DimensionSettings.func_242741_a(structures, defaultBlock, defaultFluid, name);
	}

	/**
	 * Used to create noise settings akin to the default Overworld's.
	 * 
	 * @param structures
	 * @param isAmplified
	 * @param name
	 * @return {@link DimensionSettings}
	 */
	public static DimensionSettings newSurfaceSettings(DimensionStructuresSettings structures, boolean isAmplified, ResourceLocation name)
	{
		return DimensionSettings.func_242743_a(structures, isAmplified, name);
	}

	/**
	 * Used to create noise settings akin to the default Overworld's, this one
	 * allows you to simply change the filler, and fluid blocks.
	 * 
	 * @param structureSettingsIn
	 * @param isAmplified
	 * @param defaultBlock
	 * @param defaultFluid
	 * @param name
	 * @return {@link DimensionSettings}
	 */
	public static DimensionSettings newCustomSurfaceSettings(DimensionStructuresSettings structureSettingsIn, boolean isAmplified, BlockState defaultBlock, BlockState defaultFluid, ResourceLocation name)
	{
		return new DimensionSettings(structureSettingsIn, new NoiseSettings(256, new ScalingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D), new SlideSettings(-10, 3, 0), new SlideSettings(-30, 0, 0), 1, 2, 1.0D, -0.46875D, true, true, false, isAmplified), defaultBlock, defaultFluid, -10, 0, 63, false);
	}

	/**
	 * Creates a map {@link Structure}s and {@link StructureSeparationSettings} for
	 * use in {@link DimensionSettings}. Requires use of {@link GelStructure}.
	 * 
	 * @param structures
	 * @return {@link Map}
	 */
	public static Map<Structure<?>, StructureSeparationSettings> separationSettingsMap(GelStructure<?>... structures)
	{
		return Arrays.asList(structures).stream().collect(Collectors.toMap((s) -> s, GelStructure::getSeparationSettings));
	}
}

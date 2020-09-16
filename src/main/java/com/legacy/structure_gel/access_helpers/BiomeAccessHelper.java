package com.legacy.structure_gel.access_helpers;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.util.GelCollectors;
import com.legacy.structure_gel.worldgen.structure.GelStructure;
import com.legacy.structure_gel.worldgen.structure.IConfigStructure;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Contains methods to add various things to biomes, such as features,
 * structures, carvers, mob spawns, etc. Thanks 1.16.2.
 * 
 * @author David
 *
 */
public class BiomeAccessHelper
{
	/**
	 * Returns the biome generation settings. Mainly a mapped method for
	 * convenience.
	 * 
	 * @param biome
	 * @return {@link BiomeGenerationSettings}
	 */
	public static BiomeGenerationSettings getGenSettings(Biome biome)
	{
		return biome.getGenerationSettings();
	}

	/**
	 * Adds the feature to the biome with the given settings. For flowers, use
	 * {@link #addFlowerFeature(Biome, Decoration, ConfiguredFeature)}. Make sure that the
	 * {@link ConfiguredFeature} is registered first.
	 * 
	 * @param biome
	 * @param stage
	 * @param feature
	 */
	public static <C extends IFeatureConfig, F extends Feature<C>> void addFeature(Biome biome, Decoration stage, ConfiguredFeature<C, F> feature)
	{
		// Make list mutable before I try to mess with it in case it isn't
		if (getGenSettings(biome).features instanceof ImmutableList || (getGenSettings(biome).features.size() > stage.ordinal() && getGenSettings(biome).features.get(stage.ordinal()) instanceof ImmutableList))
			getGenSettings(biome).features = GelCollectors.makeListMutable(getGenSettings(biome).features, GelCollectors::makeListMutable);

		// If the generation stage isn't present, add it and make sure other stages
		// exist because Mojang didn't use a map.
		while (getGenSettings(biome).features.size() <= stage.ordinal())
			getGenSettings(biome).features.add(Lists.newArrayList());

		// Add the feature to the proper stage
		getGenSettings(biome).features.get(stage.ordinal()).add(() -> feature);
	}

	/**
	 * Sets the input {@link Structure} to generate in the biome.
	 * 
	 * @param biome
	 * @param structure
	 * @param separationSettings
	 * @param noiseSettings
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> void addStructure(Biome biome, StructureFeature<C, S> structure, StructureSeparationSettings separationSettings, List<DimensionSettings> noiseSettings)
	{
		// Add structure to the biome's structure list
		getGenSettings(biome).structures = GelCollectors.addToList(getGenSettings(biome).structures, () -> structure);

		// Add structure to starts map
		// Make map mutable before I try to add to it in case it isn't
		if (biome.biomeStructures instanceof ImmutableMap || biome.biomeStructures.get(structure.field_236268_b_.func_236396_f_().ordinal()) instanceof ImmutableList)
			biome.biomeStructures = GelCollectors.makeMapMutable(biome.biomeStructures, Map.Entry::getKey, e -> GelCollectors.makeListMutable(e.getValue()));

		biome.biomeStructures.get(structure.field_236268_b_.func_236396_f_().ordinal()).add(structure.field_236268_b_);

		// Add separation settings to noise settings
		noiseSettings.forEach(noiseSetting ->
		{
			if (!noiseSetting.getStructures().field_236193_d_.containsKey(structure.field_236268_b_))
				noiseSetting.getStructures().field_236193_d_.put(structure.field_236268_b_, separationSettings);
		});
	}

	/**
	 * Sets the input {@link GelStructure} to generate in the biome.
	 * 
	 * @param biome
	 * @param gelStructure
	 */
	public static <C extends IFeatureConfig, S extends GelStructure<C>> void addStructure(Biome biome, StructureFeature<C, S> gelStructure)
	{
		addStructure(biome, gelStructure, gelStructure.field_236268_b_.getSeparationSettings(), gelStructure.field_236268_b_.getNoiseSettingsToGenerateIn());
	}

	/**
	 * Registers the input {@link StructureFeature} to the biomes listed in it's
	 * config if the structure is an instance of {@link IConfigStructure}.
	 * 
	 * @param structure
	 */
	public static <C extends IFeatureConfig, S extends GelStructure<C>> void addStructureToBiomes(StructureFeature<C, S> structure)
	{
		if (structure.field_236268_b_ instanceof IConfigStructure)
		{
			ForgeRegistries.BIOMES.getValues().stream().filter(b -> ((IConfigStructure) structure.field_236268_b_).getConfig().isBiomeAllowed(b)).forEach(b ->
			{
				BiomeAccessHelper.addStructure(b, structure);
			});
		}
		else
		{
			StructureGelMod.LOGGER.error("Attempted to add " + structure.field_236268_b_.getRegistryName() + " to it's configured biomes, but it was not an instance of IConfigStructure.");
		}
	}

	/**
	 * Gets the surface builder for the biome passed in.
	 * 
	 * @param biome
	 * @return {@link ConfiguredSurfaceBuilder}
	 */
	@Nullable
	public static ConfiguredSurfaceBuilder<?> getSurfaceBuilder(Biome biome)
	{
		return getGenSettings(biome).surfaceBuilder.get();
	}

	/**
	 * Sets the surface builder for the biome passed in.
	 * 
	 * @param biome
	 * @param surfaceBuilder
	 */
	public static void setSurfaceBuilder(Biome biome, ConfiguredSurfaceBuilder<?> surfaceBuilder)
	{
		getGenSettings(biome).surfaceBuilder = () -> surfaceBuilder;
	}

	/**
	 * Adds the carver to the biome passed in.
	 * 
	 * @param biome
	 * @param carvingType
	 * @param configuredCarver
	 */
	public static void addCarver(Biome biome, Carving carvingType, ConfiguredCarver<?> configuredCarver)
	{
		// Make the map and it's lists mutable
		if (getGenSettings(biome).carvers instanceof ImmutableMap || getGenSettings(biome).carvers.get(carvingType) instanceof ImmutableList)
			getGenSettings(biome).carvers = GelCollectors.makeMapMutable(getGenSettings(biome).carvers, Map.Entry::getKey, e -> GelCollectors.makeListMutable(e.getValue()));

		// Add an entry to the map for the required carver if it's absent
		if (!getGenSettings(biome).carvers.containsKey(carvingType))
			getGenSettings(biome).carvers.put(carvingType, Lists.newArrayList());

		// Add the carver
		getGenSettings(biome).carvers.get(carvingType).add(() -> configuredCarver);
	}

	/**
	 * Returns the carvers for the given type.
	 * 
	 * @param biome
	 * @param carvingType
	 * @return {@link List}
	 */
	public static List<Supplier<ConfiguredCarver<?>>> getCarvers(Biome biome, Carving carvingType)
	{
		return getGenSettings(biome).getCarvers(carvingType);
	}

	/**
	 * Adds the flower feature to the biome with the given settings.
	 * 
	 * @param biome
	 * @param stage
	 * @param feature
	 */
	public static <C extends IFeatureConfig, PC extends IPlacementConfig> void addFlowerFeature(Biome biome, Decoration stage, ConfiguredFeature<?, ?> feature)
	{
		addFeature(biome, stage, feature);

		// Make list mutable before I try to mess with it in case it isn't
		if (getGenSettings(biome).flowerFeatures instanceof ImmutableList)
			getGenSettings(biome).flowerFeatures = GelCollectors.makeListMutable(getGenSettings(biome).flowerFeatures);

		// Add the feature to the proper stage
		getGenSettings(biome).flowerFeatures.add(feature);
	}

	/**
	 * Adds the mob spawn to the biome.
	 * 
	 * @param biome
	 * @param classification
	 * @param spawner
	 */
	public static void addSpawn(Biome biome, EntityClassification classification, MobSpawnInfo.Spawners spawner)
	{
		// Make the map and it's lists mutable
		if (biome.getMobSpawnInfo().spawners instanceof ImmutableMap || biome.getMobSpawnInfo().spawners.get(classification) instanceof ImmutableList)
			biome.getMobSpawnInfo().spawners = GelCollectors.makeMapMutable(biome.getMobSpawnInfo().spawners, Map.Entry::getKey, (e) -> GelCollectors.makeListMutable(e.getValue()));

		// Add an entry to the map for the required spawner if it's absent
		if (!biome.getMobSpawnInfo().spawners.containsKey(classification))
			biome.getMobSpawnInfo().spawners.put(classification, Lists.newArrayList());

		// Add the spawn
		biome.getMobSpawnInfo().spawners.get(classification).add(spawner);
	}

	/**
	 * Adds the spawn cost to the given biome. This is how the soul sand valley
	 * limits ghast spawns.
	 * 
	 * @param biome
	 * @param entity
	 * @param spawnCost
	 */
	public static void addSpawnCost(Biome biome, EntityType<?> entity, MobSpawnInfo.SpawnCosts spawnCost)
	{
		// Make the map and it's lists mutable
		if (biome.getMobSpawnInfo().spawnCosts instanceof ImmutableMap)
			biome.getMobSpawnInfo().spawnCosts = GelCollectors.makeMapMutable(biome.getMobSpawnInfo().spawnCosts);

		// Add the spawn
		biome.getMobSpawnInfo().spawnCosts.put(entity, spawnCost);
	}

	/**
	 * Sets the ambience for the biome.
	 * 
	 * @param biome
	 * @param ambience
	 */
	public static void setAmbience(Biome biome, BiomeAmbience ambience)
	{
		biome.effects = ambience;
	}
}

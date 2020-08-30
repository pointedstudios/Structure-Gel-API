package com.legacy.structure_gel.access_helpers;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.legacy.structure_gel.util.GelCollectors;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;

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
	 * @return BiomeGenerationSettings
	 */
	public static BiomeGenerationSettings getGenSettings(Biome biome)
	{
		return biome.func_242440_e();
	}

	/**
	 * Adds the feature to the biome with the given settings. For flowers, use
	 * {@link #addFlowerFeature(Biome, Decoration, Feature)}.
	 * 
	 * @param biome
	 * @param stage
	 * @param feature
	 * @param config
	 * @param placement
	 * @param placementConfig
	 */
	public static <C extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(Biome biome, Decoration stage, ConfiguredFeature<?, ?> feature)
	{
		// Make list mutable before I try to mess with it in case it isn't
		if (getGenSettings(biome).field_242484_f instanceof ImmutableList)
			getGenSettings(biome).field_242484_f = GelCollectors.makeListMutable(getGenSettings(biome).field_242484_f, GelCollectors::makeListMutable);

		// If the generation stage isn't present, add it and make sure other stages
		// exist because Mojang didn't use a map.
		while (getGenSettings(biome).field_242484_f.size() <= stage.ordinal())
			getGenSettings(biome).field_242484_f.add(Lists.newArrayList());

		// Add the feature to the proper stage
		getGenSettings(biome).field_242484_f.get(stage.ordinal()).add(() -> feature);
	}

	/**
	 * Adds the input structure to generate in the biome.
	 * 
	 * @param biome
	 * @param structure
	 * @param config
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> void addStructure(Biome biome, StructureFeature<C, S> structure)
	{
		getGenSettings(biome).field_242485_g = GelCollectors.addToList(getGenSettings(biome).field_242485_g, () -> structure);
	}

	/**
	 * Gets the surface builder for the biome passed in.
	 * 
	 * @param biome
	 * @return ConfiguredSurfaceBuilder
	 */
	@Nullable
	public static ConfiguredSurfaceBuilder<?> getSurfaceBuilder(Biome biome)
	{
		return getGenSettings(biome).field_242482_d.get();
	}

	/**
	 * Sets the surface builder for the biome passed in.
	 * 
	 * @param biome
	 * @param surfaceBuilder
	 */
	public static void setSurfaceBuilder(Biome biome, ConfiguredSurfaceBuilder<?> surfaceBuilder)
	{
		getGenSettings(biome).field_242482_d = () -> surfaceBuilder;
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
		if (getGenSettings(biome).field_242483_e instanceof ImmutableMap || (getGenSettings(biome).field_242483_e.containsKey(carvingType) && getGenSettings(biome).field_242483_e.get(carvingType) instanceof ImmutableList))
			getGenSettings(biome).field_242483_e = GelCollectors.makeMapMutable(getGenSettings(biome).field_242483_e, Map.Entry::getKey, (e) -> Lists.newArrayList(e.getValue()));

		// Add an entry to the map for the required carver if it's absent
		if (!getGenSettings(biome).field_242483_e.containsKey(carvingType))
			getGenSettings(biome).field_242483_e.put(carvingType, Lists.newArrayList());

		// Add the carver
		getGenSettings(biome).field_242483_e.get(carvingType).add(() -> configuredCarver);
	}

	/**
	 * Returns the carvers for the given type.
	 * 
	 * @param biome
	 * @param carvingType
	 * @return List
	 */
	public static List<Supplier<ConfiguredCarver<?>>> getCarvers(Biome biome, Carving carvingType)
	{
		return getGenSettings(biome).func_242489_a(carvingType);
	}

	/**
	 * Adds the flower feature to the biome with the given settings.
	 * 
	 * @param biome
	 * @param stage
	 * @param feature
	 * @param config
	 * @param placement
	 * @param placementConfig
	 */
	public static <C extends IFeatureConfig, PC extends IPlacementConfig> void addFlowerFeature(Biome biome, Decoration stage, ConfiguredFeature<?, ?> feature)
	{
		addFeature(biome, stage, feature);

		// Make list mutable before I try to mess with it in case it isn't
		if (getGenSettings(biome).field_242486_h instanceof ImmutableList)
			getGenSettings(biome).field_242486_h = GelCollectors.makeListMutable(getGenSettings(biome).field_242486_h);

		// Add the feature to the proper stage
		getGenSettings(biome).field_242486_h.add(feature);
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
		if (biome.func_242433_b().field_242554_e instanceof ImmutableMap || (biome.func_242433_b().field_242554_e.containsKey(classification) && biome.func_242433_b().field_242554_e.get(classification) instanceof ImmutableList))
			biome.func_242433_b().field_242554_e = GelCollectors.makeMapMutable(biome.func_242433_b().field_242554_e, Map.Entry::getKey, (e) -> GelCollectors.makeListMutable(e.getValue()));

		// Add an entry to the map for the required spawner if it's absent
		if (!biome.func_242433_b().field_242554_e.containsKey(classification))
			biome.func_242433_b().field_242554_e.put(classification, Lists.newArrayList());

		// Add the spawn
		biome.func_242433_b().field_242554_e.get(classification).add(spawner);
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
		if (biome.func_242433_b().field_242555_f instanceof ImmutableMap)
			biome.func_242433_b().field_242555_f = GelCollectors.makeMapMutable(biome.func_242433_b().field_242555_f);

		// Add the spawn
		biome.func_242433_b().field_242555_f.put(entity, spawnCost);
	}

	/**
	 * Sets the ambience for the biome.
	 * 
	 * @param biome
	 * @param ambience
	 */
	public static void setAmbience(Biome biome, BiomeAmbience ambience)
	{
		biome.field_235052_p_ = ambience;
	}
}

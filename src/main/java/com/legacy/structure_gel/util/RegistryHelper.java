package com.legacy.structure_gel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.legacy.structure_gel.registrars.IForgeRegistrar;
import com.legacy.structure_gel.registrars.IRegistrar;
import com.legacy.structure_gel.registrars.StructureRegistrar;
import com.legacy.structure_gel.worldgen.structure.GelStructure;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * A simple class that gives methods to ease the registry process by a bit.
 * <p>
 * Note: As of 1.16.2, jigsaw structures no longer require a piece type.
 *
 * @author David
 */
public class RegistryHelper
{
	public static final Map<Structure<?>, StructureSeparationSettings> STRUCTURE_SETTINGS_MAP = new HashMap<>();

	/**
	 * Returns an optional containing the registry key associated with the value
	 * passed.
	 *
	 * @param world
	 * @param registry
	 * @param value
	 * @return {@link Optional}
	 */
	public static <T> Optional<RegistryKey<T>> getKey(IWorld world, RegistryKey<Registry<T>> registry, T value)
	{
		return world.func_241828_r().getRegistry(registry).getOptionalKey(value);
	}

	/**
	 * Returns an optional containing the value of the registry key passed.
	 *
	 * @param world
	 * @param registry
	 * @param key
	 * @return {@link Optional}
	 */
	public static <T> Optional<T> getValue(IWorld world, RegistryKey<Registry<T>> registry, RegistryKey<T> key)
	{
		return Optional.ofNullable(world.func_241828_r().getRegistry(registry).getValueForKey(key));
	}

	/**
	 * Simple means of registering to a forge registry. Returns the exact type of
	 * the object passed in.
	 *
	 * @param registry
	 * @param key
	 * @param registryObject
	 * @return The registryObject
	 */
	public static <T extends IForgeRegistryEntry<T>, C extends T> C register(IForgeRegistry<T> registry, ResourceLocation key, C registryObject)
	{
		registryObject.setRegistryName(key);
		registry.register(registryObject);
		return registryObject;
	}

	/**
	 * Registers the {@link PointOfInterestType} properly since the methods to do it
	 * are private normally.
	 *
	 * @param registry
	 * @param poi
	 * @return {@link PointOfInterestType}
	 */
	public static PointOfInterestType registerPOI(IForgeRegistry<PointOfInterestType> registry, PointOfInterestType poi)
	{
		PointOfInterestType.registerBlockStates(poi);
		return register(registry, new ResourceLocation(poi.toString()), poi);
	}

	/**
	 * Registers the configured feature. If you don't add register a configured
	 * feature, things break when you try to add it to a biome.
	 *
	 * @param key
	 * @param configuredFeature
	 * @return {@link ConfiguredFeature}
	 */
	public static <FC extends IFeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> registerConfiguredFeature(ResourceLocation key, ConfiguredFeature<FC, F> configuredFeature)
	{
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, configuredFeature);
	}

	/**
	 * Registers the input structure to both the forge Feature registry and the
	 * Structure Feature registry.
	 * 
	 * @deprecated TODO 1.17 remove. Does not add to FlatGenerationSettings. use
	 *             {@link #registerStructure(IForgeRegistry, StructureRegistrar, StructureSeparationSettings)}
	 * @param registry
	 * @param key
	 * @param structure
	 * @param generationStage
	 * @return {@link Structure}
	 */
	@Deprecated
	public static <C extends IFeatureConfig, S extends Structure<C>> S registerStructure(IForgeRegistry<Structure<?>> registry, ResourceLocation key, S structure, Decoration generationStage)
	{
		if (structure.getRegistryName() == null)
			structure.setRegistryName(key);
		return registerStructure(registry, key, structure, generationStage, structure instanceof GelStructure<?> ? ((GelStructure<?>) structure).getSeparationSettings() : new StructureSeparationSettings(32, 8, 0));
	}

	/**
	 * Registers the input structure to both the forge Feature registry and the
	 * Structure Feature registry
	 * 
	 * @deprecated TODO 1.17 remove. Does not add to FlatGenerationSettings. use
	 *             {@link #registerStructure(IForgeRegistry, StructureRegistrar, StructureSeparationSettings)}
	 * @param registry
	 * @param key
	 * @param structure
	 * @param generationStage
	 * @return {@link Structure}
	 */
	@Deprecated
	public static <C extends IFeatureConfig, S extends Structure<C>> S registerStructure(IForgeRegistry<Structure<?>> registry, ResourceLocation key, S structure, Decoration generationStage, StructureSeparationSettings separationSettings)
	{
		if (structure.getRegistryName() == null)
			structure.setRegistryName(key);
		registry.register(structure);
		Structure.NAME_STRUCTURE_BIMAP.put(key.toString(), structure);
		Structure.STRUCTURE_DECORATION_STAGE_MAP.put(structure, generationStage);
		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder().putAll(DimensionStructuresSettings.field_236191_b_).put(structure, separationSettings).build();
		STRUCTURE_SETTINGS_MAP.put(structure, separationSettings);
		return structure;
	}

	/**
	 * 
	 * @param registry
	 * @param registrar
	 * @param separationSettings
	 * @return {@link Structure}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> S registerStructure(IForgeRegistry<Structure<?>> registry, StructureRegistrar<C, S> registrar)
	{
		S structure = registrar.getStructure();
		if (structure.getRegistryName() == null)
			structure.setRegistryName(registrar.getRegistryName());
		registry.register(structure);
		Structure.NAME_STRUCTURE_BIMAP.put(registrar.getRegistryName().toString(), structure);
		Structure.STRUCTURE_DECORATION_STAGE_MAP.put(structure, registrar.getGenerationStage());
		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder().putAll(DimensionStructuresSettings.field_236191_b_).put(structure, registrar.getSeparationSettings()).build();
		FlatGenerationSettings.STRUCTURES = GelCollectors.addToMap(FlatGenerationSettings.STRUCTURES, structure, registrar.getStructureFeature());
		STRUCTURE_SETTINGS_MAP.put(structure, registrar.getSeparationSettings());
		return structure;
	}

	/**
	 * Registers the input {@link IStructurePieceType}
	 *
	 * @param key
	 * @param pieceType
	 * @return {@link IStructurePieceType}
	 */
	public static <P extends IStructurePieceType> P registerStructurePiece(ResourceLocation key, P pieceType)
	{
		return Registry.register(Registry.STRUCTURE_PIECE, key, pieceType);
	}

	/**
	 * Registers the input {@link Structure} and {@link IStructurePieceType}.
	 *
	 * @param registry
	 * @param key
	 * @param structure
	 * @param generationStage
	 * @param pieceType
	 * @return {@link Pair}
	 * @deprecated Use
	 *             {@link RegistryHelper#handleRegistrar(IForgeRegistrar, IForgeRegistry)}
	 */
	@Deprecated
	public static <C extends IFeatureConfig, S extends Structure<C>, P extends IStructurePieceType> Pair<S, P> registerStructureAndPiece(IForgeRegistry<Structure<?>> registry, ResourceLocation key, S structure, Decoration generationStage, P pieceType)
	{
		S struc = registerStructure(registry, key, structure, generationStage);
		P piece = registerStructurePiece(key, pieceType);
		return Pair.of(struc, piece);
	}

	/**
	 * Registers the structure as a feature for world generation.
	 *
	 * @param key
	 * @param structureFeature
	 * @return {@link StructureFeature}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>, SF extends StructureFeature<C, S>> SF registerStructureFeature(ResourceLocation key, SF structureFeature)
	{
		return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, key, structureFeature);
	}

	/**
	 * Registers the structure as a feature for world generation. Uses the
	 * structure's registry name as the key.
	 *
	 * @param structureFeature
	 * @return {@link StructureFeature}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>, SF extends StructureFeature<C, S>> SF registerStructureFeature(SF structureFeature)
	{
		return registerStructureFeature(structureFeature.field_236268_b_.getRegistryName(), structureFeature);
	}

	/**
	 * Executes the handle method in the registrar.
	 *
	 * @param registrar
	 * @return {@link IRegistrar}
	 */
	public static <R extends IRegistrar<R>> R handleRegistrar(R registrar)
	{
		return registrar.handle();
	}

	/**
	 * Executes the handleForge method in the registrar.
	 *
	 * @param registrar
	 * @param registry
	 * @return {@link IForgeRegistrar}
	 */
	public static <R extends IForgeRegistrar<R, V>, V extends IForgeRegistryEntry<V>> R handleRegistrar(R registrar, IForgeRegistry<V> registry)
	{
		return registrar.handleForge(registry);
	}

	/**
	 * Registers the {@link StructureProcessor} as a {@link StructureProcessorList}.
	 *
	 * @param key
	 * @param processor
	 * @return {@link StructureProcessorList}
	 */
	public static StructureProcessorList registerProcessor(ResourceLocation key, StructureProcessor processor)
	{
		return WorldGenRegistries.register(WorldGenRegistries.STRUCTURE_PROCESSOR_LIST, key, new StructureProcessorList(ImmutableList.of(processor)));
	}

	/**
	 * Registers the {@link StructureProcessorList}.
	 *
	 * @param key
	 * @param processorList
	 * @return {@link StructureProcessorList}
	 */
	public static StructureProcessorList registerProcessor(ResourceLocation key, StructureProcessorList processorList)
	{
		return WorldGenRegistries.register(WorldGenRegistries.STRUCTURE_PROCESSOR_LIST, key, processorList);
	}

	/**
	 * Registers the list of {@link StructureProcessor}s as a
	 * {@link StructureProcessorList}
	 *
	 * @param key
	 * @param processors
	 * @return {@link StructureProcessorList}
	 */
	public static StructureProcessorList registerProcessor(ResourceLocation key, List<StructureProcessor> processors)
	{
		return WorldGenRegistries.register(WorldGenRegistries.STRUCTURE_PROCESSOR_LIST, key, new StructureProcessorList(processors));
	}

	/**
	 * Merges the passed {@link StructureProcessorList} array and returns the
	 * result.
	 *
	 * @param lists
	 * @return {@link StructureProcessorList}
	 */
	public static StructureProcessorList combineProcessors(StructureProcessorList... lists)
	{
		List<StructureProcessor> processors = new ArrayList<>();
		Arrays.asList(lists).forEach(spl -> processors.addAll(spl.func_242919_a()));
		return new StructureProcessorList(processors);
	}

	/**
	 * Merges the {@link StructureProcessorList} with the list of
	 * {@link StructureProcessor}s and returns the resulting
	 * {@link StructureProcessorList}.
	 *
	 * @param list
	 * @param processors
	 * @return {@link StructureProcessorList}
	 */
	public static StructureProcessorList combineProcessors(StructureProcessorList list, List<StructureProcessor> processors)
	{
		return new StructureProcessorList(Streams.concat(list.func_242919_a().stream(), processors.stream()).collect(ImmutableList.toImmutableList()));
	}
}

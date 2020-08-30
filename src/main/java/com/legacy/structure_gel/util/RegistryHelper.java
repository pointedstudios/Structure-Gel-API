package com.legacy.structure_gel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * A simple class that gives methods to ease the registry process by a bit.
 * 
 * Note: As of 1.16.2, jigsaw structures no longer require a piece type.
 * 
 * @author David
 *
 */
public class RegistryHelper
{
	/**
	 * Simply means of registering to a forge registry.
	 * 
	 * @param registry
	 * @param key
	 * @param registryObject
	 * @return T
	 */
	public static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, ResourceLocation key, T registryObject)
	{
		registryObject.setRegistryName(key);
		registry.register(registryObject);
		return registryObject;
	}

	/**
	 * Registers the input structure to both the forge Feature registry and the
	 * Structure Feature registry
	 * 
	 * @param registry
	 * @param key
	 * @param structure
	 * @param generationStage
	 * @return Structure
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> S registerStructure(IForgeRegistry<Structure<?>> registry, ResourceLocation key, S structure, Decoration generationStage)
	{
		structure.setRegistryName(key);
		registry.register(structure);
		Structure.field_236365_a_.put(key.toString(), structure);
		Structure.field_236385_u_.put(structure, generationStage);
		return structure;
	}

	/**
	 * Registers the input {@link IStructurePieceType}
	 * 
	 * @param key
	 * @param structure
	 * @param pieceType
	 * @return IStructurePieceType
	 */
	public static <P extends IStructurePieceType> P registerStructurePiece(ResourceLocation key, P pieceType)
	{
		return Registry.register(Registry.STRUCTURE_PIECE, key, pieceType);
	}

	/**
	 * Registers the input {@link Structure} and {@link IStructurePieceType}
	 * 
	 * @see RegistryHelper#registerStructure(IForgeRegistry, ResourceLocation,
	 *      Structure)
	 * @see RegistryHelper#registerStructurePiece(ResourceLocation,
	 *      IStructurePieceType)
	 * @param registry
	 * @param key
	 * @param structure
	 * @param generationStage
	 * @param pieceType
	 * @return Pair
	 */
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
	 * @return StructureFeature
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>, SF extends StructureFeature<C, S>> SF registerStructureFeature(ResourceLocation key, SF structureFeature)
	{
		return WorldGenRegistries.func_243664_a(WorldGenRegistries.field_243654_f, key, structureFeature);
	}

	/**
	 * Registers the structure as a feature for world generation. Uses the
	 * structure's registry name as the key.
	 * 
	 * @param structureFeature
	 * @return StructureFeature
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>, SF extends StructureFeature<C, S>> SF registerStructureFeature(SF structureFeature)
	{
		return registerStructureFeature(structureFeature.field_236268_b_.getRegistryName(), structureFeature);
	}

	/**
	 * Registers the structure, it's piece, and it's structure feature, and returns
	 * a {@link StructureRegistrar} to hold the results.
	 * 
	 * @param registry
	 * @param key
	 * @param structure
	 * @param generationStage
	 * @param pieceType
	 * @param config
	 * @return StructureRegistrar
	 */
	@SuppressWarnings("unchecked")
	public static <C extends IFeatureConfig, S extends Structure<C>, SF extends StructureFeature<C, S>, P extends IStructurePieceType> StructureRegistrar<S, P, SF> handleRegistrar(IForgeRegistry<Structure<?>> registry, ResourceLocation key, S structure, Decoration generationStage, P pieceType, C config)
	{
		S struct = registerStructure(registry, key, structure, generationStage);
		P piece = registerStructurePiece(key, pieceType);
		SF feature = (SF) registerStructureFeature(key, structure.func_236391_a_(config));
		return StructureRegistrar.of(struct, piece, feature);
	}

	public static StructureProcessorList registerProcessor(ResourceLocation key, StructureProcessor processor)
	{
		return WorldGenRegistries.func_243664_a(WorldGenRegistries.field_243655_g, key, new StructureProcessorList(ImmutableList.of(processor)));
	}

	public static StructureProcessorList registerProcessor(ResourceLocation key, StructureProcessorList processorList)
	{
		return WorldGenRegistries.func_243664_a(WorldGenRegistries.field_243655_g, key, processorList);
	}

	public static StructureProcessorList registerProcessor(ResourceLocation key, ImmutableList<StructureProcessor> processors)
	{
		return WorldGenRegistries.func_243664_a(WorldGenRegistries.field_243655_g, key, new StructureProcessorList(processors));
	}

	public static StructureProcessorList combineProcessors(StructureProcessorList... lists)
	{
		List<StructureProcessor> processors = new ArrayList<>();
		Arrays.asList(lists).forEach(spl -> processors.addAll(spl.func_242919_a()));
		return new StructureProcessorList(processors);
	}

	public static StructureProcessorList combineProcessors(StructureProcessorList list, List<StructureProcessor> processors)
	{
		List<StructureProcessor> processors2 = list.func_242919_a();
		processors2.addAll(processors);
		return new StructureProcessorList(processors2);
	}
}
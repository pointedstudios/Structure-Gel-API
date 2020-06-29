package com.legacy.structure_gel.util;

import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * A simple class that gives methods to ease the registry process by a bit.
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
	 * @see RegistryHelper#registerStructure(IForgeRegistry, ResourceLocation,
	 *      Structure)
	 * @param registry
	 * @param modid
	 * @param key
	 * @param structure
	 * @return {@link Structure}
	 */
	public static <C extends IFeatureConfig> Structure<C> registerStructure(IForgeRegistry<Structure<?>> registry, String modid, String key, Structure<C> structure)
	{
		return registerStructure(registry, new ResourceLocation(modid, key), structure);
	}

	/**
	 * Registers the input {@link IStructurePieceType}
	 * 
	 * @see RegistryHelper#registerStructurePiece(ResourceLocation, Structure,
	 *      IStructurePieceType)
	 * @param modid
	 * @param key
	 * @param structure
	 * @param pieceType
	 * @return {@link IStructurePieceType}
	 */
	public static <C extends IFeatureConfig> IStructurePieceType registerStructurePiece(String modid, String key, IStructurePieceType pieceType)
	{
		return registerStructurePiece(new ResourceLocation(modid, key), pieceType);
	}

	/**
	 * Registers the input {@link Structure} and {@link IStructurePieceType}
	 * 
	 * @see RegistryHelper#registerStructure(IForgeRegistry, ResourceLocation,
	 *      Structure)
	 * @see RegistryHelper#registerStructurePiece(ResourceLocation, Structure,
	 *      IStructurePieceType)
	 * @param registry
	 * @param modid
	 * @param key
	 * @param structure
	 * @param pieceType
	 * @return {@link Pair}
	 */
	public static <C extends IFeatureConfig> Pair<Structure<C>, IStructurePieceType> registerStructureAndPiece(IForgeRegistry<Structure<?>> registry, String modid, String key, Structure<C> structure, IStructurePieceType pieceType)
	{
		return registerStructureAndPiece(registry, new ResourceLocation(modid, key), structure, pieceType);
	}

	/**
	 * Registers the input structure to both the forge Feature registry and the
	 * Structure Feature registry
	 * 
	 * @param registry
	 * @param key
	 * @param structure
	 * @return {@link Structure}
	 */
	public static <C extends IFeatureConfig> Structure<C> registerStructure(IForgeRegistry<Structure<?>> registry, ResourceLocation key, Structure<C> structure)
	{
		structure.setRegistryName(key);
		registry.register(structure);
		Structure.field_236365_a_.put(key.toString(), structure);
		return structure;
	}

	/**
	 * Registers the input {@link IStructurePieceType}
	 * 
	 * @param key
	 * @param structure
	 * @param pieceType
	 * @return {@link IStructurePieceType}
	 */
	public static <C extends IFeatureConfig> IStructurePieceType registerStructurePiece(ResourceLocation key, IStructurePieceType pieceType)
	{
		return Registry.register(Registry.STRUCTURE_PIECE, key, pieceType);
	}

	/**
	 * Registers the input {@link Structure} and {@link IStructurePieceType}
	 * 
	 * @see RegistryHelper#registerStructure(IForgeRegistry, ResourceLocation,
	 *      Structure)
	 * @see RegistryHelper#registerStructurePiece(ResourceLocation, Structure,
	 *      IStructurePieceType)
	 * @param registry
	 * @param key
	 * @param structure
	 * @param pieceType
	 * @return {@link Pair}
	 */
	public static <C extends IFeatureConfig> Pair<Structure<C>, IStructurePieceType> registerStructureAndPiece(IForgeRegistry<Structure<?>> registry, ResourceLocation key, Structure<C> structure, IStructurePieceType pieceType)
	{
		Structure<C> struc = registerStructure(registry, key, structure);
		IStructurePieceType piece = registerStructurePiece(key, pieceType);
		return Pair.of(struc, piece);
	}

	/**
	 * Adds the feature to the biome with the given settings.
	 * 
	 * @param biome
	 * @param stage
	 * @param feature
	 * @param config
	 * @param placement
	 * @param placementConfig
	 */
	public static <C extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(Biome biome, Decoration stage, Feature<C> feature, C config, Placement<PC> placement, PC placementConfig)
	{
		biome.addFeature(stage, feature.withConfiguration(config).withPlacement(placement.configure(placementConfig)));
	}

	/**
	 * @see RegistryHelper#addFeature(Biome, Decoration, Feature, IFeatureConfig,
	 *      Placement, IPlacementConfig)
	 * @param biome
	 * @param stage
	 * @param feature
	 * @param config
	 * @param placement
	 */
	public static <C extends IFeatureConfig> void addFeature(Biome biome, Decoration stage, Feature<C> feature, C config, Placement<NoPlacementConfig> placement)
	{
		addFeature(biome, stage, feature, config, placement, IPlacementConfig.NO_PLACEMENT_CONFIG);
	}

	/**
	 * @see RegistryHelper#addFeature(Biome, Decoration, Feature, IFeatureConfig,
	 *      Placement, IPlacementConfig)
	 * @param biome
	 * @param stage
	 * @param feature
	 * @param config
	 */
	public static <C extends IFeatureConfig> void addFeature(Biome biome, Decoration stage, Feature<C> feature, C config)
	{
		addFeature(biome, stage, feature, config, Placement.NOPE);
	}

	/**
	 * Adds the input feature to the biome assuming no configs.
	 * 
	 * @see RegistryHelper#addFeature(Biome, Decoration, Feature, IFeatureConfig,
	 *      Placement, IPlacementConfig)
	 * @param biome
	 * @param stage
	 * @param feature
	 */
	public static void addFeature(Biome biome, Decoration stage, Feature<NoFeatureConfig> feature)
	{
		addFeature(biome, stage, feature, IFeatureConfig.NO_FEATURE_CONFIG);
	}

	/**
	 * Adds the input structure to generate in the biome.
	 * 
	 * @param biome
	 * @param structure
	 * @param config
	 */
	public static <C extends IFeatureConfig> void addStructure(Biome biome, Structure<C> structure, C config)
	{
		biome.func_235063_a_(structure.func_236391_a_(config));
	}

	/**
	 * Adds the input structure to generate in the biome with no config.
	 * 
	 * @see RegistryHelper#addStructure(Biome, Structure, IFeatureConfig)
	 * @param biome
	 * @param structure
	 */
	public static void addStructure(Biome biome, Structure<NoFeatureConfig> structure)
	{
		addStructure(biome, structure, IFeatureConfig.NO_FEATURE_CONFIG);
	}
}
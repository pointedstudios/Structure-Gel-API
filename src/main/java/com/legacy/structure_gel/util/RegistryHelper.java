package com.legacy.structure_gel.util;

import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
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
	public static <C extends IFeatureConfig, S extends Structure<C>> S registerStructure(IForgeRegistry<Structure<?>> registry, String modid, String key, S structure)
	{
		return registerStructure(registry, new ResourceLocation(modid, key), structure);
	}

	/**
	 * Registers the input {@link IStructurePieceType}
	 * 
	 * @see RegistryHelper#registerStructurePiece(ResourceLocation,
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
	 * @see RegistryHelper#registerStructurePiece(ResourceLocation,
	 *      IStructurePieceType)
	 * @param registry
	 * @param modid
	 * @param key
	 * @param structure
	 * @param pieceType
	 * @return {@link Pair}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> Pair<S, IStructurePieceType> registerStructureAndPiece(IForgeRegistry<Structure<?>> registry, String modid, String key, S structure, IStructurePieceType pieceType)
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
	public static <C extends IFeatureConfig, S extends Structure<C>> S registerStructure(IForgeRegistry<Structure<?>> registry, ResourceLocation key, S structure)
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
	 * @see RegistryHelper#registerStructurePiece(ResourceLocation,
	 *      IStructurePieceType)
	 * @param registry
	 * @param key
	 * @param structure
	 * @param pieceType
	 * @return {@link Pair}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> Pair<S, IStructurePieceType> registerStructureAndPiece(IForgeRegistry<Structure<?>> registry, ResourceLocation key, S structure, IStructurePieceType pieceType)
	{
		S struc = registerStructure(registry, key, structure);
		IStructurePieceType piece = registerStructurePiece(key, pieceType);
		return Pair.of(struc, piece);
	}
}
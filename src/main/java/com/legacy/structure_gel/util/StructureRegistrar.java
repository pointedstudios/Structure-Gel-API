package com.legacy.structure_gel.util;

import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;

/**
 * Stores a registered {@link Structure}, {@link IStructurePieceType}, and
 * {@link StructureFeature} with accessors. Use with
 * {@link RegistryHelper#handleRegistrar(net.minecraftforge.registries.IForgeRegistry, net.minecraft.util.ResourceLocation, Structure, net.minecraft.world.gen.GenerationStage.Decoration, IStructurePieceType, IFeatureConfig)}
 * during registry.
 * 
 * @author David
 *
 * @param <C>
 * @param <S>
 */
public class StructureRegistrar<C extends IFeatureConfig, S extends Structure<C>>
{
	private final S structure;
	private final IStructurePieceType pieceType;
	private final StructureFeature<C, S> structureFeature;

	private StructureRegistrar(S structure, IStructurePieceType pieceType, StructureFeature<C, S> structureFeature)
	{
		this.structure = structure;
		this.pieceType = pieceType;
		this.structureFeature = structureFeature;
	}

	public static <C extends IFeatureConfig, S extends Structure<C>> StructureRegistrar<C, S> of(S structure, IStructurePieceType pieceType, StructureFeature<C, S> structureFeature)
	{
		return new StructureRegistrar<C, S>(structure, pieceType, structureFeature);
	}

	/**
	 * Gets the {@link Structure}.
	 * 
	 * @return The {@link Structure} passed in
	 */
	public S getStructure()
	{
		return this.structure;
	}

	/**
	 * Gets the {@link IStructurePieceType}. This is what you use in your
	 * {@link StructurePiece}.
	 * 
	 * @return {@link IStructurePieceType}
	 */
	public IStructurePieceType getPieceType()
	{
		return this.pieceType;
	}

	/**
	 * Gets the {@link StructureFeature}. This is what you add to a biome.
	 * 
	 * @return {@link StructureFeature}
	 */
	public StructureFeature<C, S> getStructureFeature()
	{
		return this.structureFeature;
	}
}

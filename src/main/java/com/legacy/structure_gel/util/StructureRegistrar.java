package com.legacy.structure_gel.util;

import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;

/**
 * Stores a registered {@link Structure}, {@link IStructurePieceType}, and
 * {@link StructureFeature} with accessors. Use with
 * {@link RegistryHelper#handleRegistrar(net.minecraftforge.registries.IForgeRegistry, net.minecraft.util.ResourceLocation, Structure, net.minecraft.world.gen.GenerationStage.Decoration, IStructurePieceType, IFeatureConfig)}
 * during registry.
 * 
 * @author David
 *
 * @param <S>
 * @param <P>
 * @param <SF>
 */
public class StructureRegistrar<S, P, SF>
{
	private final S structure;
	private final P pieceType;
	private final SF structureFeature;

	private StructureRegistrar(S structure, P pieceType, SF structureFeature)
	{
		this.structure = structure;
		this.pieceType = pieceType;
		this.structureFeature = structureFeature;
	}

	public static <C extends IFeatureConfig, S extends Structure<C>, SF extends StructureFeature<C, S>, P extends IStructurePieceType> StructureRegistrar<S, P, SF> of(S structure, P pieceType, SF structureFeature)
	{
		return new StructureRegistrar<>(structure, pieceType, structureFeature);
	}

	public S getStructure()
	{
		return this.structure;
	}

	public P getPieceType()
	{
		return this.pieceType;
	}

	public SF getStructureFeature()
	{
		return this.structureFeature;
	}
}

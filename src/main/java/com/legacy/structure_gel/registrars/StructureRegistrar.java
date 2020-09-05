package com.legacy.structure_gel.registrars;

import com.legacy.structure_gel.util.RegistryHelper;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Stores a {@link Structure}, {@link IStructurePieceType}, and
 * {@link StructureFeature} and registers with {@link #handle()} or
 * {@link RegistryHelper#handleRegistrar(IRegistrar)}.
 * 
 * @author David
 *
 * @param <C>
 * @param <S>
 */
public class StructureRegistrar<C extends IFeatureConfig, S extends Structure<C>> implements IForgeRegistrar<StructureRegistrar<C, S>, Structure<?>>
{
	private final ResourceLocation name;
	private final S structure;
	private final IStructurePieceType pieceType;
	private final StructureFeature<C, S> structureFeature;
	private final GenerationStage.Decoration generationStage;

	@SuppressWarnings("unchecked")
	public StructureRegistrar(ResourceLocation name, S structure, IStructurePieceType pieceType, C config, GenerationStage.Decoration generationStage)
	{
		this.name = name;
		this.structure = structure;
		this.pieceType = pieceType;
		this.structureFeature = (StructureFeature<C, S>) structure.func_236391_a_(config);
		this.generationStage = generationStage;
	}

	/**
	 * Handy method so you don't have to type the generic type parameters.
	 * 
	 * @param registry
	 * @param name
	 * @param structure
	 * @param pieceType
	 * @param config
	 * @param generationStage
	 * @return {@link StructureRegistrar}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> StructureRegistrar<C, S> of(ResourceLocation name, S structure, IStructurePieceType pieceType, C config, GenerationStage.Decoration generationStage)
	{
		return new StructureRegistrar<C, S>(name, structure, pieceType, config, generationStage);
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

	@Override
	public StructureRegistrar<C, S> handle()
	{
		RegistryHelper.registerStructurePiece(this.name, this.pieceType);
		RegistryHelper.registerStructureFeature(this.name, this.structureFeature);
		return this;
	}
	
	@Override
	public StructureRegistrar<C, S> handleForge(IForgeRegistry<Structure<?>> registry)
	{
		RegistryHelper.registerStructure(registry, this.name, this.structure, this.generationStage);
		return this;
	}
}

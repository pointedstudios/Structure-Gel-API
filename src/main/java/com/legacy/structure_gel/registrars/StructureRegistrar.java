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
public class StructureRegistrar<C extends IFeatureConfig, S extends Structure<C>> implements IRegistrar<StructureRegistrar<C, S>>
{
	private final IForgeRegistry<Structure<?>> registry;
	private final ResourceLocation name;
	private final S structure;
	private final IStructurePieceType pieceType;
	private final StructureFeature<C, S> structureFeature;
	private final C config;
	private final GenerationStage.Decoration generationStage;

	@SuppressWarnings("unchecked")
	public StructureRegistrar(IForgeRegistry<Structure<?>> registry, ResourceLocation name, S structure, IStructurePieceType pieceType, C config, GenerationStage.Decoration generationStage)
	{
		this.registry = registry;
		this.name = name;
		this.structure = structure;
		this.pieceType = pieceType;
		this.structureFeature = (StructureFeature<C, S>) structure.func_236391_a_(config);
		this.config = config;
		this.generationStage = generationStage;
	}
	
	public static <C extends IFeatureConfig, S extends Structure<C>> StructureRegistrar<C, S> of(IForgeRegistry<Structure<?>> registry, ResourceLocation name, S structure, IStructurePieceType pieceType, C config, GenerationStage.Decoration generationStage)
	{
		return new StructureRegistrar<C, S>(registry, name, structure, pieceType, config, generationStage);
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
		RegistryHelper.registerStructure(this.registry, this.name, this.structure, this.generationStage);
		RegistryHelper.registerStructurePiece(this.name, this.pieceType);
		RegistryHelper.registerStructureFeature(this.name, this.structure.func_236391_a_(this.config));
		return this;
	}
}

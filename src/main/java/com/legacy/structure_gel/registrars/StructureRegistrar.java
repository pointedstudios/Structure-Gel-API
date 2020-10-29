package com.legacy.structure_gel.registrars;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
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
	private final Map<String, StructureFeature<C, S>> structureFeatures;
	private final GenerationStage.Decoration generationStage;

	@SuppressWarnings("unchecked")
	public StructureRegistrar(ResourceLocation name, S structure, IStructurePieceType pieceType, Map<String, C> configs, GenerationStage.Decoration generationStage)
	{
		this.name = name;
		this.structure = structure;
		this.pieceType = pieceType;
		this.structureFeatures = configs.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (v) -> (StructureFeature<C, S>) structure.withConfiguration(v.getValue())));
		this.generationStage = generationStage;
	}

	public StructureRegistrar(ResourceLocation name, S structure, IStructurePieceType pieceType, C config, GenerationStage.Decoration generationStage)
	{
		this(name, structure, pieceType, ImmutableMap.of("", config), generationStage);
	}

	/**
	 * Handy method so you don't have to type the generic parameters.
	 * 
	 * @param name
	 * @param structure
	 * @param pieceType
	 * @param configs
	 * @param generationStage
	 * @return {@link StructureRegistrar}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> StructureRegistrar<C, S> of(ResourceLocation name, S structure, IStructurePieceType pieceType, Map<String, C> configs, GenerationStage.Decoration generationStage)
	{
		return new StructureRegistrar<C, S>(name, structure, pieceType, configs, generationStage);
	}

	/**
	 * Handy method so you don't have to type the generic parameters.
	 * 
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
	 * Gets the list of {@link StructureFeature}s.
	 * 
	 * @return {@link List}
	 */
	public Map<String, StructureFeature<C, S>> getStructureFeatures()
	{
		return this.structureFeatures;
	}

	/**
	 * Returns the {@link StructureFeature} for the name passed.
	 * 
	 * @return {@link StructureFeature}
	 */
	@Nullable
	public StructureFeature<C, S> getStructureFeature(String name)
	{
		return this.structureFeatures.get(name);
	}

	/**
	 * Returns the first {@link StructureFeature} in the map. Only use this if you
	 * only have one StructureFeature registered for this structure.
	 * 
	 * @return {@link StructureFeature}
	 */
	@Nullable
	public StructureFeature<C, S> getStructureFeature()
	{
		if (this.structureFeatures.size() > 0)
			return this.structureFeatures.get(this.structureFeatures.keySet().toArray()[0]);
		else
			return null;
	}

	@Override
	public StructureRegistrar<C, S> handle()
	{
		RegistryHelper.registerStructurePiece(this.name, this.pieceType);
		this.structureFeatures.forEach((s, sf) -> RegistryHelper.registerStructureFeature(s.isEmpty() ? this.name : new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "_" + s), sf));
		return this;
	}

	@Override
	public StructureRegistrar<C, S> handleForge(IForgeRegistry<Structure<?>> registry)
	{
		RegistryHelper.registerStructure(registry, this.name, this.structure, this.generationStage);
		return this;
	}
}

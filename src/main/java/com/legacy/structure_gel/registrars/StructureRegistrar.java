package com.legacy.structure_gel.registrars;

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

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Stores a {@link Structure}, {@link IStructurePieceType}, and
 * {@link StructureFeature} and registers with {@link #handle()} or
 * {@link RegistryHelper#handleRegistrar(IRegistrar)}.
 *
 * @param <C>
 * @param <S>
 * @author David
 */
public class StructureRegistrar<C extends IFeatureConfig, S extends Structure<C>> implements IForgeRegistrar<StructureRegistrar<C, S>, Structure<?>>
{
	private final ResourceLocation name;
	private final S structure;
	private final Map<String, IStructurePieceType> pieceTypes;
	private final Map<String, StructureFeature<C, S>> structureFeatures;
	private final GenerationStage.Decoration generationStage;

	/**
	 * The most simple structure with only one config and one piece type
	 *
	 * @param name
	 * @param structure
	 * @param pieceType
	 * @param config
	 * @param generationStage
	 */
	public StructureRegistrar(ResourceLocation name, S structure, IStructurePieceType pieceType, C config, GenerationStage.Decoration generationStage)
	{
		this(name, structure, ImmutableMap.of("", pieceType), ImmutableMap.of("", config), generationStage);
	}

	/**
	 * A structure with one piece type and multiple configured features. Generally
	 * those would be used for different biomes, like how villages work
	 *
	 * @param name
	 * @param structure
	 * @param pieceType
	 * @param configs
	 * @param generationStage
	 */
	public StructureRegistrar(ResourceLocation name, S structure, IStructurePieceType pieceType, Map<String, C> configs, GenerationStage.Decoration generationStage)
	{
		this(name, structure, ImmutableMap.of("", pieceType), configs, generationStage);
	}

	/**
	 * A structure with multiple piece types and one config. Generally this would be
	 * used for a structure with a lot of complexity in its pieces
	 *
	 * @param name
	 * @param structure
	 * @param pieceTypes
	 * @param config
	 * @param generationStage
	 */
	public StructureRegistrar(ResourceLocation name, S structure, Map<String, IStructurePieceType> pieceTypes, C config, GenerationStage.Decoration generationStage)
	{
		this(name, structure, pieceTypes, ImmutableMap.of("", config), generationStage);
	}

	/**
	 * A structure with multiple piece types and multiple configs. This would be for
	 * something with many pieces and different configured variations. Probably the
	 * most rare type to need
	 *
	 * @param name
	 * @param structure
	 * @param pieceTypes
	 * @param configs
	 * @param generationStage
	 */
	@SuppressWarnings("unchecked")
	public StructureRegistrar(ResourceLocation name, S structure, Map<String, IStructurePieceType> pieceTypes, Map<String, C> configs, GenerationStage.Decoration generationStage)
	{
		this.name = name;
		this.structure = structure;
		this.pieceTypes = pieceTypes;
		this.structureFeatures = configs.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (v) -> (StructureFeature<C, S>) structure.withConfiguration(v.getValue())));
		this.generationStage = generationStage;
	}

	/**
	 * Handy method so you don't have to type the generic type parameters.
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
	 * Handy method so you don't have to type the generic type parameters.
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
	 * Handy method so you don't have to type the generic type parameters.
	 *
	 * @param name
	 * @param structure
	 * @param pieceType
	 * @param configs
	 * @param generationStage
	 * @return {@link StructureRegistrar}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> StructureRegistrar<C, S> of(ResourceLocation name, S structure, Map<String, IStructurePieceType> pieceTypes, Map<String, C> configs, GenerationStage.Decoration generationStage)
	{
		return new StructureRegistrar<C, S>(name, structure, pieceTypes, configs, generationStage);
	}

	/**
	 * Handy method so you don't have to type the generic type parameters.
	 *
	 * @param name
	 * @param structure
	 * @param pieceType
	 * @param config
	 * @param generationStage
	 * @return {@link StructureRegistrar}
	 */
	public static <C extends IFeatureConfig, S extends Structure<C>> StructureRegistrar<C, S> of(ResourceLocation name, S structure, Map<String, IStructurePieceType> pieceTypes, C config, GenerationStage.Decoration generationStage)
	{
		return new StructureRegistrar<C, S>(name, structure, pieceTypes, config, generationStage);
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
	 * Gets the map of {@link IStructurePieceType}s.
	 *
	 * @return {@link Map}
	 */
	public Map<String, IStructurePieceType> getPieceTypes()
	{
		return this.pieceTypes;
	}

	/**
	 * Returns the {@link IStructurePieceType} for the name passed.
	 *
	 * @param name
	 * @return {@link IStructurePieceType} or null if no object is present in the
	 * Map
	 */
	@Nullable
	public IStructurePieceType getPieceType(String name)
	{
		return this.pieceTypes.get(name);
	}

	/**
	 * Gets the {@link IStructurePieceType}. This is what you use in your
	 * {@link StructurePiece}. Use this if there's only one piece type.
	 *
	 * @return {@link IStructurePieceType}
	 */
	public IStructurePieceType getPieceType()
	{
		if (this.pieceTypes.size() > 0)
			return this.pieceTypes.get(this.pieceTypes.keySet().toArray()[0]);
		else
			return null;
	}

	/**
	 * Gets the map of {@link StructureFeature}s.
	 *
	 * @return {@link Map}
	 */
	public Map<String, StructureFeature<C, S>> getStructureFeatures()
	{
		return this.structureFeatures;
	}

	/**
	 * Returns the {@link StructureFeature} for the name passed.
	 *
	 * @param name
	 * @return {@link StructureFeature} or null if no object is present in the Map
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
		this.pieceTypes.forEach((name, pieceType) -> RegistryHelper.registerStructurePiece(name.isEmpty() ? this.name : new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "_" + name), pieceType));
		this.structureFeatures.forEach((name, feature) -> RegistryHelper.registerStructureFeature(name.isEmpty() ? this.name : new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "_" + name), feature));
		return this;
	}

	@Override
	public StructureRegistrar<C, S> handleForge(IForgeRegistry<Structure<?>> registry)
	{
		RegistryHelper.registerStructure(registry, this.name, this.structure, this.generationStage);
		return this;
	}
}

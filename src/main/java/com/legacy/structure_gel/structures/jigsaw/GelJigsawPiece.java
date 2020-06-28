package com.legacy.structure_gel.structures.jigsaw;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.structures.GelPlacementSettings;
import com.legacy.structure_gel.structures.GelStructurePiece;
import com.legacy.structure_gel.structures.GelTemplate;
import com.legacy.structure_gel.structures.processors.RemoveGelStructureProcessor;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Blocks;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.JigsawReplacementStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * Extension of {@link SingleJigsawPiece} without the
 * {@link BlockIgnoreStructureProcessor#AIR_AND_STRUCTURE_BLOCK} in favor of
 * {@link RemoveGelStructureProcessor#INSTANCE} and including a way to determine
 * interactions with water.
 * 
 * @author David
 *
 */
public class GelJigsawPiece extends SingleJigsawPiece
{
	public static final Codec<Either<ResourceLocation, Template>> POOL_CODEC = Codec.of(GelJigsawPiece::encodePool, ResourceLocation.field_240908_a_.map(Either::left));
	public static final Codec<GelJigsawPiece> CODEC = RecordCodecBuilder.create((instance) ->
	{
		return instance.group(encodeLocaiton(), encodeProcessor(), func_236848_d_(), Codec.BOOL.fieldOf("maintainWater").forGetter(jigsawPiece -> jigsawPiece.maintainWater), Codec.BOOL.fieldOf("ignoreEntities").forGetter(jigsawPiece -> jigsawPiece.ignoreEntities)).apply(instance, GelJigsawPiece::new);
	});
	private boolean maintainWater = true;
	private boolean ignoreEntities = false;

	private static <T> DataResult<T> encodePool(Either<ResourceLocation, Template> locationTemplate, DynamicOps<T> dyn, T data)
	{
		Optional<ResourceLocation> optional = locationTemplate.left();
		return !optional.isPresent() ? DataResult.error("Can not serialize a runtime pool element") : ResourceLocation.field_240908_a_.encode(optional.get(), dyn, data);
	}

	protected static <E extends GelJigsawPiece> RecordCodecBuilder<E, List<StructureProcessor>> encodeProcessor()
	{
		return IStructureProcessorType.field_237137_i_.listOf().fieldOf("processors").forGetter((jigsawPiece) ->
		{
			return jigsawPiece.processors;
		});
	}

	protected static <E extends GelJigsawPiece> RecordCodecBuilder<E, Either<ResourceLocation, Template>> encodeLocaiton()
	{
		return POOL_CODEC.fieldOf("location").forGetter((jigsawPiece) ->
		{
			return jigsawPiece.field_236839_c_;
		});
	}

	/**
	 * 
	 * @param location : the structure
	 * @param processors
	 * @param placementBehavior
	 * @param maintainWater
	 * @param ignoreEntities
	 */
	public GelJigsawPiece(Either<ResourceLocation, Template> location, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour placementBehavior, boolean maintainWater, boolean ignoreEntities)
	{
		super(location, processors, placementBehavior);
		this.maintainWater = maintainWater;
		this.ignoreEntities = ignoreEntities;
	}

	/**
	 * 
	 * @param location : the structure
	 * @param processors
	 * @param placementBehavior
	 */
	public GelJigsawPiece(Either<ResourceLocation, Template> location, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour placementBehavior)
	{
		super(location, processors, placementBehavior);
	}

	/**
	 * 
	 * @param template
	 * @param processors
	 * @param placementBehavior
	 */
	public GelJigsawPiece(Template template, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour placementBehavior)
	{
		super(template, processors, placementBehavior);
	}

	/**
	 * 
	 * @param location
	 * @param processors
	 * @param placementBehavior
	 * @param maintainWater
	 * @param ignoreEntities
	 */
	public GelJigsawPiece(ResourceLocation location, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour placementBehavior, boolean maintainWater, boolean ignoreEntities)
	{
		this(Either.left(location), processors, placementBehavior, maintainWater, ignoreEntities);
	}

	/**
	 * 
	 * @param location
	 * @param processors
	 * @param placementBehavior
	 */
	public GelJigsawPiece(ResourceLocation location, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour placementBehavior)
	{
		this(Either.left(location), processors, placementBehavior);
	}

	/**
	 * @see GelJigsawPiece
	 * @param location : the structure
	 * @param processors
	 */
	public GelJigsawPiece(ResourceLocation location, List<StructureProcessor> processors)
	{
		this(Either.left(location), processors, JigsawPattern.PlacementBehaviour.RIGID);
	}

	/**
	 * @see GelJigsawPiece
	 * @param location : the structure
	 */
	public GelJigsawPiece(ResourceLocation location)
	{
		this(location, ImmutableList.of());
	}

	/**
	 * Determins if blocks should become waterlogged when placed in water.
	 * 
	 * @param value
	 * @return {@link GelJigsawPiece}
	 */
	public GelJigsawPiece maintainWater(boolean value)
	{
		this.maintainWater = value;
		return this;
	}

	/**
	 * Prevents entities from generating with the structure. Not sure why you'd do
	 * this, but I'll allow it.
	 * 
	 * @param value
	 * @return {@link GelJigsawPiece}
	 */
	public GelJigsawPiece setIgnoreEntities(boolean value)
	{
		this.ignoreEntities = value;
		return this;
	}

	/**
	 * 
	 */
	@Override
	protected PlacementSettings func_230379_a_(Rotation rotation, MutableBoundingBox boundingBox, boolean isLegacy)
	{
		GelPlacementSettings placementSettings = new GelPlacementSettings();
		placementSettings.setMaintainWater(this.maintainWater);
		placementSettings.setBoundingBox(boundingBox);
		placementSettings.setRotation(rotation);
		placementSettings.func_215223_c(true);
		placementSettings.setIgnoreEntities(this.ignoreEntities);
		placementSettings.addProcessor(RemoveGelStructureProcessor.INSTANCE);
		if (!isLegacy)
			placementSettings.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
		this.processors.forEach(placementSettings::addProcessor);
		this.getPlacementBehaviour().getStructureProcessors().forEach(placementSettings::addProcessor);
		return placementSettings;
	}

	/**
	 * Changes how the place function works to allow for data structure blocks.
	 * 
	 * @param templateManager
	 * @param seedReader
	 * @param structureManager
	 * @param chunkGen
	 * @param pos
	 * @param pos2
	 * @param rotation
	 * @param bounds
	 * @param rand
	 * @param isLegacy
	 * @param gelStructurePiece
	 * @return
	 */
	public boolean place(TemplateManager templateManager, ISeedReader seedReader, StructureManager structureManager, ChunkGenerator chunkGen, BlockPos pos, BlockPos pos2, Rotation rotation, MutableBoundingBox bounds, Random rand, boolean isLegacy, GelStructurePiece gelStructurePiece)
	{
		Template template = this.getTemplate(templateManager);
		PlacementSettings placementSettings = this.func_230379_a_(rotation, bounds, isLegacy);
		if (!new GelTemplate(template).func_237146_a_(seedReader, pos, pos2, placementSettings, rand, 18))
		{
			return false;
		}
		else
		{
			for (Template.BlockInfo blockInfo : Template.processBlockInfos(seedReader, pos, pos2, placementSettings, this.getDataMarkers(templateManager, pos, rotation, false), template))
			{
				this.handleDataMarker(seedReader, blockInfo, pos, rotation, rand, bounds);
				if (blockInfo.nbt != null && seedReader.getBlockState(blockInfo.pos).getBlock() == Blocks.STRUCTURE_BLOCK)
				{
					StructureMode mode = StructureMode.valueOf(blockInfo.nbt.getString("mode"));
					if (mode == StructureMode.DATA)
					{
						gelStructurePiece.handleDataMarker(blockInfo.nbt.getString("metadata"), seedReader, blockInfo.pos, rand, bounds);
					}
				}
			}

			return true;
		}
	}

	/**
	 * Gets the resource location of this piece
	 * 
	 * @return {@link ResourceLocation}
	 */
	public ResourceLocation getLocation()
	{
		return this.field_236839_c_.left().get();
	}

	private Template getTemplate(TemplateManager templateManager)
	{
		return this.field_236839_c_.map(templateManager::getTemplateDefaulted, Function.identity());
	}

	/**
	 * 
	 */
	@Override
	public IJigsawDeserializer<?> getType()
	{
		return StructureGelMod.JigsawDeserializers.GEL_SINGLE_POOL_ELEMENT;
	}

	/**
	 * 
	 */
	@Override
	public String toString()
	{
		return "Gel[" + this.field_236839_c_ + "]";
	}
}

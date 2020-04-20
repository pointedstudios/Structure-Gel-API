package com.legacy.structure_gel.structures.jigsaw;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.structures.GelPlacementSettings;
import com.legacy.structure_gel.structures.processors.RemoveGelStructureProcessor;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.JigsawReplacementStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;

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
	private boolean maintainWater = true;
	private boolean ignoreEntities = false;

	/**
	 * @see GelJigsawPiece
	 * @param location : the structure
	 * @param processors
	 * @param placementBehavior
	 */
	public GelJigsawPiece(ResourceLocation location, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour placementBehavior)
	{
		super(location.toString(), processors, placementBehavior);
	}

	/**
	 * @see GelJigsawPiece
	 * @param location : the structure
	 * @param processors
	 */
	public GelJigsawPiece(ResourceLocation location, List<StructureProcessor> processors)
	{
		this(location, processors, JigsawPattern.PlacementBehaviour.RIGID);
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
	 * Used internally for deserialization
	 * 
	 * @param dyn
	 */
	public GelJigsawPiece(Dynamic<?> dyn)
	{
		super(dyn);
		this.maintainWater = dyn.get("maintainWater").asBoolean(true);
		this.ignoreEntities = dyn.get("ignoreEntities").asBoolean(false);
	}

	/**
	 * Determins if blocks should become waterlogged when placed in water.
	 * 
	 * @param value
	 * @return GelJigsawPiece
	 */
	public GelJigsawPiece setMaintainWater(boolean value)
	{
		this.maintainWater = value;
		return this;
	}

	/**
	 * Prevents entities from generating with the structure. Not sure why you'd do
	 * this, but I'll allow it.
	 * 
	 * @param value
	 * @return GelJigsawPiece
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
	protected PlacementSettings createPlacementSettings(Rotation rotation, MutableBoundingBox boundingBox)
	{
		GelPlacementSettings placementSettings = new GelPlacementSettings();
		placementSettings.setMaintainWater(this.maintainWater);
		placementSettings.setBoundingBox(boundingBox);
		placementSettings.setRotation(rotation);
		placementSettings.func_215223_c(true);
		placementSettings.setIgnoreEntities(this.ignoreEntities);
		placementSettings.addProcessor(RemoveGelStructureProcessor.INSTANCE);
		placementSettings.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
		this.processors.forEach(placementSettings::addProcessor);
		this.getPlacementBehaviour().getStructureProcessors().forEach(placementSettings::addProcessor);
		return placementSettings;
	}

	/**
	 * 
	 */
	@Override
	public IJigsawDeserializer getType()
	{
		return StructureGelMod.JigsawDeserializers.GEL_SINGLE_POOL_ELEMENT;
	}

	/**
	 * 
	 */
	@Override
	public <T> Dynamic<T> serialize0(DynamicOps<T> ops)
	{
		//@formatter:off
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(
				ops.createString("maintainWater"), ops.createBoolean(this.maintainWater), 
				ops.createString("ignoreEntities"), ops.createBoolean(this.ignoreEntities)
				)));
		//@formatter:on
	}

	/**
	 * 
	 */
	@Override
	public String toString()
	{
		return "Gel[" + this.location + "]";
	}
}

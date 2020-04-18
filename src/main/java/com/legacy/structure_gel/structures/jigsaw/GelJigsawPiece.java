package com.legacy.structure_gel.structures.jigsaw;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.legacy.structure_gel.structures.GelPlacementSettings;
import com.legacy.structure_gel.structures.processors.RemoveGelStructureProcessor;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.JigsawReplacementStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;

public class GelJigsawPiece extends SingleJigsawPiece
{
	private boolean maintainWater = true;
	
	public GelJigsawPiece(ResourceLocation location, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour placementBehavior)
	{
		super(location.toString(), processors, placementBehavior);
	}

	public GelJigsawPiece(ResourceLocation location, List<StructureProcessor> processors)
	{
		this(location, processors, JigsawPattern.PlacementBehaviour.RIGID);
	}

	public GelJigsawPiece(ResourceLocation location)
	{
		this(location, ImmutableList.of());
	}
	
	public GelJigsawPiece(String location, List<StructureProcessor> processors, JigsawPattern.PlacementBehaviour placementBehavior)
	{
		this(JigsawRegistryHelper.locatePiece(location), Streams.concat(processors.stream(), ImmutableList.of(RemoveGelStructureProcessor.INSTANCE).stream()).collect(ImmutableList.toImmutableList()), placementBehavior);
	}

	public GelJigsawPiece(String location, List<StructureProcessor> processors)
	{
		this(JigsawRegistryHelper.locatePiece(location), processors, JigsawPattern.PlacementBehaviour.RIGID);
	}

	public GelJigsawPiece(String location)
	{
		this(JigsawRegistryHelper.locatePiece(location), ImmutableList.of());
	}

	public GelJigsawPiece setMaintainWater(boolean value)
	{
		this.maintainWater = value;
		return this;
	}
	
	protected PlacementSettings createPlacementSettings(Rotation rotation, MutableBoundingBox boundingBox)
	{
		GelPlacementSettings placementSettings = new GelPlacementSettings();
		placementSettings.setMaintainWater(this.maintainWater);
		placementSettings.setBoundingBox(boundingBox);
		placementSettings.setRotation(rotation);
		placementSettings.func_215223_c(true);
		placementSettings.setIgnoreEntities(false);
		placementSettings.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
		this.processors.forEach(placementSettings::addProcessor);
		this.getPlacementBehaviour().getStructureProcessors().forEach(placementSettings::addProcessor);
		return placementSettings;
	}
}

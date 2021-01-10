package com.legacy.structure_gel.worldgen.jigsaw;

import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;
import com.legacy.structure_gel.worldgen.structure.IConfigStructure;
import com.mojang.serialization.Codec;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.structure.VillageConfig;

import java.util.List;
import java.util.Set;

/**
 * Extension of {@link GelJigsawStructure} for easy implementation of config
 * files using {@link StructureConfig}.
 *
 * @author David
 */
public abstract class GelConfigJigsawStructure extends GelJigsawStructure implements IConfigStructure
{
	private final StructureConfig config;

	public GelConfigJigsawStructure(Codec<VillageConfig> codec, StructureConfig config, int deltaY, boolean flag1, boolean placesOnSurface)
	{
		super(codec, deltaY, flag1, placesOnSurface);
		this.config = config;
		this.spawns.putAll(config.getSpawns());
	}

	@Override
	public StructureConfig getConfig()
	{
		return this.config;
	}

	@Override
	public double getProbability()
	{
		return this.getConfig().getProbability();
	}

	@Override
	public int getSpacing()
	{
		return this.getConfig().getSpacing();
	}

	@Override
	public int getOffset()
	{
		return this.getConfig().getOffset();
	}

	/**
	 * NO LONGER FUNCTIONS! Replaced with {@link #getValidDimensions()} as of 1.7.3
	 */
	@Override
	@Deprecated //TODO remove
	public List<DimensionSettings> getNoiseSettingsToGenerateIn()
	{
		return this.getConfig().getNoiseSettings() == null ? super.getNoiseSettingsToGenerateIn() : this.getConfig().getNoiseSettings();
	}
	
	@Override
	public Set<ResourceLocation> getValidDimensions()
	{
		return this.getConfig().getValidDimensions();
	}
}

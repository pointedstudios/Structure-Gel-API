package com.legacy.structure_gel.worldgen.structure;

import java.util.List;

import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;
import com.mojang.serialization.Codec;

import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;

/**
 * Extension of {@link GelStructure} for easy implementation of config files
 * using {@link StructureConfig}.
 * 
 * @author David
 *
 * @param <C>
 */
public abstract class GelConfigStructure<C extends IFeatureConfig> extends GelStructure<C> implements IConfigStructure
{
	private final StructureConfig config;

	public GelConfigStructure(Codec<C> codec, StructureConfig config)
	{
		super(codec);
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

	@Override
	public List<DimensionSettings> getNoiseSettingsToGenerateIn()
	{
		return this.getConfig().getNoiseSettings() == null ? super.getNoiseSettingsToGenerateIn() : this.getConfig().getNoiseSettings();
	}
}

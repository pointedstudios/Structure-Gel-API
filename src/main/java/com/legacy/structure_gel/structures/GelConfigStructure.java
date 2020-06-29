package com.legacy.structure_gel.structures;

import com.legacy.structure_gel.util.ConfigTemplates;
import com.mojang.serialization.Codec;

import net.minecraft.world.gen.feature.IFeatureConfig;

/**
 * Extension of {@link GelStructure} for easy implementation of config files.
 * 
 * @author David
 *
 * @param <C>
 */
public abstract class GelConfigStructure<C extends IFeatureConfig> extends GelStructure<C>
{
	private final ConfigTemplates.StructureConfigBuilder config;

	public GelConfigStructure(Codec<C> codec, ConfigTemplates.StructureConfigBuilder config)
	{
		super(codec);
		this.config = config;
	}

	@Override
	public double getProbability()
	{
		return this.config.getProbability();
	}

	@Override
	public int getSpacing()
	{
		return this.config.getSpacing();
	}

	@Override
	public int getOffset()
	{
		return this.config.getOffset();
	}
	
	public ConfigTemplates.StructureConfigBuilder getConfig()
	{
		return this.config;
	}
}

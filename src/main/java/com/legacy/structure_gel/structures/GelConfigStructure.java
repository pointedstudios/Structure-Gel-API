package com.legacy.structure_gel.structures;

import java.util.function.Function;

import com.legacy.structure_gel.ConfigTemplates;
import com.mojang.datafixers.Dynamic;

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
	private final ConfigTemplates.StructureConfig config;

	public GelConfigStructure(Function<Dynamic<?>, ? extends C> configFactoryIn, ConfigTemplates.StructureConfig config)
	{
		super(configFactoryIn);
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
	
	public ConfigTemplates.StructureConfig getConfig()
	{
		return this.config;
	}
}

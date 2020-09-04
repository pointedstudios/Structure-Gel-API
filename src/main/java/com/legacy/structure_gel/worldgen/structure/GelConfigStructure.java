package com.legacy.structure_gel.worldgen.structure;

import com.legacy.structure_gel.util.ConfigTemplates;
import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;
import com.mojang.serialization.Codec;

import net.minecraft.world.gen.feature.IFeatureConfig;

/**
 * Extension of {@link GelStructure} for easy implementation of config files using {@link StructureConfig}.
 * 
 * @author David
 *
 * @param <C>
 */
public abstract class GelConfigStructure<C extends IFeatureConfig> extends GelStructure<C> implements IConfigStructure
{
	private final ConfigTemplates.StructureConfig config;

	public GelConfigStructure(Codec<C> codec, ConfigTemplates.StructureConfig config)
	{
		super(codec);
		this.config = config;
		this.spawns.putAll(config.getSpawns());
	}

	public ConfigTemplates.StructureConfig getConfig()
	{
		return this.config;
	}
}

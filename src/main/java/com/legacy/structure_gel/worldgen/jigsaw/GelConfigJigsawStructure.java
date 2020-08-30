package com.legacy.structure_gel.worldgen.jigsaw;

import com.legacy.structure_gel.util.ConfigTemplates;
import com.mojang.serialization.Codec;

import net.minecraft.world.gen.feature.structure.VillageConfig;

public abstract class GelConfigJigsawStructure extends GelJigsawStructure
{
	private final ConfigTemplates.StructureConfig config;

	public GelConfigJigsawStructure(Codec<VillageConfig> codec, ConfigTemplates.StructureConfig config, int deltaY, boolean flag1, boolean flag2)
	{
		super(codec, deltaY, flag1, flag2);
		this.config = config;
		this.spawns.putAll(config.getSpawns());
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

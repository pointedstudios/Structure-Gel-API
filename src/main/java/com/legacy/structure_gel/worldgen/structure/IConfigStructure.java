package com.legacy.structure_gel.worldgen.structure;

import java.util.List;

import com.legacy.structure_gel.util.ConfigTemplates;

import net.minecraft.world.gen.DimensionSettings;

public interface IConfigStructure
{
	public ConfigTemplates.StructureConfig getConfig();

	default public double getProbability()
	{
		return this.getConfig().getProbability();
	}

	default public int getSpacing()
	{
		return this.getConfig().getSpacing();
	}

	default public int getOffset()
	{
		return this.getConfig().getOffset();
	}

	default public List<DimensionSettings> getNoiseSettingsToGenerateIn()
	{
		return this.getConfig().getNoiseSettings();
	}
}

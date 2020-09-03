package com.legacy.structure_gel.access_helpers;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;

public class DimensionAccessHelper
{
	public static DimensionSettings newDimensionSettings(DimensionStructuresSettings structures, NoiseSettings noise, BlockState defaultBlock, BlockState defaultFluid, int p_i231905_5_, int p_i231905_6_, int p_i231905_7_, boolean p_i231905_8_)
	{
		return new DimensionSettings(structures, noise, defaultBlock, defaultFluid, p_i231905_5_, p_i231905_6_, p_i231905_7_, p_i231905_8_);
	}

	public static DimensionSettings newFloatingIslandSettings(DimensionStructuresSettings p_242742_0_, BlockState p_242742_1_, BlockState p_242742_2_, ResourceLocation p_242742_3_, boolean p_242742_4_, boolean p_242742_5_)
	{
		return DimensionSettings.func_242742_a(p_242742_0_, p_242742_1_, p_242742_2_, p_242742_3_, p_242742_4_, p_242742_5_);
	}

	public static DimensionSettings newCavesSettings(DimensionStructuresSettings p_242741_0_, BlockState p_242741_1_, BlockState p_242741_2_, ResourceLocation p_242741_3_)
	{
		return DimensionSettings.func_242741_a(p_242741_0_, p_242741_1_, p_242741_2_, p_242741_3_);
	}

	public static DimensionSettings newAmplifiedSettings(DimensionStructuresSettings p_242743_0_, boolean p_242743_1_, ResourceLocation p_242743_2_)
	{
		return DimensionSettings.func_242743_a(p_242743_0_, p_242743_1_, p_242743_2_);
	}
}

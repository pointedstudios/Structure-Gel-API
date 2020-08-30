package com.legacy.dungeons_plus.features;

import com.legacy.dungeons_plus.DPProcessors;
import com.legacy.dungeons_plus.DungeonsPlus;
import com.legacy.structure_gel.worldgen.jigsaw.JigsawRegistryHelper;

import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;

public class SnowyTemplePools
{
	public static final JigsawPattern ROOT;

	public static void init()
	{
	}

	static
	{
		JigsawRegistryHelper registry = new JigsawRegistryHelper(DungeonsPlus.MODID, "snowy_temple/");

		ROOT = registry.register("temple/outside", registry.builder().names("temple/outside").processors(DPProcessors.ICE_TO_BLUE).build());
		registry.register("temple/inside", registry.builder().names("temple/inside_1", "temple/inside_2").processors(DPProcessors.ICE_TO_BLUE).build());
		registry.register("road", registry.builder().names("road_1", "road_2").build(), PlacementBehaviour.TERRAIN_MATCHING);
	}
}

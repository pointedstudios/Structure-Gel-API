package com.legacy.dungeons_plus.features;

import com.legacy.dungeons_plus.DungeonsPlus;
import com.legacy.structure_gel.worldgen.jigsaw.JigsawRegistryHelper;

import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

public class LeviathanPools
{
	public static final JigsawPattern ROOT;

	public static void init()
	{
	}

	static
	{
		JigsawRegistryHelper registry = new JigsawRegistryHelper(DungeonsPlus.MODID, "leviathan/");
		ROOT = registry.register("spine", registry.builder().names("spine_front_1", "spine_front_2").build());
		registry.register("spine_back", registry.builder().names("spine_back_1", "spine_back_2").build());
		registry.register("skull", registry.builder().names("skull_1", "skull_2").build());
		registry.register("tail", registry.builder().names("tail_1", "tail_2").build());
		registry.register("room", registry.builder().names("room").build());
	}
}
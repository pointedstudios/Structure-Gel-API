package com.legacy.dungeons_plus.features;

import com.legacy.dungeons_plus.DPProcessors;
import com.legacy.dungeons_plus.DungeonsPlus;
import com.legacy.structure_gel.worldgen.jigsaw.JigsawPoolBuilder;
import com.legacy.structure_gel.worldgen.jigsaw.JigsawRegistryHelper;

import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

public class BiggerDungeonPools
{
	public static final JigsawPattern ROOT;

	public static void init()
	{
	}

	static
	{
		JigsawRegistryHelper registry = new JigsawRegistryHelper(DungeonsPlus.MODID, "bigger_dungeon/");

		/**
		 * Check TowerPieces for information on the JigsawRegistryHelper and
		 * JigsawPoolBuilder.
		 * 
		 * Jigsaw structures generate at surface level by default. To prevent this, have
		 * whatever structure starts the generation have generateAtSurface set to false.
		 * You'll set the y level in the structure start like normal. In this case, it's
		 * in BiggerDungeonStructure.Start.init
		 */
		ROOT = registry.register("root", registry.builder().names("root").maintainWater(false).build());

		JigsawPoolBuilder basicPoolBuilder = registry.builder().maintainWater(false).processors(DPProcessors.COBBLE_TO_MOSSY);
		registry.register("main_room", basicPoolBuilder.clone().names("main_room").build());

		/**
		 * Since basicRooms is cloning from basicPoolBuilder, they will have the
		 * processor that converts some cobblestone to mossy cobble. Note: We have to
		 * clone it otherwise we end up changing the instance, which affects all
		 * builders associated with it.
		 */
		JigsawPoolBuilder basicRooms = basicPoolBuilder.clone().weight(8).names("side_room/skeleton", "side_room/zombie");
		JigsawPoolBuilder strayRoom = registry.builder().names("side_room/stray").maintainWater(false).processors(DPProcessors.COBBLE_TO_ICE);
		JigsawPoolBuilder huskRoom = registry.builder().names("side_room/husk").maintainWater(false).processors(DPProcessors.COBBLE_TO_TERRACOTTA);

		/**
		 * Using the JigsawPoolBuilder's collect method, I can merge as many pools as I
		 * want together. Doing this will maintain the input weights of each pool. In
		 * this instance, a normal room has skeleton and zombies with a weight of 8 and
		 * husks and strays with a weight of 1 each.
		 */
		registry.register("normal_room", JigsawPoolBuilder.collect(basicRooms, strayRoom, huskRoom));

		registry.register("special_room", JigsawPoolBuilder.collect(strayRoom, huskRoom));

	}
}

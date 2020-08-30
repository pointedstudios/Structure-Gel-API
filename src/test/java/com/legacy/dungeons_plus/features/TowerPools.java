package com.legacy.dungeons_plus.features;

import com.google.common.collect.ImmutableMap;
import com.legacy.dungeons_plus.DPProcessors;
import com.legacy.dungeons_plus.DungeonsPlus;
import com.legacy.structure_gel.worldgen.jigsaw.JigsawPoolBuilder;
import com.legacy.structure_gel.worldgen.jigsaw.JigsawRegistryHelper;

import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

public class TowerPools
{
	public static final JigsawPattern ROOT;

	public static void init()
	{
	}

	static
	{
		/**
		 * This registry is used in combination with JigsawPoolBuilders. The prefix
		 * ("tower/") is optional, but allows you to cut down some typing since your
		 * resource locations may need to start with it.
		 * 
		 * If you need to change the prefix, you can use .setPrefix on the registry to
		 * create a clone of it.
		 * 
		 * In the future, any string input using this registry will become a
		 * ResourceLocation with this format. modid:prefixtext ->
		 * dungeons_plus:tower/text
		 */
		JigsawRegistryHelper registry = new JigsawRegistryHelper(DungeonsPlus.MODID, "tower/");

		/**
		 * The JigsawRegistryHelper.register method takes a string for the name of the
		 * pool. This name takes the set modid and prefix into account. In this case,
		 * it'll end up being "structure_gel_demo:tower/root". This is the target pool
		 * in your jigsaw blocks. You could also just use a ResourceLocation directly
		 * instead of letting it fill in the repeated mod id and prefix.
		 *
		 * The second value is the JigsawPoolBuilder's built List<Pair<JigsawPiece,
		 * Integer>>. The "names" method tells the pool what options it has for
		 * structures to generate. These all take the mod id and prefix into account
		 * just like before. You can use ResourceLocations to handle the mod id and
		 * prefix yourself. You can also input a Map<String, Integer> to set weights.
		 *
		 * ...names(ImmutableMap.of("root", 1, "root_2", 9)) would generate "root_2" 90%
		 * of the time.
		 * 
		 * build() takes everything in the builder and compiles it into the List that
		 * the registry needs. Any settings like processors will take effect for all
		 * pieces passed in. This is done in the next line.
		 */
		ROOT = registry.register("root", registry.builder().names("root").build());

		/**
		 * Here we have an example of using more than one structure in the names()
		 * method. Since they are simply written in a list, they will all have the same
		 * weight.
		 * 
		 * This registry is told to not maintain water and to use the structure
		 * processors (cobbleToMossy and brickDecay) defined above.
		 */
		registry.register("floor", registry.builder().names("floor_spider", "floor_zombie", "floor_skeleton").maintainWater(false).processors(DPProcessors.TOWER_PROCESSOR).build());

		registry.register("floor_vex", registry.builder().names("floor_vex").maintainWater(false).processors(DPProcessors.TOWER_PROCESSOR).build());

		/**
		 * Just another way to get the JigsawPoolBuilder if you like doing things like
		 * this.
		 */
		registry.register("base", new JigsawPoolBuilder(registry).names("base").maintainWater(false).processors(DPProcessors.TOWER_PROCESSOR).build());

		/**
		 * Creating a JigsawPoolBuilder instance beforehand with shared settings that'll
		 * be used in the "top" pool.
		 */
		JigsawPoolBuilder topBuilder = registry.builder().maintainWater(false).processors(DPProcessors.COBBLE_TO_MOSSY);

		/**
		 * In this case, I'm using the JigsawPoolBuilder.collect to merge two different
		 * pool builders into one. Since both "top_full" and "top_decay" will use the
		 * same basic settings, but with "top_decay" having a gold decay processor, I
		 * use a copy of the JigsawPoolBuilder instance created beforehand. With these
		 * copies, I can adjust the settings for each builder while keeping commmon
		 * settings the same.
		 * 
		 * It's worth noting that processors() functions as an append, meaning that the
		 * "top_decay" structure will have both cobbleToMossy and goldDecay.
		 * 
		 * The weights of each structure set in names() are preserved, so set them as
		 * you normally would.
		 */
		registry.register("top", JigsawPoolBuilder.collect(topBuilder.clone().names(ImmutableMap.of("top_full", 1)), topBuilder.clone().names(ImmutableMap.of("top_decay", 4)).processors(DPProcessors.TOWER_GOLD_DECAY)));

	}
}

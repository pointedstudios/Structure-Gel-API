package com.legacy.dungeons_plus;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.util.RegistryHelper;
import com.legacy.structure_gel.worldgen.processors.RandomBlockSwapProcessor;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessorList;

public class DPProcessors
{
	public static final StructureProcessorList COBBLE_TO_MOSSY = register("cobble_to_mossy", new RandomBlockSwapProcessor(Blocks.COBBLESTONE, 0.2F, Blocks.MOSSY_COBBLESTONE));
	public static final StructureProcessorList COBBLE_TO_ICE = register("cobble_to_ice", new RandomBlockSwapProcessor(Blocks.COBBLESTONE, 0.2F, Blocks.PACKED_ICE));
	public static final StructureProcessorList COBBLE_TO_TERRACOTTA = register("cobble_to_terracotta", new RandomBlockSwapProcessor(Blocks.COBBLESTONE, 0.2F, Blocks.TERRACOTTA));
	public static final StructureProcessorList TOWER_PROCESSOR = register("tower_processor", RegistryHelper.combineProcessors(COBBLE_TO_MOSSY, ImmutableList.of(new RandomBlockSwapProcessor(Blocks.STONE_BRICKS, 0.15F, Blocks.MOSSY_STONE_BRICKS), new RandomBlockSwapProcessor(Blocks.STONE_BRICKS, 0.3F, Blocks.CRACKED_STONE_BRICKS))));
	public static final StructureProcessorList TOWER_GOLD_DECAY = register("tower_gold_decay", new RandomBlockSwapProcessor(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
	public static final StructureProcessorList ICE_TO_BLUE = register("ice_to_blue", new RandomBlockSwapProcessor(Blocks.PACKED_ICE, 0.07F, Blocks.BLUE_ICE));
	public static final StructureProcessorList END_RUINS_TOWER = register("end_ruins_tower", new RandomBlockSwapProcessor(Blocks.END_STONE_BRICKS, 0.1F, Blocks.END_STONE));

	private static StructureProcessorList register(String key, StructureProcessor processor)
	{
		return RegistryHelper.registerProcessor(DungeonsPlus.locate(key), processor);
	}

	private static StructureProcessorList register(String key, StructureProcessorList processorList)
	{
		return RegistryHelper.registerProcessor(DungeonsPlus.locate(key), processorList);
	}
}

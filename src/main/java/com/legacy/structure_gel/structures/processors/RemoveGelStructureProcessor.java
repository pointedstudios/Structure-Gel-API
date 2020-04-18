package com.legacy.structure_gel.structures.processors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.data.GelTags;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

public class RemoveGelStructureProcessor extends StructureProcessor
{
	public static final RemoveGelStructureProcessor INSTANCE = new RemoveGelStructureProcessor();
	public static final ImmutableList<Block> IGNORED_BLOCKS = ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK);
	
	@Nullable
	public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo existing, Template.BlockInfo placed, PlacementSettings settings)
	{
		if (placed.state.getBlock().isIn(GelTags.GEL))
		{
			if (pos.equals(new BlockPos(58146, 45, 60842)))
				System.out.println(existing.state.getBlock() + " -> "  + Blocks.AIR);
			return new Template.BlockInfo(placed.pos, Blocks.AIR.getDefaultState(), null);
		}
		else if (IGNORED_BLOCKS.contains(placed.state.getBlock()))
		{
			if (pos.equals(new BlockPos(58146, 45, 60842)))
				System.out.println(existing.state.getBlock() + " -> null");
			return null;
		}
		else 
		{
			if (pos.equals(new BlockPos(58146, 45, 60842)))
				System.out.println(existing.state.getBlock() + " -> " + placed.state.getBlock());
			return placed;
		}
		//return (placed.state.getBlock() == SkiesBlocks.structure_filler) ? new Template.BlockInfo(placed.pos, Blocks.AIR.getDefaultState(), null) : (IGNORED_BLOCKS.contains(placed.state.getBlock()) ? null : placed);
	}

	protected IStructureProcessorType getType()
	{
		return StructureGelMod.Processors.REMOVE_FILLER;
	}

	protected <T> Dynamic<T> serialize0(DynamicOps<T> ops)
	{
		return new Dynamic<>(ops, ops.emptyMap());
	}

}

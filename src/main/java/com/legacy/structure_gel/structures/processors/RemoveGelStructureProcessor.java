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

/**
 * Ignores {@link Blocks#AIR} and {@link Blocks#STRUCTURE_BLOCK}, and replaces
 * structure gel blocks with air. Great to make sure the insides of things don't
 * have unintended things inside of them, and to make it easier to blend into
 * the world.
 * 
 * @author David
 *
 */
public class RemoveGelStructureProcessor extends StructureProcessor
{
	public static final RemoveGelStructureProcessor INSTANCE = new RemoveGelStructureProcessor();
	public static final ImmutableList<Block> IGNORED_BLOCKS = ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK);

	@Nullable
	public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo existing, Template.BlockInfo placed, PlacementSettings settings)
	{
		if (placed.state.getBlock().isIn(GelTags.GEL))
			return new Template.BlockInfo(placed.pos, Blocks.AIR.getDefaultState(), null);
		else if (IGNORED_BLOCKS.contains(placed.state.getBlock()))
			return null;
		else
			return placed;
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

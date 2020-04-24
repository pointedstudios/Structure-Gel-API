package com.legacy.structure_gel.structures.processors;

import javax.annotation.Nullable;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.data.GelTags;
import com.legacy.structure_gel.structures.GelStructurePiece;
import com.legacy.structure_gel.structures.jigsaw.GelJigsawPiece;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

/**
 * Functions similar to
 * {@link BlockIgnoreStructureProcessor#AIR_AND_STRUCTURE_BLOCK} but also
 * replaces blocks tagged as {@link GelTags#GEL} with air. Structure blocks are
 * not ignored as they can be used in jigsaw structures with
 * {@link GelStructurePiece} Great to make sure the insides of structures don't
 * have unintended blocks inside of them, and to make it easier to blend them
 * into the world.<br>
 * <br>
 * This processor is automatically added to anything using
 * {@link GelJigsawPiece}.
 * 
 * @author David
 *
 */
public class RemoveGelStructureProcessor extends StructureProcessor
{
	/**
	 * @see RemoveGelStructureProcessor
	 */
	public static final RemoveGelStructureProcessor INSTANCE = new RemoveGelStructureProcessor();

	/**
	 * 
	 */
	@Nullable
	public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo existing, Template.BlockInfo placed, PlacementSettings settings)
	{
		if (placed.state.getBlock().isIn(GelTags.GEL))
			return new Template.BlockInfo(placed.pos, Blocks.AIR.getDefaultState(), null);
		else if (placed.state.getBlock() == Blocks.AIR)
			return null;
		else
			return placed;
	}

	/**
	 * 
	 */
	protected IStructureProcessorType getType()
	{
		return StructureGelMod.Processors.REMOVE_FILLER;
	}

	/**
	 * 
	 */
	protected <T> Dynamic<T> serialize0(DynamicOps<T> ops)
	{
		return new Dynamic<>(ops, ops.emptyMap());
	}
}

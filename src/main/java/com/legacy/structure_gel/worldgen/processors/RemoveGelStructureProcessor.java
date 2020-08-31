package com.legacy.structure_gel.worldgen.processors;

import javax.annotation.Nullable;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.data.GelTags;
import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.worldgen.jigsaw.GelJigsawPiece;
import com.legacy.structure_gel.worldgen.jigsaw.GelStructurePiece;
import com.mojang.serialization.Codec;

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
	public static final RemoveGelStructureProcessor INSTANCE = new RemoveGelStructureProcessor();	
	public static final Codec<RemoveGelStructureProcessor> CODEC = Codec.unit(() -> INSTANCE);

	/**
	 * 
	 */
	@Nullable
	@Internal
	@Override
	public Template.BlockInfo func_230386_a_(IWorldReader worldReaderIn, BlockPos pos, BlockPos pos2, Template.BlockInfo existing, Template.BlockInfo placed, PlacementSettings settings)
	{
		if (placed.state.getBlock().isIn(GelTags.GEL))
			return new Template.BlockInfo(placed.pos, Blocks.AIR.getDefaultState(), null);
		return placed.state.getBlock() == Blocks.AIR ? null : placed;
	}

	/**
	 * 
	 */
	@Internal
	@Override
	protected IStructureProcessorType<?> getType()
	{
		return StructureGelMod.Processors.REMOVE_FILLER;
	}
}

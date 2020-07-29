package com.legacy.structure_gel.structures.processors;

import java.util.Random;

import javax.annotation.Nullable;

import com.legacy.structure_gel.StructureGelMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

/**
 * Shorthand way of creating a structure processor to randomly replace some
 * blocks.
 * 
 * @author David
 *
 */
public class RandomStateSwapProcessor extends StructureProcessor
{
	public static final Codec<RandomStateSwapProcessor> CODEC = RecordCodecBuilder.create((instance) ->
	{
		return instance.group(BlockState.BLOCKSTATE_CODEC.fieldOf("condition").forGetter(processor ->
		{
			return processor.condition;
		}), Codec.FLOAT.fieldOf("chance").forGetter(processor ->
		{
			return processor.chance;
		}), BlockState.BLOCKSTATE_CODEC.fieldOf("change_to").forGetter(processor ->
		{
			return processor.changeTo;
		})).apply(instance, RandomStateSwapProcessor::new);
	});
	
	private final BlockState condition;
	private final float chance;
	private final BlockState changeTo;

	/**
	 * @param condition : the block state to change
	 * @param chance : expressed as a percentage. 0.1F = 10%
	 * @param changeTo : the BlockState to change "condition" to when the chance is
	 *            true
	 */
	public RandomStateSwapProcessor(BlockState condition, float chance, BlockState changeTo)
	{
		this.condition = condition;
		this.chance = chance;
		this.changeTo = changeTo;
	}

	/**
	 * 100% chance to swap
	 * 
	 * @param condition
	 * @param changeTo
	 */
	public RandomStateSwapProcessor(BlockState condition, BlockState changeTo)
	{
		this(condition, 1F, changeTo);
	}

	/**
	 * Assumes changeTo uses the default state.
	 * 
	 * @param condition
	 * @param chance
	 * @param changeTo
	 */
	public RandomStateSwapProcessor(BlockState condition, float chance, Block changeTo)
	{
		this(condition, chance, changeTo.getDefaultState());
	}

	/**
	 * 100% chance to swap. Assumes changeTo uses the default state.
	 * 
	 * @param condition
	 * @param changeTo
	 */
	public RandomStateSwapProcessor(BlockState condition, Block changeTo)
	{
		this(condition, changeTo.getDefaultState());
	}

	/**
	 * 
	 */
	@Nullable
	@Override
	public Template.BlockInfo func_230386_a_(IWorldReader worldReaderIn, BlockPos pos, BlockPos pos2, Template.BlockInfo existing, Template.BlockInfo placed, PlacementSettings settings)
	{
		if (placed.state == this.condition && (this.chance == 1F || new Random(MathHelper.getPositionRandom(placed.pos)).nextFloat() < this.chance))
			return new Template.BlockInfo(placed.pos, changeTo, null);
		return placed;
	}

	/**
	 * 
	 */
	@Override
	protected IStructureProcessorType<?> getType()
	{
		return StructureGelMod.Processors.REPLACE_BLOCK;
	}
}

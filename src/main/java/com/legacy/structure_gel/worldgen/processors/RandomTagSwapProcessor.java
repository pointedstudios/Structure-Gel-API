package com.legacy.structure_gel.worldgen.processors;

import com.legacy.structure_gel.SGRegistry.Processors;
import com.legacy.structure_gel.util.Internal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Shorthand way of creating a structure processor to randomly replace some
 * blocks.
 *
 * @author David
 */
public class RandomTagSwapProcessor extends StructureProcessor
{
	public static final Codec<RandomTagSwapProcessor> CODEC = RecordCodecBuilder.create((instance) ->
	{
		return instance.group(ITag.getTagCodec(() -> TagCollectionManager.getManager().getBlockTags()).fieldOf("condition").forGetter(processor ->
		{
			return processor.condition;
		}), Codec.FLOAT.fieldOf("chance").forGetter(processor ->
		{
			return processor.chance;
		}), BlockState.CODEC.fieldOf("change_to").forGetter(processor ->
		{
			return processor.changeTo;
		})).apply(instance, RandomTagSwapProcessor::new);
	});

	public final ITag<Block> condition;
	public final float chance;
	public final BlockState changeTo;

	/**
	 * @param condition : the tag to change
	 * @param chance    : expressed as a percentage. 0.1F = 10%
	 * @param changeTo  : the BlockState to change "condition" to when the chance is
	 *                  true
	 */
	public RandomTagSwapProcessor(ITag<Block> condition, float chance, BlockState changeTo)
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
	public RandomTagSwapProcessor(ITag<Block> condition, BlockState changeTo)
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
	public RandomTagSwapProcessor(ITag<Block> condition, float chance, Block changeTo)
	{
		this(condition, chance, changeTo.getDefaultState());
	}

	/**
	 * 100% chance to swap. Assumes changeTo uses the default state.
	 *
	 * @param condition
	 * @param changeTo
	 */
	public RandomTagSwapProcessor(Tag<Block> condition, Block changeTo)
	{
		this(condition, changeTo.getDefaultState());
	}

	/**
	 *
	 */
	@Nullable
	@Internal
	@Override
	public Template.BlockInfo func_230386_a_(IWorldReader worldReaderIn, BlockPos pos, BlockPos pos2, Template.BlockInfo existing, Template.BlockInfo placed, PlacementSettings settings)
	{
		if (placed.state.getBlock().isIn(this.condition) && (this.chance == 1F || new Random(MathHelper.getPositionRandom(placed.pos)).nextFloat() < this.chance))
			return new Template.BlockInfo(placed.pos, changeTo, null);
		return placed;
	}

	/**
	 *
	 */
	@Internal
	@Override
	protected IStructureProcessorType<?> getType()
	{
		return Processors.REPLACE_TAG;
	}
}

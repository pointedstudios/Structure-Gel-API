package com.legacy.structure_gel.structures.processors;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.legacy.structure_gel.StructureGelMod;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
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
public class RandomTagSwapProcessor extends StructureProcessor
{
	private final Tag<Block> condition;
	private final float chance;
	private final BlockState changeTo;

	/**
	 * @param condition : the tag to change
	 * @param chance : expressed as a percentage. 0.1F = 10%
	 * @param changeTo : the BlockState to change "condition" to when the chance is
	 *            true
	 */
	public RandomTagSwapProcessor(Tag<Block> condition, float chance, BlockState changeTo)
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
	public RandomTagSwapProcessor(Tag<Block> condition, BlockState changeTo)
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
	public RandomTagSwapProcessor(Tag<Block> condition, float chance, Block changeTo)
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
	 * @see #RandomTagSwapProcessor(Tag, float, BlockState)
	 * @param dyn
	 */
	public RandomTagSwapProcessor(Dynamic<?> dyn)
	{
		this.condition = BlockTags.getCollection().get(new ResourceLocation(dyn.get("condition").asString("")));
		this.chance = dyn.get("chance").asFloat(0.0F);
		this.changeTo = BlockState.deserialize(dyn.get("change_to").orElseEmptyMap());
	}

	/**
	 * 
	 */
	@Nullable
	public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo existing, Template.BlockInfo placed, PlacementSettings settings)
	{
		if (placed.state.getBlock().isIn(this.condition) && (this.chance == 1F || new Random(MathHelper.getPositionRandom(placed.pos)).nextFloat() < this.chance))
			return new Template.BlockInfo(placed.pos, changeTo, null);
		return placed;
	}

	/**
	 * 
	 */
	protected IStructureProcessorType getType()
	{
		return StructureGelMod.Processors.REPLACE_TAG;
	}

	/**
	 * 
	 */
	protected <T> Dynamic<T> serialize0(DynamicOps<T> ops)
	{
		//@formatter:off
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(
				ops.createString("condition"), ops.createString(this.condition.getId().toString()),
				ops.createString("chance"), ops.createFloat(this.chance),
				ops.createString("change_to"), BlockState.serialize(ops, changeTo).getValue()
				)));
		//@formatter:on
	}
}

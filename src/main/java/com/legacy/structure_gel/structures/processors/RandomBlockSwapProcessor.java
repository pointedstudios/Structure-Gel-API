package com.legacy.structure_gel.structures.processors;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.legacy.structure_gel.StructureGelMod;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Shorthand way of creating a structure processor to randomly replace some
 * blocks.
 * 
 * @author David
 *
 */
public class RandomBlockSwapProcessor extends StructureProcessor
{
	private final Block condition;
	private final float chance;
	private final BlockState changeTo;

	public RandomBlockSwapProcessor(Block condition, float chance, BlockState changeTo)
	{
		this.condition = condition;
		this.chance = chance;
		this.changeTo = changeTo;
	}

	public RandomBlockSwapProcessor(Dynamic<?> dyn)
	{
		this.condition = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(dyn.get("condition").asString("minecraft:air")));
		this.chance = dyn.get("chance").asFloat(0.0F);
		this.changeTo = BlockState.deserialize(dyn.get("changeTo").orElseEmptyMap());
	}

	@Nullable
	public Template.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, Template.BlockInfo existing, Template.BlockInfo placed, PlacementSettings settings)
	{
		if (new Random(MathHelper.getPositionRandom(placed.pos)).nextFloat() < this.chance)
			return new Template.BlockInfo(placed.pos, changeTo, null);
		return placed;
	}

	protected IStructureProcessorType getType()
	{
		return StructureGelMod.Processors.REPLACE_RANDOMLY;
	}

	protected <T> Dynamic<T> serialize0(DynamicOps<T> ops)
	{
		//@formatter:off
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(
				ops.createString("condition"), ops.createString(this.condition.getRegistryName().toString()),
				ops.createString("chance"), ops.createFloat(this.chance),
				ops.createString("change_to"), BlockState.serialize(ops, changeTo).getValue()
				)));
		//@formatter:on
	}
}

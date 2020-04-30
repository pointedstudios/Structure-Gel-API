package com.legacy.structure_gel.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This extension of {@link StructureGelBlock} contains a blockstate for its
 * axis, and it uses it to only spread in a flat plane.
 * 
 * @author David
 *
 */
public class AxisStructureGelBlock extends StructureGelBlock
{
	public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

	public AxisStructureGelBlock(Behavior... behaviors)
	{
		super(behaviors);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		super.fillStateContainer(builder);
		builder.add(AXIS);
	}

	/**
	 * Using the hook to set the axis of this gel based on the look vector of the
	 * player, similar to pistons.
	 */
	@Override
	public BlockState onHandPlaceHook(BlockItemUseContext context)
	{
		return this.getDefaultState().with(COUNT, context.getPlayer().isSneaking() ? 0 : 50).with(AXIS, context.getNearestLookingDirection().getAxis());
	}

	/**
	 * Spreads the gel along a flat plane and then cancels the normal spread action
	 * in
	 * {@link #tick(BlockState, net.minecraft.world.server.ServerWorld, BlockPos, Random)}.
	 */
	@Override
	public boolean spreadHookPre(BlockState state, World worldIn, BlockPos pos, Random random)
	{
		if (this.behaviors.contains(Behavior.AXIS_SPREAD))
		{
			for (Direction d : Direction.values())
			{
				if (d.getAxis() != state.get(AXIS))
				{
					BlockPos offset = pos.offset(d);
					addGel(state, worldIn, offset, state.get(COUNT) + 1);
				}
			}
			setGel(state, worldIn, pos, 50);

			return false;
		}
		return true;
	}
}
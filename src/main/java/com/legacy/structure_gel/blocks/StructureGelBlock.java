package com.legacy.structure_gel.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StructureGelBlock extends Block
{
	// 0-49 spreads, 50 does nothing, 51 deletes
	private static final IntegerProperty COUNT = IntegerProperty.create("count", 0, 51);

	public StructureGelBlock()
	{
		super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0F).noDrops());
		this.setDefaultState(this.getDefaultState().with(COUNT, 50));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		if (context.getPlayer().isCreative())
			return this.getDefaultState().with(COUNT, context.getPlayer().isSneaking() ? 0 : 50);
		else
			return Blocks.AIR.getDefaultState();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
	}

	@Override
	public void tick(BlockState state, World worldIn, BlockPos pos, Random random)
	{
		if (state.get(COUNT) < 50)
		{
			for (Direction d : Direction.values())
			{
				BlockPos offset = pos.offset(d);
				if (worldIn.isAirBlock(offset))
				{
					// TODO Temporary avoid sky. Stops when it would expose itself to the air
					if (worldIn.getLightFor(LightType.SKY, offset) < 15)
					{
						worldIn.setBlockState(offset, this.getDefaultState().with(COUNT, state.get(COUNT) + 1));
						worldIn.getPendingBlockTicks().scheduleTick(offset, worldIn.getBlockState(offset).getBlock(), 2);
					}
				}
			}
			worldIn.setBlockState(pos, this.getDefaultState().with(COUNT, 50));
		}
		else if (state.get(COUNT) == 51)
		{
			for (Direction d : Direction.values())
			{
				BlockPos offset = pos.offset(d);
				if (worldIn.getBlockState(offset).getBlock() == this)
				{
					worldIn.setBlockState(offset, this.getDefaultState().with(COUNT, 51));
					worldIn.getPendingBlockTicks().scheduleTick(offset, worldIn.getBlockState(offset).getBlock(), 2);
				}
			}
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		if (player.isCreative() && player.getHeldItem(handIn).getItem() == Items.GUNPOWDER)
		{
			worldIn.setBlockState(pos, state.with(COUNT, 51));
			worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
			return true;
		}
		return false;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (!player.isCreative())
		{
			for (Direction d : Direction.values())
			{
				BlockPos offset = pos.offset(d);
				if (worldIn.getBlockState(offset).getBlock() == this)
				{
					worldIn.setBlockState(offset, state.with(COUNT, 51));
					worldIn.getPendingBlockTicks().scheduleTick(offset, this, 2);
				}
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(COUNT);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return VoxelShapes.empty();
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean isSolid(BlockState state)
	{
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return adjacentBlockState.getBlock() == this;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return 1.0F;
	}

	@Override
	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type)
	{
		return false;
	}
}

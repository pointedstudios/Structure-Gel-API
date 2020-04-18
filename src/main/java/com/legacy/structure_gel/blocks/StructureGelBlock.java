package com.legacy.structure_gel.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.legacy.structure_gel.data.GelTags;

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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StructureGelBlock extends Block
{
	// 0-49 spreads, 50 does nothing, 51 deletes
	private static final IntegerProperty COUNT = IntegerProperty.create("count", 0, 51);
	private final boolean diagonalSpread;
	private final boolean photosensitive;

	public StructureGelBlock(boolean diagonalSpread, boolean photosensitive)
	{
		super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0F).noDrops());
		this.diagonalSpread = diagonalSpread;
		this.photosensitive = photosensitive;

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
				addGel(worldIn, offset, state.get(COUNT) + 1);
				if (this.diagonalSpread)
				{
					if (d == Direction.UP || d == Direction.DOWN)
						for (int i = 0; i < 4; i++)
							addGel(worldIn, offset.offset(Direction.byHorizontalIndex(i)), state.get(COUNT) + 1);
					else
						addGel(worldIn, offset.offset(d.rotateY()), state.get(COUNT) + 1);
				}
			}
			setGel(worldIn, pos, 50);
		}
		else if (state.get(COUNT) == 51)
		{
			for (Direction d : Direction.values())
			{
				BlockPos offset = pos.offset(d);
				removeGel(worldIn, offset);
				if (this.diagonalSpread)
				{
					if (d == Direction.UP || d == Direction.DOWN)
						for (int i = 0; i < 4; i++)
							removeGel(worldIn, offset.offset(Direction.byHorizontalIndex(i)));
					else
						removeGel(worldIn, offset.offset(d.rotateY()));
				}
			}
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	/**
	 * When right clicked with gunpowder, a chain reaction starts destroying all
	 * connected gels.
	 */
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		if (player.isCreative() && player.getHeldItem(handIn).getItem() == Items.GUNPOWDER)
		{
			removeGel(worldIn, pos);
			return true;
		}
		return false;
	}

	/**
	 * Triggers a chain break reaction if broken in survival mode in case a survival
	 * player finds it.
	 */
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (!player.isCreative())
		{
			for (Direction d : Direction.values())
			{
				BlockPos offset = pos.offset(d);
				if (worldIn.getBlockState(offset).getBlock() == this)
					removeGel(worldIn, offset);
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	public void addGel(World worldIn, BlockPos pos, int count)
	{
		if (worldIn.isAirBlock(pos) && checkAbove(worldIn, pos))
			setGel(worldIn, pos, count);
	}
	
	public void removeGel(World worldIn, BlockPos pos)
	{
		if (worldIn.getBlockState(pos).getBlock() == this)
			setGel(worldIn, pos, 51);
	}
	
	public void setGel(World worldIn, BlockPos pos, int count)
	{
		worldIn.setBlockState(pos, this.getDefaultState().with(COUNT, count));
		worldIn.getPendingBlockTicks().scheduleTick(pos, worldIn.getBlockState(pos).getBlock(), 2);
	}

	/**
	 * Checks to see if the blocks above this are either air or gel. Returns false
	 * if there's anything else above.
	 * 
	 * @param worldIn
	 * @param pos
	 * @return boolean
	 */
	public boolean checkAbove(World worldIn, BlockPos pos)
	{
		if (!this.photosensitive)
			return true;
		else
		{
			if (!worldIn.isSkyLightMax(pos))
				return true;
			for (int dy = 1; pos.up(dy).getY() < 256; dy++)
				if (!(worldIn.isAirBlock(pos.up(dy)) || worldIn.getBlockState(pos.up(dy)).isIn(GelTags.GEL)))
					return true;
			return false;
		}
	}

	@Override
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

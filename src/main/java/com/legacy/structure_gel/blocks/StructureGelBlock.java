package com.legacy.structure_gel.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A block that spreads out from where it's placed. Sneak while placing to
 * trigger this behavior. Right click with gunpowder to cause a chain reaction
 * that removes all gels connected. Some methods contain hooks in
 * {@link IStructureGel} that you can override for custom behavior. All methods
 * containing hooks are documented with "@see"
 * 
 * @author David
 *
 */
public class StructureGelBlock extends Block implements IStructureGel
{
	/**
	 * 0-49 causes spreading, 50 does nothing, and 51 causes it to remove itself.
	 */
	public static final IntegerProperty COUNT = IntegerProperty.create("count", 0, 51);
	/**
	 * List of behaviors that this block will use.
	 * 
	 * @see Behavior
	 */
	public final List<IBehavior> behaviors;

	/**
	 * @param behaviors
	 * @see Behavior
	 * @see StructureGelBlock
	 */
	public StructureGelBlock(IBehavior... behaviors)
	{
		super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0F).noDrops().notSolid().sound(SoundType.SLIME));
		this.behaviors = ImmutableList.copyOf(behaviors);

		this.setDefaultState(this.getDefaultState().with(COUNT, 50));
	}

	/**
	 * Ensures that only creative players can place this block.
	 * 
	 * @see IStructureGel#onHandPlaceHook(BlockItemUseContext)
	 */
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		if (context.getPlayer().isCreative())
		{
			BlockState hookState = this.onHandPlaceHook(context);
			if (hookState != null)
				return hookState;

			int count = 50;
			if (context.getPlayer().isSneaking())
			{
				count = 0;
				if (this.behaviors.contains(Behavior.DYNAMIC_SPREAD_DIST))
					count = Math.min(Math.max(50 - context.getItem().getCount(), 0), 50);
			}
			return this.getDefaultState().with(COUNT, count);
		}
		else
			return Blocks.AIR.getDefaultState();
	}

	/**
	 * 
	 */
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
	}

	/**
	 * Called on block update to either spread or remove the structure gel.
	 * 
	 * @see IStructureGel#spreadHookPre(BlockState, World, BlockPos, Random)
	 * @see IStructureGel#spreadHookPost(BlockState, World, BlockPos, Random)
	 * @see IStructureGel#removalHookPre(BlockState, World, BlockPos, Random)
	 * @see IStructureGel#removalHookPost(BlockState, World, BlockPos, Random)
	 * @see #addGel(World, BlockPos, int)
	 * @see #removeGel(World, BlockPos)
	 */
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
	{
		if (state.get(COUNT) < 50)
		{
			if (!this.spreadHookPre(state, worldIn, pos, random))
				return;
			for (Direction d : Direction.values())
			{
				BlockPos offset = pos.offset(d);
				addGel(state, worldIn, offset, state.get(COUNT) + 1);
				if (this.behaviors.contains(Behavior.DIAGONAL_SPREAD))
				{
					if (d == Direction.UP || d == Direction.DOWN)
						for (int i = 0; i < 4; i++)
							addGel(state, worldIn, offset.offset(Direction.byHorizontalIndex(i)), state.get(COUNT) + 1);
					else
						addGel(state, worldIn, offset.offset(d.rotateY()), state.get(COUNT) + 1);
				}
			}
			setGel(state, worldIn, pos, 50);
			this.spreadHookPost(state, worldIn, pos, random);
		}
		else if (state.get(COUNT) == 51)
		{
			if (!this.removalHookPre(state, worldIn, pos, random))
				return;
			for (Direction d : Direction.values())
			{
				BlockPos offset = pos.offset(d);
				removeGel(state, worldIn, offset);
				if (this.behaviors.contains(Behavior.DIAGONAL_SPREAD))
				{
					if (d == Direction.UP || d == Direction.DOWN)
						for (int i = 0; i < 4; i++)
							removeGel(state, worldIn, offset.offset(Direction.byHorizontalIndex(i)));
					else
						removeGel(state, worldIn, offset.offset(d.rotateY()));
				}
			}
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			this.removalHookPost(state, worldIn, pos, random);
		}
	}

	/**
	 * When right clicked with gunpowder, a chain reaction starts destroying all
	 * connected gels.
	 */
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		if (player.isCreative() && player.getHeldItem(handIn).getItem() == Items.GUNPOWDER)
		{
			removeGel(state, worldIn, pos);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
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
					removeGel(state, worldIn, offset);
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	/**
	 * Called when adding structure gel to the world. This method contains checks to
	 * ensure that it can place the gel before actually placing it.
	 * 
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param count
	 * 
	 * @see IStructureGel#checkPlacementHook(World, BlockPos, int)
	 * @see StructureGelBlock#setGel(World, BlockPos, int)
	 */
	public void addGel(BlockState state, World worldIn, BlockPos pos, int count)
	{
		if (worldIn.isAirBlock(pos) && checkAbove(worldIn, pos) && this.checkPlacementHook(worldIn, pos, count))
			setGel(state, worldIn, pos, count);
	}

	/**
	 * Called when removing structure gel from the world from the gunpowder chain
	 * reaction. This method contains checks to make sure it's only removing
	 * structure gel blocks from the world.
	 * 
	 * @param state
	 * @param worldIn
	 * @param pos
	 * 
	 * @see StructureGelBlock#setGel(World, BlockPos, int)
	 */
	public void removeGel(BlockState state, World worldIn, BlockPos pos)
	{
		if (worldIn.getBlockState(pos).getBlock() == this)
			setGel(state, worldIn, pos, 51);
	}

	/**
	 * Places structure gel with the proper state and then schedules a block update
	 * on it to continue the spread.
	 * 
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param count
	 */
	public void setGel(BlockState state, World worldIn, BlockPos pos, int count)
	{
		worldIn.setBlockState(pos, state.with(COUNT, count));
		worldIn.getPendingBlockTicks().scheduleTick(pos, worldIn.getBlockState(pos).getBlock(), 2);
	}

	/**
	 * Checks to see if the blocks above this are either air or gel. Returns false
	 * if there's anything else above.
	 * 
	 * @param worldIn
	 * @param pos
	 * @return boolean {@link HuskEntity}
	 */
	public boolean checkAbove(World worldIn, BlockPos pos)
	{
		if (!this.behaviors.contains(Behavior.PHOTOSENSITIVE))
			return true;
		else
		{
			if (!worldIn.canSeeSky(pos))
				return true;
			for (int dy = 1; pos.up(dy).getY() < 256; dy++)
				if (!worldIn.isAirBlock(pos.up(dy)))
					return true;
			return false;
		}
	}

	/**
	 * 
	 */
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(COUNT);
	}

	/**
	 * 
	 */
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return VoxelShapes.empty();
	}

	/**
	 * 
	 */
	@Override
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return true;
	}

	/**
	 * 
	 */
	@Override
	public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return false;
	}

	/**
	 * 
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return adjacentBlockState.getBlock() == this;
	}

	/**
	 * 
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return 1.0F;
	}

	/**
	 * 
	 */
	@Override
	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type)
	{
		return false;
	}
}

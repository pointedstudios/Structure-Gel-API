package com.legacy.structure_gel.util;

import com.legacy.structure_gel.blocks.GelPortalBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.PortalSize;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A version of {@link PortalSize} to work with {@link GelPortalBlock} and
 * {@link GelTeleporter}. Call {@link #isValidSize()} to check if you're in a
 * portal frame or
 * {@link #trySpawnPortal(IWorld, BlockPos, GelPortalBlock, List)} to attempt to
 * spawn a portal at the position.
 *
 * @author David
 */
public class GelPortalSize
{
	public final Block frame;
	public final GelPortalBlock portal;
	public final IWorld world;
	public final Direction.Axis axis;
	public final Direction rightDir;
	public final Direction leftDir;
	public int portalBlockCount;
	@Nullable
	public BlockPos bottomLeft;
	public int height;
	public int width;
	public List<Block> allowedBlocks = new ArrayList<>();

	public GelPortalSize(IWorld world, BlockPos pos, Direction.Axis axis, Block frame, GelPortalBlock portal, List<Block> allowedBlocks)
	{
		this.world = world;
		this.axis = axis;
		this.frame = frame;
		this.portal = portal;
		this.allowedBlocks = allowedBlocks;

		if (axis == Direction.Axis.X)
		{
			this.leftDir = Direction.EAST;
			this.rightDir = Direction.WEST;
		}
		else
		{
			this.leftDir = Direction.NORTH;
			this.rightDir = Direction.SOUTH;
		}
		for (BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 && this.isValidInsideMaterial(world.getBlockState(pos.down())); pos = pos.down())
		{
		}
		int i = this.getDistanceUntilEdge(pos, this.leftDir) - 1;
		if (i >= 0)
		{
			this.bottomLeft = pos.offset(this.leftDir, i);
			this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
			if (this.width < 2 || this.width > 21)
			{
				this.bottomLeft = null;
				this.width = 0;
			}
		}
		if (this.bottomLeft != null)
		{
			this.height = this.calculatePortalHeight();
		}
	}

	/**
	 * Attempts to spawn a portal. Returns true if it succeeded and false if it
	 * failed.
	 *
	 * @param worldIn
	 * @param pos
	 * @param portalBlock
	 * @param allowedBlocks
	 * @return {@link Boolean}
	 */
	public static boolean trySpawnPortal(IWorld worldIn, BlockPos pos, GelPortalBlock portalBlock, List<Block> allowedBlocks)
	{
		Block frameBlock = portalBlock.getFrameBlock().get().getBlock();
		GelPortalSize sizeX = new GelPortalSize(worldIn, pos, Direction.Axis.X, frameBlock, portalBlock, allowedBlocks);

		if (sizeX.isValidSize() && sizeX.portalBlockCount == 0)
		{
			sizeX.placePortalBlocks();
			return true;
		}
		else
		{
			GelPortalSize sizeZ = new GelPortalSize(worldIn, pos, Direction.Axis.Z, frameBlock, portalBlock, allowedBlocks);

			if (sizeZ.isValidSize() && sizeZ.portalBlockCount == 0)
			{
				sizeZ.placePortalBlocks();
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	/**
	 * Checks to see if the position is within a portal frame.
	 *
	 * @param worldIn
	 * @param pos
	 * @param portalBlock
	 * @param allowedInsideBlocks
	 * @return {@link Boolean}
	 */
	public static boolean isPortal(World worldIn, BlockPos pos, GelPortalBlock portalBlock, List<Block> allowedInsideBlocks)
	{
		GelPortalSize sizeX = new GelPortalSize(worldIn, pos, Direction.Axis.X, portalBlock.getFrameBlock().get().getBlock(), portalBlock, allowedInsideBlocks);
		if (sizeX.isValidSize())
			return true;
		else
		{
			GelPortalSize sizeZ = new GelPortalSize(worldIn, pos, Direction.Axis.Z, portalBlock.getFrameBlock().get().getBlock(), portalBlock, allowedInsideBlocks);
			return sizeZ.isValidSize();
		}
	}

	/**
	 * Checks to see if the portal is the right size.
	 *
	 * @return {@link Boolean}
	 */
	public boolean isValidSize()
	{
		return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
	}

	/**
	 * Checks to see if the portal is the right size and isn't missing any portal
	 * blocks.
	 *
	 * @return {@link Boolean}
	 */
	public boolean isPortalComplete()
	{
		return this.isValidSize() && this.hasAllPortals();
	}

	/**
	 * Checks if all portal blocks exist within the frame.
	 *
	 * @return {@link Boolean}
	 */
	public boolean hasAllPortals()
	{
		return this.portalBlockCount >= this.width * this.height;
	}

	protected int getDistanceUntilEdge(BlockPos pos, Direction direction)
	{
		int i;
		for (i = 0; i < 22; ++i)
		{
			BlockPos blockpos = pos.offset(direction, i);
			if (!this.isValidInsideMaterial(this.world.getBlockState(blockpos)) || this.world.getBlockState(blockpos.down()).getBlock() != this.frame)
			{
				break;
			}
		}
		Block block = this.world.getBlockState(pos.offset(direction, i)).getBlock();
		return block == this.frame ? i : 0;
	}

	protected int calculatePortalHeight()
	{
		label56:
		for (this.height = 0; this.height < 21; ++this.height)
		{
			for (int i = 0; i < this.width; ++i)
			{
				BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
				BlockState blockstate = this.world.getBlockState(blockpos);
				if (!this.isValidInsideMaterial(blockstate))
				{
					break label56;
				}
				Block block = blockstate.getBlock();
				if (block == this.portal)
				{
					++this.portalBlockCount;
				}
				if (i == 0)
				{
					block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();
					if (block != this.frame)
					{
						break label56;
					}
				}
				else if (i == this.width - 1)
				{
					block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();
					if (block != this.frame)
					{
						break label56;
					}
				}
			}
		}
		for (int j = 0; j < this.width; ++j)
		{
			if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != this.frame)
			{
				this.height = 0;
				break;
			}
		}
		if (this.height <= 21 && this.height >= 3)
		{
			return this.height;
		}
		else
		{
			this.bottomLeft = null;
			this.width = 0;
			this.height = 0;
			return 0;
		}
	}

	protected boolean isValidInsideMaterial(BlockState state)
	{
		Block block = state.getBlock();
		return state.getMaterial() == Material.AIR || block == this.portal || allowedBlocks.contains(block);
	}

	public void placePortalBlocks()
	{
		for (int i = 0; i < this.width; ++i)
		{
			BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);
			for (int j = 0; j < this.height; ++j)
			{
				this.world.setBlockState(blockpos.up(j), this.portal.getDefaultState().with(NetherPortalBlock.AXIS, this.axis), 18);
			}
		}
	}
}

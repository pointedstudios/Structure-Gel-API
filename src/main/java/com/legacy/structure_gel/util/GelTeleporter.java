package com.legacy.structure_gel.util;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Supplier;

import com.legacy.structure_gel.blocks.GelPortalBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;

/**
 * A more mod compatible teleporter for the API.
 * 
 * @author David
 *
 */
public class GelTeleporter extends Teleporter
{
	/**
	 * The default dimension this teleporter will take you to.
	 */
	private final Supplier<RegistryKey<World>> dimension1;
	/**
	 * The dimension this teleporter will take you to.
	 */
	private final Supplier<RegistryKey<World>> dimension2;
	/**
	 * The PointOfInterestType representing the portal.
	 */
	private final Supplier<PointOfInterestType> portalPOI;
	/**
	 * The portal block that gets placed. Must extend {@link GelPortalBlock}.
	 */
	private final Supplier<GelPortalBlock> portalBlock;
	/**
	 * The {@link BlockState} of the frame that will get placed.
	 */
	private final Supplier<BlockState> frameBlock;
	/**
	 * The logic behind how creating a new portal will work.
	 * 
	 * @see ICreatePortalFuncion
	 * @see CreatePortalBehavior
	 */
	private final ICreatePortalFuncion placementBehavior;

	public GelTeleporter(ServerWorld world, Supplier<RegistryKey<World>> dimension1, Supplier<RegistryKey<World>> dimension2, Supplier<PointOfInterestType> portalPOI, Supplier<GelPortalBlock> portalBlock, Supplier<BlockState> frameBlock, ICreatePortalFuncion placementBehavior)
	{
		super(world);
		this.dimension1 = dimension1;
		this.dimension2 = dimension2;
		this.portalPOI = portalPOI;
		this.portalBlock = portalBlock;
		this.frameBlock = frameBlock;
		this.placementBehavior = placementBehavior;
	}

	public GelTeleporter(ServerWorld worldIn, Supplier<RegistryKey<World>> dimension1, Supplier<RegistryKey<World>> dimension2, Supplier<PointOfInterestType> portalPOI, Supplier<GelPortalBlock> portalBlock, Supplier<BlockState> frameBlock, CreatePortalBehavior placementBehavior)
	{
		this(worldIn, dimension1, dimension2, portalPOI, portalBlock, frameBlock, placementBehavior.get());
	}

	/**
	 * Returns the opposite world from the one in this instance, or
	 * {@link #dimension1} by default.
	 * 
	 * @return {@link RegistryKey}
	 */
	@Internal
	public RegistryKey<World> getOpposite()
	{
		if (this.world != null && this.world.getDimensionKey().func_240901_a_().equals(this.dimension1.get().func_240901_a_()))
			return this.dimension2.get();
		else
			return this.dimension1.get();
	}

	public Supplier<RegistryKey<World>> getDimension1()
	{
		return this.dimension1;
	}

	public Supplier<RegistryKey<World>> getDimension2()
	{
		return this.dimension2;
	}

	public Supplier<PointOfInterestType> getPortalPOI()
	{
		return this.portalPOI;
	}

	public Supplier<GelPortalBlock> getPortalBlock()
	{
		return this.portalBlock;
	}

	public Supplier<BlockState> getFrameBlock()
	{
		return this.frameBlock;
	}

	public ICreatePortalFuncion getPlacementBehavior()
	{
		return this.placementBehavior;
	}

	// findPortal
	@Override
	public Optional<TeleportationRepositioner.Result> func_242957_a(BlockPos startPos, boolean toNether)
	{
		PointOfInterestManager poiManager = this.world.getPointOfInterestManager();
		int i = (int) Math.max(DimensionType.func_242715_a(this.world.getServer().getWorld(this.getOpposite()).func_230315_m_(), this.world.func_230315_m_()) * 16, 16);
		poiManager.ensureLoadedAndValid(this.world, startPos, i);

		Optional<PointOfInterest> optional = poiManager.getInSquare(poiType ->
		{
			return poiType == this.portalPOI.get();
		}, startPos, i, PointOfInterestManager.Status.ANY).sorted(Comparator.<PointOfInterest>comparingDouble(poi ->
		{
			return poi.getPos().distanceSq(startPos);
		}).thenComparingInt(poi ->
		{
			return poi.getPos().getY();
		})).filter(poi ->
		{
			return this.world.getBlockState(poi.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS);
		}).findFirst();

		return optional.map(poi ->
		{
			BlockPos blockpos = poi.getPos();
			this.world.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(blockpos), 3, blockpos);
			BlockState blockstate = this.world.getBlockState(blockpos);
			return TeleportationRepositioner.func_243676_a(blockpos, blockstate.get(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, (pos) -> this.world.getBlockState(pos) == blockstate);
		});
	}

	// createAndFindPortal
	@Override
	public Optional<TeleportationRepositioner.Result> func_242956_a(BlockPos startPos, Direction.Axis enterAxis)
	{
		return this.placementBehavior.apply(this, startPos, enterAxis);
	}

	//TODO finish code
	public static Optional<TeleportationRepositioner.Result> createAndFindPortalSurface(GelTeleporter teleporter, BlockPos startPos, Direction.Axis enterAxis)
	{
		return null;
	}

	/**
	 * Default code for the Teleporter with slight modification.
	 * 
	 * @param teleporter
	 * @param startPos
	 * @param enterAxis
	 * @return {@link Optional}
	 */
	public static Optional<TeleportationRepositioner.Result> createAndFindPortalNether(GelTeleporter teleporter, BlockPos startPos, Direction.Axis enterAxis)
	{
		Direction direction = Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, enterAxis);
		double d0 = -1.0D;
		BlockPos blockpos = null;
		double d1 = -1.0D;
		BlockPos blockpos1 = null;
		WorldBorder worldborder = teleporter.world.getWorldBorder();
		int i = teleporter.world.func_234938_ad_() - 1;
		BlockPos.Mutable blockpos$mutable = startPos.toMutable();

		for (BlockPos.Mutable blockpos$mutable1 : BlockPos.func_243514_a(startPos, 16, Direction.EAST, Direction.SOUTH))
		{
			int j = Math.min(i, teleporter.world.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos$mutable1.getX(), blockpos$mutable1.getZ()));
			if (worldborder.contains(blockpos$mutable1) && worldborder.contains(blockpos$mutable1.move(direction, 1)))
			{
				blockpos$mutable1.move(direction.getOpposite(), 1);

				for (int l = j; l >= 0; --l)
				{
					blockpos$mutable1.setY(l);
					if (teleporter.world.isAirBlock(blockpos$mutable1))
					{
						int i1;
						for (i1 = l; l > 0 && teleporter.world.isAirBlock(blockpos$mutable1.move(Direction.DOWN)); --l)
						{
						}

						if (l + 4 <= i)
						{
							int j1 = i1 - l;
							if (j1 <= 0 || j1 >= 3)
							{
								blockpos$mutable1.setY(l);
								if (teleporter.isSafePosition(blockpos$mutable1, blockpos$mutable, direction, 0))
								{
									double d2 = startPos.distanceSq(blockpos$mutable1);
									if (teleporter.isSafePosition(blockpos$mutable1, blockpos$mutable, direction, -1) && teleporter.isSafePosition(blockpos$mutable1, blockpos$mutable, direction, 1) && (d0 == -1.0D || d0 > d2))
									{
										d0 = d2;
										blockpos = blockpos$mutable1.toImmutable();
									}

									if (d0 == -1.0D && (d1 == -1.0D || d1 > d2))
									{
										d1 = d2;
										blockpos1 = blockpos$mutable1.toImmutable();
									}
								}
							}
						}
					}
				}
			}
		}

		if (d0 == -1.0D && d1 != -1.0D)
		{
			blockpos = blockpos1;
			d0 = d1;
		}

		if (d0 == -1.0D)
		{
			blockpos = (new BlockPos(startPos.getX(), MathHelper.clamp(startPos.getY(), 70, teleporter.world.func_234938_ad_() - 10), startPos.getZ())).toImmutable();
			Direction direction1 = direction.rotateY();
			if (!worldborder.contains(blockpos))
			{
				return Optional.empty();
			}

			for (int l1 = -1; l1 < 2; ++l1)
			{
				for (int k2 = 0; k2 < 2; ++k2)
				{
					for (int i3 = -1; i3 < 3; ++i3)
					{
						BlockState blockstate1 = i3 < 0 ? teleporter.frameBlock.get() : Blocks.AIR.getDefaultState();
						blockpos$mutable.setAndOffset(blockpos, k2 * direction.getXOffset() + l1 * direction1.getXOffset(), i3, k2 * direction.getZOffset() + l1 * direction1.getZOffset());
						teleporter.world.setBlockState(blockpos$mutable, blockstate1);
					}
				}
			}
		}

		for (int k1 = -1; k1 < 3; ++k1)
		{
			for (int i2 = -1; i2 < 4; ++i2)
			{
				if (k1 == -1 || k1 == 2 || i2 == -1 || i2 == 3)
				{
					blockpos$mutable.setAndOffset(blockpos, k1 * direction.getXOffset(), i2, k1 * direction.getZOffset());
					teleporter.world.setBlockState(blockpos$mutable, teleporter.frameBlock.get(), 3);
				}
			}
		}

		BlockState blockstate = teleporter.portalBlock.get().getDefaultState().with(NetherPortalBlock.AXIS, enterAxis);

		for (int j2 = 0; j2 < 2; ++j2)
		{
			for (int l2 = 0; l2 < 3; ++l2)
			{
				blockpos$mutable.setAndOffset(blockpos, j2 * direction.getXOffset(), l2, j2 * direction.getZOffset());
				teleporter.world.setBlockState(blockpos$mutable, blockstate, 18);
			}
		}

		return Optional.of(new TeleportationRepositioner.Result(blockpos.toImmutable(), 2, 3));
	}

	// func_242955_a
	private boolean isSafePosition(BlockPos pos, BlockPos.Mutable mutablePos, Direction facing, int offset)
	{
		Direction direction = facing.rotateY();

		for (int i = -1; i < 3; ++i)
		{
			for (int j = -1; j < 4; ++j)
			{
				mutablePos.setAndOffset(pos, facing.getXOffset() * i + direction.getXOffset() * offset, j, facing.getZOffset() * i + direction.getZOffset() * offset);
				if (j < 0 && !this.world.getBlockState(mutablePos).getMaterial().isSolid())
					return false;

				if (j >= 0 && !this.world.isAirBlock(mutablePos))
					return false;
			}
		}

		return true;
	}

	/**
	 * Determins how the portal generated should be placed.
	 * 
	 * @author David
	 *
	 */
	public static enum CreatePortalBehavior
	{
		NETHER(GelTeleporter::createAndFindPortalNether), ON_SURFACE(GelTeleporter::createAndFindPortalSurface);

		private final ICreatePortalFuncion function;

		private CreatePortalBehavior(ICreatePortalFuncion function)
		{
			this.function = function;
		}

		public ICreatePortalFuncion get()
		{
			return this.function;
		}
	}

	@FunctionalInterface
	private static interface ICreatePortalFuncion
	{
		public Optional<TeleportationRepositioner.Result> apply(GelTeleporter teleporter, BlockPos startPos, Direction.Axis enterAxis);
	}
}

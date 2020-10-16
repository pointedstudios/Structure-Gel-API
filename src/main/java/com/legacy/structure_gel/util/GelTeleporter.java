package com.legacy.structure_gel.util;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.legacy.structure_gel.blocks.GelPortalBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.PortalSize;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.TeleportationRepositioner.Result;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

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
		if (this.world != null && this.world.getDimensionKey().getLocation().equals(this.dimension1.get().getLocation()))
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

	/**
	 * Gets the destination world of this teleporter.
	 * 
	 * @return {@link ServerWorld}
	 */
	public ServerWorld getWorld()
	{
		return this.world;
	}

	/**
	 * Returns if the block passed should be ignored when placing on the surface. By
	 * default, ignores blocks tagged as leaves or logs, air, and blocks without
	 * collision (except fluids).
	 * 
	 * @param state
	 * @param pos
	 * @return {@link Boolean}
	 */
	@SuppressWarnings("deprecation")
	public boolean shouldIgnoreBlock(BlockState state, BlockPos pos)
	{
		return state.isIn(BlockTags.LEAVES) || state.isIn(BlockTags.LOGS) || state.isAir(world, pos) || (state.getCollisionShape(world, pos).isEmpty() && !state.getMaterial().isLiquid());
	}

	/**
	 * Locates a portal in the {@link #getWorld()}.
	 */
	@Override
	public Optional<TeleportationRepositioner.Result> getExistingPortal(BlockPos startPos, boolean toNether)
	{
		PointOfInterestManager poiManager = this.world.getPointOfInterestManager();
		int dist = (int) Math.max(DimensionType.getCoordinateDifference(this.world.getServer().getWorld(this.getOpposite()).getDimensionType(), this.world.getDimensionType()) * 16, 16);
		poiManager.ensureLoadedAndValid(this.world, startPos, dist);

		Optional<PointOfInterest> optional = poiManager.getInSquare(poiType -> poiType == this.portalPOI.get(), startPos, dist, PointOfInterestManager.Status.ANY).filter(poi -> poiManager.getType(poi.getPos().down()).orElse(null) != poi.getType()).filter(poi -> this.world.getBlockState(poi.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS)).min(Comparator.<PointOfInterest>comparingDouble(poi -> poi.getPos().distanceSq(startPos)).thenComparingInt(poi -> poi.getPos().getY()));

		return optional.map(poi ->
		{
			BlockPos blockpos = poi.getPos();
			this.world.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(blockpos), 3, blockpos);
			BlockState blockstate = this.world.getBlockState(blockpos);
			return TeleportationRepositioner.findLargestRectangle(blockpos, blockstate.get(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, (pos) -> this.world.getBlockState(pos) == blockstate);
		});
	}

	/**
	 * Places a new portal in the {@link #getWorld()} and locates it.
	 */
	@Override
	public Optional<TeleportationRepositioner.Result> makePortal(BlockPos startPos, Direction.Axis enterAxis)
	{
		return this.placementBehavior.apply(this, startPos, enterAxis);
	}

	@Override
	@Nullable
	public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo)
	{
		// Scale position
		WorldBorder worldborder = destWorld.getWorldBorder();
		double minX = Math.max(-2.9999872E7D, worldborder.minX() + 16.0D);
		double minZ = Math.max(-2.9999872E7D, worldborder.minZ() + 16.0D);
		double maxX = Math.min(2.9999872E7D, worldborder.maxX() - 16.0D);
		double maxZ = Math.min(2.9999872E7D, worldborder.maxZ() - 16.0D);
		double scaling = DimensionType.getCoordinateDifference(entity.world.getDimensionType(), destWorld.getDimensionType());
		BlockPos scaledPos = new BlockPos(MathHelper.clamp(entity.getPosX() * scaling, minX, maxX), entity.getPosY(), MathHelper.clamp(entity.getPosZ() * scaling, minZ, maxZ));

		// Get info about current portal
		BlockPos portalPos = ObfuscationReflectionHelper.getPrivateValue(Entity.class, entity, "field_242271_ac");
		BlockState blockstate = entity.world.getBlockState(portalPos);
		Direction.Axis portalAxis;
		Vector3d offset;
		if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
		{
			portalAxis = blockstate.get(BlockStateProperties.HORIZONTAL_AXIS);
			TeleportationRepositioner.Result motionTpResult = TeleportationRepositioner.findLargestRectangle(portalPos, portalAxis, 21, Direction.Axis.Y, 21, bp -> entity.world.getBlockState(bp) == blockstate);
			offset = PortalSize.func_242973_a(motionTpResult, portalAxis, entity.getPositionVec(), entity.getSize(entity.getPose()));
		}
		else
		{
			portalAxis = Direction.Axis.X;
			offset = new Vector3d(0.5D, 0.0D, 0.0D);
		}

		// Find or create a new portal
		Optional<Result> result = this.getExistingPortal(scaledPos, false);
		if (entity instanceof ServerPlayerEntity && !result.isPresent())
			result = makePortal(scaledPos, portalAxis);
		if (!result.isPresent())
			return null;
		
		// Get info from new portal
		PortalInfo portalInfo = PortalSize.func_242963_a(destWorld, result.get(), portalAxis, offset, entity.getSize(entity.getPose()), entity.getMotion(), entity.rotationYaw, entity.rotationPitch);
		return new PortalInfo(new Vector3d(result.get().startPos.getX() + 0.5, result.get().startPos.getY() + 0.05, result.get().startPos.getZ() + 0.5), portalInfo.motion, portalInfo.rotationYaw, portalInfo.rotationPitch);
	}

	/**
	 * Places this portal on highest block in the world, ignoring blocks specified
	 * in {@link #shouldIgnoreBlock(BlockState, BlockPos)}.
	 * 
	 * @param teleporter
	 * @param startPos
	 * @param enterAxis
	 * @return {@link Optional}
	 */
	public static Optional<TeleportationRepositioner.Result> createAndFindPortalSurface(GelTeleporter teleporter, BlockPos startPos, Direction.Axis enterAxis)
	{
		ServerWorld world = teleporter.world;
		int x = startPos.getX();
		int y = world.func_234938_ad_();
		int z = startPos.getZ();

		// Calculate position
		BlockPos.Mutable mutablePos = new BlockPos.Mutable(x, y, z);
		int i = y;
		BlockState state = world.getBlockState(mutablePos);
		while (i > 0 && teleporter.shouldIgnoreBlock(state, mutablePos))
		{
			state = world.getBlockState(mutablePos.move(Direction.DOWN));
			i--;
		}
		if (i > 0)
			y = i + 1;

		// Place frame
		BlockState frameState = teleporter.getFrameBlock().get();
		for (int horizontalOffset = -1; horizontalOffset < 3; ++horizontalOffset)
		{
			for (int verticalOffset = -1; verticalOffset < 4; ++verticalOffset)
			{
				if (horizontalOffset == -1 || horizontalOffset == 2 || verticalOffset == -1 || verticalOffset == 3)
				{
					BlockPos pos2 = new BlockPos(x, y + verticalOffset, z + horizontalOffset);
					world.setBlockState(pos2, frameState, 3);
				}
			}
		}

		// Place portal blocks
		BlockState portalState = teleporter.getPortalBlock().get().getDefaultState().with(NetherPortalBlock.AXIS, Direction.Axis.Z);
		for (int horizontalOffset = 0; horizontalOffset < 2; ++horizontalOffset)
		{
			for (int verticalOffset = 0; verticalOffset < 3; ++verticalOffset)
			{
				BlockPos pos2 = new BlockPos(x, y + verticalOffset, z + horizontalOffset);
				world.setBlockState(pos2, portalState, 18);
			}
		}

		// Add platform below portal
		boolean placePlatform = true;
		label0: for (int x1 = -1; x1 < 2; x1++)
		{
			for (int z1 = 0; z1 < 2; z1++)
			{
				BlockPos pos2 = new BlockPos(x + x1, y - 1, z + z1);
				BlockState existingState = world.getBlockState(pos2);
				if (!(teleporter.shouldIgnoreBlock(existingState, pos2) || existingState.getMaterial().isLiquid()) && existingState.getBlock() != frameState.getBlock())
				{
					placePlatform = false;
					break label0;
				}
			}
		}

		if (placePlatform)
			for (int x1 = -1; x1 < 2; x1++)
				for (int z1 = 0; z1 < 2; z1++)
					world.setBlockState(new BlockPos(x + x1, y - 1, z + z1), frameState);

		// find and return portal
		return teleporter.getExistingPortal(startPos, false);
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
								if (teleporter.checkRegionForPlacement(blockpos$mutable1, blockpos$mutable, direction, 0))
								{
									double d2 = startPos.distanceSq(blockpos$mutable1);
									if (teleporter.checkRegionForPlacement(blockpos$mutable1, blockpos$mutable, direction, -1) && teleporter.checkRegionForPlacement(blockpos$mutable1, blockpos$mutable, direction, 1) && (d0 == -1.0D || d0 > d2))
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

	private boolean checkRegionForPlacement(BlockPos pos, BlockPos.Mutable mutablePos, Direction facing, int offset)
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

	/**
	 * Used to create a portal and get it's location.
	 * 
	 * @author David
	 *
	 */
	@FunctionalInterface
	public static interface ICreatePortalFuncion
	{
		public Optional<TeleportationRepositioner.Result> apply(GelTeleporter teleporter, BlockPos startPos, Direction.Axis enterAxis);
	}
}

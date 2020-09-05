package com.legacy.structure_gel.blocks;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import com.legacy.structure_gel.util.GelTeleporter;
import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.util.capability.GelCapability;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GelPortalBlock extends NetherPortalBlock
{
	private final Function<ServerWorld, GelTeleporter> teleporter;
	private final Supplier<RegistryKey<World>> dimension1;
	private final Supplier<RegistryKey<World>> dimension2;

	public GelPortalBlock(AbstractBlock.Properties properties, Function<ServerWorld, GelTeleporter> teleporter, Supplier<RegistryKey<World>> dimension1, Supplier<RegistryKey<World>> dimension2)
	{
		super(properties);
		this.teleporter = teleporter;
		this.dimension1 = dimension1;
		this.dimension2 = dimension2;
	}

	@Internal
	public final GelTeleporter getTeleporter(ServerWorld world)
	{
		return this.teleporter.apply(world);
	}

	@Internal
	public final RegistryKey<World> getDestination(Entity entityIn)
	{
		if (entityIn.world.getDimensionKey().func_240901_a_().equals(this.dimension1.get().func_240901_a_()))
			return this.dimension2.get();
		else
			return this.dimension1.get();
	}

	public int getMaxTimeInside(Entity entityIn)
	{
		return entityIn.getMaxInPortalTime();
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
	{
		return;
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		/*		Direction.Axis direction$axis = facing.getAxis();
				Direction.Axis direction$axis1 = stateIn.get(AXIS);
				boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
				return !flag && !facingState.isIn(this) && !(new PortalSize(worldIn, currentPos, direction$axis1)).validatePortal() ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		*/ return stateIn;
	}

	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
	{
		/*if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss())
			entityIn.setPortal(pos);*/

		if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss() && entityIn.getCapability(GelCapability.INSTANCE).isPresent())
		{
			entityIn.setPortal(pos);
			entityIn.getCapability(GelCapability.INSTANCE).ifPresent(c -> c.setPortal(this));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		if (rand.nextInt(100) == 0)
		{
			worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
		}

		for (int i = 0; i < 4; ++i)
		{
			double d0 = (double) pos.getX() + rand.nextDouble();
			double d1 = (double) pos.getY() + rand.nextDouble();
			double d2 = (double) pos.getZ() + rand.nextDouble();
			double d3 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			double d4 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			double d5 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			int j = rand.nextInt(2) * 2 - 1;
			if (!worldIn.getBlockState(pos.west()).isIn(this) && !worldIn.getBlockState(pos.east()).isIn(this))
			{
				d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
				d3 = (double) (rand.nextFloat() * 2.0F * (float) j);
			}
			else
			{
				d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) j;
				d5 = (double) (rand.nextFloat() * 2.0F * (float) j);
			}

			worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		}

	}
}

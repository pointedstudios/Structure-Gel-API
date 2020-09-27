package com.legacy.structure_gel.core.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.legacy.structure_gel.blocks.GelPortalBlock;
import com.legacy.structure_gel.util.capability.GelCapability;

import net.minecraft.block.BlockState;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.PortalSize;
import net.minecraft.entity.Entity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;

@Mixin(Entity.class)
public class EntityMixin
{
	@Shadow
	public World world;
	@Shadow
	protected boolean inPortal;
	@Shadow
	protected int portalCounter;
	@Shadow
	protected BlockPos field_242271_ac;

	/*
	 * Entity#updatePortal insert a modified version of vanilla code at the head of the method, only to execute if the player has stepped in a GelPortal.
	 */
	@Inject(at = @At("HEAD"), method = "updatePortal()V", cancellable = true)
	private void updatePortal(CallbackInfo callback)
	{
		GelCapability.ifPresent((Entity) (Object) this, (gelEntity) ->
		{
			// not null when a player steps in a GelPortal
			if (gelEntity.getPortal() != null)
			{
				if (this.world instanceof ServerWorld)
				{
					if (this.inPortal)
					{
						GelPortalBlock portal = gelEntity.getPortal();
						int maxTime = portal.getMaxTimeInside(((Entity) (Object) this));
						ServerWorld destinationWorld = ((ServerWorld) this.world).getServer().getWorld(portal.getTeleporter((ServerWorld) this.world).getOpposite());

						if (destinationWorld != null && !this.isPassenger() && this.portalCounter++ >= maxTime)
						{
							this.world.getProfiler().startSection("portal");
							this.portalCounter = maxTime;
							this.func_242279_ag();

							Teleporter oldTeleporter = destinationWorld.worldTeleporter;
							destinationWorld.worldTeleporter = portal.getTeleporter(destinationWorld);
							this.changeDimension(destinationWorld);
							destinationWorld.worldTeleporter = oldTeleporter;

							this.world.getProfiler().endSection();
						}

						this.inPortal = false;
						gelEntity.setPortal(null);
					}
					else
					{
						if (this.portalCounter > 0)
							this.portalCounter -= 4;
						if (this.portalCounter < 0)
							this.portalCounter = 0;
					}

					this.decrementTimeUntilPortal();
					callback.cancel();
				}

				gelEntity.setPrevPortal(gelEntity.getPortal());
				gelEntity.setPortal(null);
			}
			
			else if (this.inPortal == true)
			{
				gelEntity.setPrevPortal(null);
			}
		});
	}

	/*
	 * Entity#func_241829_a
	 */
	@Inject(at = @At(value = "RETURN", ordinal = 0), method = "func_241829_a(Lnet/minecraft/world/server/ServerWorld;)Lnet/minecraft/block/PortalInfo;", cancellable = true)
	protected void func_241829_a(ServerWorld destWorld, CallbackInfoReturnable<PortalInfo> callback)
	{
		if (GelCapability.getIfPresent(((Entity) (Object) this), (gelEntity) -> gelEntity.getPortal() != null))
		{
			boolean toNether = destWorld.getDimensionKey() == World.THE_NETHER;
			WorldBorder worldborder = destWorld.getWorldBorder();
			double minX = Math.max(-2.9999872E7D, worldborder.minX() + 16.0D);
			double minZ = Math.max(-2.9999872E7D, worldborder.minZ() + 16.0D);
			double maxX = Math.min(2.9999872E7D, worldborder.maxX() - 16.0D);
			double maxZ = Math.min(2.9999872E7D, worldborder.maxZ() - 16.0D);
			double scaling = DimensionType.getCoordinateDifference(this.world.getDimensionType(), destWorld.getDimensionType());
			BlockPos pos = new BlockPos(MathHelper.clamp(((Entity) (Object) this).getPosX() * scaling, minX, maxX), ((Entity) (Object) this).getPosY(), MathHelper.clamp(((Entity) (Object) this).getPosZ() * scaling, minZ, maxZ));
			// Find a portal and create one if needed.
			PortalInfo portalInfo = this.func_241830_a(destWorld, pos, toNether).map((tpResult) ->
			{
				BlockState blockstate = this.world.getBlockState(this.field_242271_ac);
				Direction.Axis direction;
				Vector3d motion;
				if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
				{
					direction = blockstate.get(BlockStateProperties.HORIZONTAL_AXIS);
					TeleportationRepositioner.Result motionTpResult = TeleportationRepositioner.findLargestRectangle(this.field_242271_ac, direction, 21, Direction.Axis.Y, 21, (bp) ->
					{
						return this.world.getBlockState(bp) == blockstate;
					});
					motion = this.func_241839_a(direction, motionTpResult);
				}
				else
				{
					direction = Direction.Axis.X;
					motion = new Vector3d(0.5D, 0.0D, 0.0D);
				}

				return PortalSize.func_242963_a(destWorld, tpResult, direction, motion, ((Entity) (Object) this).getSize(((Entity) (Object) this).getPose()), ((Entity) (Object) this).getMotion(), ((Entity) (Object) this).rotationYaw, ((Entity) (Object) this).rotationPitch);
			}).orElse((PortalInfo) null);

			callback.setReturnValue(portalInfo);
		}
	}

	@Shadow
	public int getMaxInPortalTime()
	{
		throw new IllegalStateException("Mixin failed to shadow getMaxInPortalTime()");
	}

	@Shadow
	public boolean isPassenger()
	{
		throw new IllegalStateException("Mixin failed to shadow isPassenger()");
	}

	@Shadow
	public void func_242279_ag()
	{
		throw new IllegalStateException("Mixin failed to shadow func_242279_ag()");
	}

	@Shadow
	public Entity changeDimension(ServerWorld world)
	{
		throw new IllegalStateException("Mixin failed to shadow changeDimension()");
	}

	@Shadow
	public BlockPos getPosition()
	{
		throw new IllegalStateException("Mixin failed to shadow getPosition()");
	}

	@Shadow
	protected void decrementTimeUntilPortal()
	{
		throw new IllegalStateException("Mixin failed to shadow decrementTimeUntilPortal()");
	}

	@Shadow
	protected Optional<TeleportationRepositioner.Result> func_241830_a(ServerWorld destWorld, BlockPos pos, boolean toNether)
	{
		throw new IllegalStateException("Mixin failed to shadow func_241830_a()");
	}

	@Shadow
	protected Vector3d func_241839_a(Direction.Axis axis, TeleportationRepositioner.Result tpResult)
	{
		throw new IllegalStateException("Mixin failed to shadow func_241839_a()");
	}
}

package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.legacy.structure_gel.blocks.GelPortalBlock;
import com.legacy.structure_gel.util.capability.GelCapability;
import com.legacy.structure_gel.util.capability.IGelEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

@Mixin(Entity.class)
public class EntityMixin
{
	@Shadow
	public World world;
	@Shadow
	protected boolean inPortal;
	@Shadow
	protected int portalCounter;

	/*
	 * Entity#updatePortal insert a modified version of vanilla code at the head of the method, only to execute if the player has stepped in a GelPortal.
	 */
	@Inject(at = @At("HEAD"), method = "updatePortal()V", cancellable = true)
	private void updatePortal(CallbackInfo callback)
	{
		if (getThis().getCapability(GelCapability.INSTANCE).isPresent())
		{
			IGelEntity gelEntity = getThis().getCapability(GelCapability.INSTANCE).resolve().get();
			// not null when a player steps in a GelPortal
			if (gelEntity.getPortal() != null)
			{
				if (this.world instanceof ServerWorld)
				{
					if (this.inPortal)
					{
						GelPortalBlock portal = gelEntity.getPortal();
						int maxTime = portal.getMaxTimeInside(getThis());
						ServerWorld destinationWorld = ((ServerWorld) this.world).getServer().getWorld(portal.getDestination(getThis()));
						if (destinationWorld != null && !this.isPassenger() && this.portalCounter++ >= maxTime)
						{
							this.world.getProfiler().startSection("portal");
							this.portalCounter = maxTime;
							this.func_242279_ag();
							this.changeDimension(destinationWorld, portal.getTeleporter(destinationWorld));
							this.world.getProfiler().endSection();
						}
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
		}
	}

	public Entity getThis()
	{
		return (Entity) (Object) this;
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
	public Entity changeDimension(ServerWorld world, ITeleporter teleporter)
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
}

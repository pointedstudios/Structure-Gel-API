package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.legacy.structure_gel.blocks.GelPortalBlock;
import com.legacy.structure_gel.util.capability.GelCapability;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
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

	@Inject(at = @At("HEAD"), method = "updatePortal()V", cancellable = true)
	private void updatePortal(CallbackInfo callback)
	{
		GelCapability.ifPresent((Entity) (Object) this, (gelEntity) ->
		{
			// in gel portal
			if (gelEntity.getPortal() != null)
			{
				// Server side
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

				gelEntity.setPortal(null);
			}
			// not in gel portal but in some portal
			else if (this.inPortal)
				gelEntity.setPortalVisual(null);

			// If not in a portal, set audio to null
			if (!this.inPortal)
				gelEntity.setPortalAudio(null);
		});
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
	protected void decrementTimeUntilPortal()
	{
		throw new IllegalStateException("Mixin failed to shadow decrementTimeUntilPortal()");
	}
}

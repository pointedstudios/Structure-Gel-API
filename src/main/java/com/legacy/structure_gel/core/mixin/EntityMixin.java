package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.legacy.structure_gel.blocks.GelPortalBlock;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
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
	 * Entity#updatePortal
	 */
	@Inject(at = @At("HEAD"), method = "updatePortal()V", cancellable = true)
	private void updatePortal(CallbackInfo callback)
	{
		// TODO store portal in capability
		// TODO test vanilla portal just in case
		if (this.world.getBlockState(this.getPosition()).getBlock() instanceof GelPortalBlock)
		{
			if (this.world instanceof ServerWorld)
			{
				int i = this.getMaxInPortalTime();
				ServerWorld currentWorld = (ServerWorld) this.world;
				if (this.inPortal)
				{
					MinecraftServer server = currentWorld.getServer();
					GelPortalBlock portal = (GelPortalBlock) currentWorld.getBlockState(this.getPosition()).getBlock();
					RegistryKey<World> destinationKey = portal.getDestination(getThis());
					ServerWorld destinationWorld = server.getWorld(destinationKey);
					if (destinationWorld != null && !this.isPassenger() && this.portalCounter++ >= i)
					{
						this.world.getProfiler().startSection("portal");
						this.portalCounter = i;
						this.func_242279_ag();
						this.changeDimension(destinationWorld, portal.getTeleporter(destinationWorld));
						this.world.getProfiler().endSection();
					}
				}

				this.inPortal = false;
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

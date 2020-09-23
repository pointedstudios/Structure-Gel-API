package com.legacy.structure_gel.core.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.legacy.structure_gel.util.capability.GelCapability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;

@Mixin(IngameGui.class)
public class IngameGuiMixin
{
	@Shadow
	protected Minecraft mc;

	@Shadow
	protected int scaledHeight;

	@Shadow
	protected int scaledWidth;

	/*
	 * IngameGui#renderPortal
	 */
	@Inject(at = @At("HEAD"), method = "renderPortal(F)V", cancellable = true)
	private void renderPortal(float timeInPortal, CallbackInfo callback)
	{
		if (mc.player.getCapability(GelCapability.INSTANCE).isPresent() && mc.player.getCapability(GelCapability.INSTANCE).resolve().get().getPortal() != null)
		{
			mc.player.getCapability(GelCapability.INSTANCE).ifPresent(gelEntity -> Optional.ofNullable(gelEntity.getPortal()).ifPresent(portal -> portal.renderPortal(timeInPortal, this.scaledHeight, this.scaledWidth)));
			callback.cancel();
		}
	}
}

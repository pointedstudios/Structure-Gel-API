package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.legacy.structure_gel.blocks.GelPortalBlock;
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

	// Renders the portal overlay
	@Inject(at = @At("HEAD"), method = "renderPortal(F)V", cancellable = true)
	private void renderPortal(float timeInPortal, CallbackInfo callback)
	{
		GelCapability.ifPresent(this.mc.player, (gelEntity) ->
		{
			GelPortalBlock portal = gelEntity.getPortalVisual();
			if (portal != null)
			{
				portal.renderPortal(timeInPortal, this.scaledHeight, this.scaledWidth);
				callback.cancel();
			}
		});
	}
}

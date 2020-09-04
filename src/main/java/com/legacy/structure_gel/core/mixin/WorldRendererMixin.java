package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.legacy.structure_gel.events.RenderSkyEvent;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(value = Dist.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
	/**
	 * {@link WorldRenderer#renderSky(MatrixStack, float)}
	 * 
	 * @param matrixStackIn
	 * @param partialTicks
	 * @param callback
	 */
	@Inject(at = @At("HEAD"), method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", cancellable = true)
	private void renderSky(MatrixStack matrixStackIn, float partialTicks, CallbackInfo callback)
	{
		if (MinecraftForge.EVENT_BUS.post(new RenderSkyEvent(matrixStackIn, partialTicks)));
			callback.cancel();
	}
}
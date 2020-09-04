package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.legacy.structure_gel.events.RenderRainParticlesEvent;
import com.legacy.structure_gel.events.RenderCloudsEvent;
import com.legacy.structure_gel.events.RenderRainEvent;
import com.legacy.structure_gel.events.RenderSkyEvent;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(value = Dist.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
	/*
	 * WorldRenderer#renderSky
	 */
	@Inject(at = @At("HEAD"), method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", cancellable = true)
	private void renderSky(MatrixStack matrixStackIn, float partialTicks, CallbackInfo callback)
	{
		if (MinecraftForge.EVENT_BUS.post(new RenderSkyEvent(matrixStackIn, partialTicks)))
			callback.cancel();
	}

	/*
	 * WorldRenderer#renderRainSnow
	 */
	@Inject(at = @At("HEAD"), method = "renderRainSnow(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", cancellable = true)
	private void renderRainAndSnow(LightTexture lightmapIn, float partialTicks, double xIn, double yIn, double zIn, CallbackInfo callback)
	{
		if (MinecraftForge.EVENT_BUS.post(new RenderRainEvent(lightmapIn, partialTicks, xIn, yIn, zIn)))
			callback.cancel();
	}

	/*
	 * WorldRenderer#addRainParticles
	 */
	@Inject(at = @At("HEAD"), method = "addRainParticles(Lnet/minecraft/client/renderer/ActiveRenderInfo;)V", cancellable = true)
	private void addRainParticles(ActiveRenderInfo activeRenderInfoIn, CallbackInfo callback)
	{
		if (MinecraftForge.EVENT_BUS.post(new RenderRainParticlesEvent(activeRenderInfoIn)))
			callback.cancel();
	}

	/*
	 * WorldRenderer#renderClouds
	 */
	@Inject(at = @At("HEAD"), method = "renderClouds(Lcom/mojang/blaze3d/matrix/MatrixStack;FDDD)V", cancellable = true)
	private void renderClouds(MatrixStack matrixStackIn, float partialTicks, double viewEntityX, double viewEntityY, double viewEntityZ, CallbackInfo callback)
	{
		if (MinecraftForge.EVENT_BUS.post(new RenderCloudsEvent(matrixStackIn, partialTicks, viewEntityZ, viewEntityZ, viewEntityZ)))
			callback.cancel();
	}
}
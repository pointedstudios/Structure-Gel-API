package com.legacy.structure_gel.events;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ISkyRenderHandler;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Called when rendering the sky to create your own skybox. Make sure to cancel
 * the event when you're done rendering.<br>
 * <br>
 * {@link Bus#FORGE}
 * 
 * @author David
 * @deprecated will be removed in 1.17. Use {@link ISkyRenderHandler}
 *
 */
@Deprecated
@Cancelable
@OnlyIn(value = Dist.CLIENT)
public class RenderSkyEvent extends WorldRenderEvent
{
	private final MatrixStack matrixStack;
	private final float partialTicks;

	public RenderSkyEvent(MatrixStack matrixStack, float partialTicks)
	{
		super();
		this.matrixStack = matrixStack;
		this.partialTicks = partialTicks;
	}

	public MatrixStack getMatrixStack()
	{
		return this.matrixStack;
	}

	public float getPartialTicks()
	{
		return this.partialTicks;
	}
}

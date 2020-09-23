package com.legacy.structure_gel.events;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ICloudRenderHandler;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Called when rendering clouds. Make sure to cancel the event when you're done
 * rendering.<br>
 * <br>
 * {@link Bus#FORGE}
 * 
 * @author David
 * @deprecated will be removed in 1.17. Use {@link ICloudRenderHandler}
 *
 */
@Deprecated
@Cancelable
@OnlyIn(value = Dist.CLIENT)
public class RenderCloudsEvent extends WorldRenderEvent
{
	private final MatrixStack matrixStack;
	private final float partialTicks;
	private final double viewEntityX;
	private final double viewEntityY;
	private final double viewEntityZ;

	public RenderCloudsEvent(MatrixStack matrixStack, float partialTicks, double viewEntityX, double viewEntityY, double viewEntityZ)
	{
		super();
		this.matrixStack = matrixStack;
		this.partialTicks = partialTicks;
		this.viewEntityX = viewEntityX;
		this.viewEntityY = viewEntityY;
		this.viewEntityZ = viewEntityZ;
	}

	public MatrixStack getMatrixStack()
	{
		return this.matrixStack;
	}

	public float getPartialTicks()
	{
		return this.partialTicks;
	}

	public double getViewEntityX()
	{
		return this.viewEntityX;
	}

	public double getViewEntityY()
	{
		return this.viewEntityY;
	}

	public double getViewEntityZ()
	{
		return this.viewEntityZ;
	}
}

package com.legacy.structure_gel.events;

import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Called when rendering weather effects to create your own. Cancel the event
 * when you're done rendering.<br>
 * <br>
 * {@link Bus#FORGE}
 * 
 * @author David
 *
 */
@Cancelable
@OnlyIn(value = Dist.CLIENT)
public class RenderRainEvent extends WorldRenderEvent
{
	private final LightTexture lightmapIn;
	private final float partialTicks;
	private final double x;
	private final double y;
	private final double z;

	public RenderRainEvent(LightTexture lightmapIn, float partialTicks, double x, double y, double z)
	{
		super();
		this.lightmapIn = lightmapIn;
		this.partialTicks = partialTicks;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public LightTexture getLightmapIn()
	{
		return this.lightmapIn;
	}

	public float getPartialTicks()
	{
		return this.partialTicks;
	}

	public double getX()
	{
		return this.x;
	}

	public double getY()
	{
		return this.y;
	}

	public double getZ()
	{
		return this.z;
	}
}

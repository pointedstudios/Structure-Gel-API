package com.legacy.structure_gel.events;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Called when rendering the sky to create your own skybox. Make sure to cancel
 * the event when you're done rendering.<br>
 * <br>
 * {@link Bus#FORGE}
 * 
 * @author David
 *
 */
@Cancelable
@OnlyIn(value = Dist.CLIENT)
public class RenderRainParticlesEvent extends WorldRenderEvent
{
	private final ActiveRenderInfo activeRenderInfo;

	public RenderRainParticlesEvent(ActiveRenderInfo activeRenderInfo)
	{
		super();
		this.activeRenderInfo = activeRenderInfo;
	}

	public ActiveRenderInfo getActiveRenderInfo()
	{
		return this.activeRenderInfo;
	}
}

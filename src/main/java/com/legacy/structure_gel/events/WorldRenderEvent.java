package com.legacy.structure_gel.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Base class for world rendering events to get the world and world
 * renderer.<br>
 * <br>
 * {@link Bus#FORGE}
 * 
 * @author David
 *
 */
@Cancelable
@OnlyIn(value = Dist.CLIENT)
public abstract class WorldRenderEvent extends Event
{
	private final ClientWorld world;
	private final WorldRenderer worldRenderer;

	public WorldRenderEvent()
	{
		this.world = Minecraft.getInstance().world;
		this.worldRenderer = Minecraft.getInstance().worldRenderer;
	}

	public ClientWorld getWorld()
	{
		return this.world;
	}

	public WorldRenderer getWorldRenderer()
	{
		return this.worldRenderer;
	}
}

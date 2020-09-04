package com.legacy.structure_gel.events;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
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
public class RenderSkyEvent extends Event
{
	private final MatrixStack matrixStack;
	private final float partialTicks;
	private final World world;

	public RenderSkyEvent(MatrixStack matrixStack, float partialTicks)
	{
		this.matrixStack = matrixStack;
		this.partialTicks = partialTicks;
		this.world = Minecraft.getInstance().world;
	}

	public MatrixStack getMatrixStack()
	{
		return this.matrixStack;
	}

	public float getPartialTicks()
	{
		return this.partialTicks;
	}

	public World getWorld()
	{
		return this.world;
	}
}

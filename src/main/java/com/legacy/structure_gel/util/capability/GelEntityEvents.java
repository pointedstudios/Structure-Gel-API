package com.legacy.structure_gel.util.capability;

import com.legacy.structure_gel.StructureGelMod;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Bus.FORGE)
public class GelEntityEvents
{
	@SubscribeEvent
	public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event)
	{
		GelEntityProvider provider = new GelEntityProvider();
		event.addCapability(StructureGelMod.locate("gel_entity"), provider);
		event.addListener(provider::invalidate);
	}
}

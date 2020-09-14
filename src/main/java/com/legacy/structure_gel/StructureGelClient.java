package com.legacy.structure_gel;

import com.legacy.structure_gel.StructureGelMod.GelBlocks;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.client.gui.screen.ConfirmBackupScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class StructureGelClient
{
	public StructureGelClient()
	{
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::clientInit);
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(this::skipExperimentalBackupScreen);
	}
	
	@Internal
	public void clientInit(final FMLClientSetupEvent event)
	{
		GelBlocks.BLOCKS.forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getTranslucent()));
	}
	
	@Internal
	public void skipExperimentalBackupScreen(final GuiScreenEvent.DrawScreenEvent.Pre event)
	{
		if (event.getGui() instanceof ConfirmBackupScreen && StructureGelConfig.CLIENT.skipExperimentalScreen())
		{
			ConfirmBackupScreen gui = (ConfirmBackupScreen) event.getGui();
			if (gui.buttons.size() > 1 && gui.buttons.get(1) instanceof AbstractButton)
			{
				((AbstractButton) gui.buttons.get(1)).onPress();
				StructureGelMod.LOGGER.debug("Skipping backup request screen for world that uses experimental settings. You can disable this via config.");
			}
		}
	}
}

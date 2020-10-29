package com.legacy.structure_gel;

import java.util.List;

import com.legacy.structure_gel.StructureGelMod.GelBlocks;
import com.legacy.structure_gel.blocks.GelPortalBlock;
import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.util.capability.GelCapability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmBackupScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Structure Gel events to fire on the client thread.
 * 
 * @author David
 *
 */
@OnlyIn(Dist.CLIENT)
@Internal
public class SGClientEvents
{
	public static void init(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(SGClientEvents::clientInit);
		forgeBus.addListener(SGClientEvents::skipExperimentalBackupScreen);
		forgeBus.addListener(SGClientEvents::onPlaySound);
	}

	public static void clientInit(final FMLClientSetupEvent event)
	{
		GelBlocks.BLOCKS.forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getTranslucent()));
	}

	public static void skipExperimentalBackupScreen(final GuiScreenEvent.DrawScreenEvent.Post event)
	{
		if (StructureGelConfig.CLIENT.skipExperimentalScreen())
		{
			if (event.getGui() instanceof ConfirmBackupScreen)
			{
				ConfirmBackupScreen gui = (ConfirmBackupScreen) event.getGui();
				if (doesTitleMatch(gui.getTitle(), "selectWorld.backupQuestion.experimental") && hasButton(gui.buttons, 1))
				{
					StructureGelMod.LOGGER.info("Skipped backup request screen for world that uses experimental settings. You can disable this via config.");
					((AbstractButton) gui.buttons.get(1)).onPress();
				}
			}
			else if (event.getGui() instanceof ConfirmScreen)
			{
				ConfirmScreen gui = (ConfirmScreen) event.getGui();
				if (doesTitleMatch(gui.getTitle(), "selectWorld.backupQuestion.experimental") && hasButton(gui.buttons, 0))
				{
					StructureGelMod.LOGGER.info("Skipped world load warning screen for world that uses experimental settings. You can disable this via config.");
					((AbstractButton) gui.buttons.get(0)).onPress();
				}
			}
		}
	}

	private static boolean hasButton(List<Widget> buttons, int index)
	{
		return buttons.size() > index && buttons.get(index) instanceof AbstractButton;
	}

	private static boolean doesTitleMatch(ITextComponent title, String compare)
	{
		return title instanceof TranslationTextComponent && ((TranslationTextComponent) title).getKey().equals(compare);
	}

	public static void onPlaySound(final PlaySoundEvent event)
	{
		Minecraft mc = Minecraft.getInstance();
		GelCapability.ifPresent(mc.player, gelEntity ->
		{
			ResourceLocation name = event.getSound().getSoundLocation();
			GelPortalBlock portal = gelEntity.getPortalAudio();
			if (portal != null)
			{
				if (name.equals(SoundEvents.BLOCK_PORTAL_TRAVEL.getName()))
					event.setResultSound(portal.getTravelSound());
				else if (name.equals(SoundEvents.BLOCK_PORTAL_TRIGGER.getName()))
					event.setResultSound(portal.getTriggerSound());
			}
		});
	}
}

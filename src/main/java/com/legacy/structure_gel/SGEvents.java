package com.legacy.structure_gel;

import com.legacy.structure_gel.biome_dictionary.BiomeDictionary;
import com.legacy.structure_gel.biome_dictionary.BiomeType;
import com.legacy.structure_gel.commands.StructureGelCommand;
import com.legacy.structure_gel.packets.PacketHandler;
import com.legacy.structure_gel.packets.UpdateGelPlayerPacket;
import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.util.capability.GelCapability;
import com.legacy.structure_gel.util.capability.GelEntityProvider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * Structure Gel events to fire on both threads.
 * 
 * @author David
 *
 */
@Internal
public class SGEvents
{
	protected static void init(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(SGEvents::commonInit);
		modBus.addListener(SGEvents::loadComplete);
		modBus.addListener(SGEvents::createRegistries);
		forgeBus.addListener(SGEvents::registerCommands);
		forgeBus.addListener(SGEvents::onEntityJoinWorld);
		forgeBus.addGenericListener(Entity.class, SGEvents::attachCapabilities);
	}

	protected static void commonInit(final FMLCommonSetupEvent event)
	{
		GelCapability.register();
		PacketHandler.register();
	}

	protected static void loadComplete(final FMLLoadCompleteEvent event)
	{
		try
		{
			if (StructureGelConfig.COMMON.shouldGuessBiomeDict())
			{
				StructureGelMod.LOGGER.info("Attempting to register unregistered biomes to the biome dictionary. This can be disabled via config.");
				BiomeDictionary.makeGuess();
			}
		}
		catch (Exception e)
		{
			StructureGelMod.LOGGER.error("Encountered an issue while making assumptions for the biome dictionary. Please narrow down which mods cause a conflict here and report it to our issue tracker:");
			StructureGelMod.LOGGER.error("https://gitlab.com/modding-legacy/structure-gel-api/-/issues");
			e.printStackTrace();
		}
	}

	protected static void onEntityJoinWorld(final EntityJoinWorldEvent event)
	{
		if (event.getEntity() instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
			GelCapability.ifPresent(player, gelPlayer ->
			{
				PacketHandler.sendToClient(new UpdateGelPlayerPacket(gelPlayer), player);
			});
		}
	}

	protected static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event)
	{
		GelEntityProvider provider = new GelEntityProvider();
		event.addCapability(StructureGelMod.locate("gel_entity"), provider);
		event.addListener(provider::invalidate);
	}

	protected static void createRegistries(final RegistryEvent.NewRegistry event)
	{
		new RegistryBuilder<BiomeType>().setName(StructureGelMod.locate("biome_dictionary")).setType(BiomeType.class).setDefaultKey(StructureGelMod.locate("empty")).create();
	}

	protected static void registerCommands(final RegisterCommandsEvent event)
	{
		StructureGelCommand.register(event.getDispatcher());
	}
}

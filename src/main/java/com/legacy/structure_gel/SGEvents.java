package com.legacy.structure_gel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.legacy.structure_gel.biome_dictionary.BiomeDictionary;
import com.legacy.structure_gel.biome_dictionary.BiomeType;
import com.legacy.structure_gel.commands.StructureGelCommand;
import com.legacy.structure_gel.packets.PacketHandler;
import com.legacy.structure_gel.packets.UpdateGelPlayerPacket;
import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.util.RegistryHelper;
import com.legacy.structure_gel.util.capability.GelCapability;
import com.legacy.structure_gel.util.capability.GelEntityProvider;
import com.legacy.structure_gel.worldgen.structure.GelStructure;
import com.mojang.datafixers.DataFixerUpper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * Structure Gel events to fire on both threads.
 *
 * @author David
 */
@Internal
public class SGEvents
{
	private static boolean worldHasFakeDataFixer = false;

	protected static void init(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(SGEvents::commonInit);
		modBus.addListener(SGEvents::loadComplete);
		modBus.addListener(SGEvents::createRegistries);
		forgeBus.addListener(SGEvents::registerCommands);
		forgeBus.addListener(SGEvents::onEntityJoinWorld);
		forgeBus.addListener(SGEvents::worldLoad);
		forgeBus.addListener(SGEvents::serverStarted);
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
				BiomeDictionary.makeGuess().forEach((name, types) -> StructureGelMod.LOGGER.info(String.format("Registered %s to %s:[%s]", name, StructureGelMod.MODID, String.join(", ", types.stream().filter(BiomeDictionary::filterTypeForAutoRegister).map(bt -> bt.getRegistryName().getPath()).sorted().collect(Collectors.toSet())))));
			}
		}
		catch (Exception e)
		{
			StructureGelMod.LOGGER.error("Encountered an issue while making assumptions for the biome dictionary. Please narrow down which mods cause a conflict here and report it to our issue tracker: https://gitlab.com/modding-legacy/structure-gel-api/-/issues");
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

			if (worldHasFakeDataFixer && !StructureGelConfig.COMMON.silenceFakeDataFixerWarning())
				player.sendMessage(new TranslationTextComponent(String.format("info.structure_gel.fake_data_fixer.%s", ((ServerWorld) event.getWorld()).getServer().isSinglePlayer() ? "integrated" : "external"), "[Structure Gel API] ").mergeStyle(TextFormatting.YELLOW), Util.DUMMY_UUID);
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

	protected static void worldLoad(final WorldEvent.Load event)
	{
		if (event.getWorld() instanceof ServerWorld)
		{
			worldHasFakeDataFixer = !DataFixesManager.getDataFixer().getClass().equals(DataFixerUpper.class);
			ServerWorld world = (ServerWorld) event.getWorld();

			// Get from settings
			Map<Structure<?>, StructureSeparationSettings> settingsMap = new HashMap<>(world.getChunkProvider().getChunkGenerator().func_235957_b_().field_236193_d_);

			// Add to settings map
			RegistryHelper.STRUCTURE_SETTINGS_MAP.forEach((structure, settings) ->
			{
				if (structure instanceof GelStructure)
				{
					Set<ResourceLocation> dims = ((GelStructure<?>) structure).getValidDimensions();
					if (dims == null || dims.contains(world.getDimensionKey().getLocation()))
						settingsMap.put(structure, settings);
				}
				else
					settingsMap.put(structure, settings);
			});

			// Set into settings
			// getDimSettings.instanceMap
			world.getChunkProvider().getChunkGenerator().func_235957_b_().field_236193_d_ = settingsMap;

		}
	}

	protected static void serverStarted(final FMLServerStartedEvent event)
	{
		if (worldHasFakeDataFixer)
			StructureGelMod.LOGGER.warn(new TranslationTextComponent(String.format("info.structure_gel.fake_data_fixer.%s", event.getServer().isSinglePlayer() && !event.getServer().getPublic() ? "integrated" : "external"), "").mergeStyle(TextFormatting.YELLOW).getString());
	}
}

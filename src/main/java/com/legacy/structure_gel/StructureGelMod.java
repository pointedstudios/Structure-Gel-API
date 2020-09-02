package com.legacy.structure_gel;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import com.legacy.structure_gel.blocks.AxisStructureGelBlock;
import com.legacy.structure_gel.blocks.IStructureGel.Behavior;
import com.legacy.structure_gel.blocks.StructureGelBlock;
import com.legacy.structure_gel.commands.GetSpawnsCommand;
import com.legacy.structure_gel.data.BiomeDictionaryManager;
import com.legacy.structure_gel.items.StructureGelItem;
import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.util.RegistryHelper;
import com.legacy.structure_gel.worldgen.jigsaw.GelJigsawPiece;
import com.legacy.structure_gel.worldgen.jigsaw.GelStructurePiece;
import com.legacy.structure_gel.worldgen.processors.RandomBlockSwapProcessor;
import com.legacy.structure_gel.worldgen.processors.RandomStateSwapProcessor;
import com.legacy.structure_gel.worldgen.processors.RandomTagSwapProcessor;
import com.legacy.structure_gel.worldgen.processors.RemoveGelStructureProcessor;
import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * This is an API mod designed with the purpose of simplifying the work required
 * to create generated structures, particularly with jigsaws. All methods and
 * classes that you will interact with are documented with how they function.
 * 
 * @author David
 *
 */
@Mod(StructureGelMod.MODID)
public class StructureGelMod implements IBiomeDictionary
{
	public static final String MODID = "structure_gel";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final BiomeDictionaryManager biomeDictionary = new BiomeDictionaryManager();
	@Internal
	private static final Type MOD_ANNOTATE = Type.getType(Mod.class);

	public StructureGelMod()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureGelConfig.COMMON_SPEC);
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::clientInit);
		modBus.addListener(this::commonInit);
		modBus.addGenericListener(Biome.class, this::registerBiomeDictionary);
		// modBus.addListener(this::gatherData);
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(this::registerCommands);
		// forgeBus.addListener(this::registerDataPack);
	}

	@Override
	public void getBiomes()
	{
	}

	public void clientInit(final FMLClientSetupEvent event)
	{
		Blocks.BLOCKS.forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getTranslucent()));
	}

	public void commonInit(final FMLCommonSetupEvent event)
	{

	}

	public void registerCommands(final RegisterCommandsEvent event)
	{
		GetSpawnsCommand.register(event.getDispatcher());
	}

	/*public void registerDataPack(final AddReloadListenerEvent event)
	{
		event.addListener(StructureGelMod.biomeDictionary);
	}
	
	public void gatherData(final GatherDataEvent event)
	{
		DataGenerator gen = event.getGenerator();
		gen.addProvider(new BiomeDictionaryProvider(gen));
	}*/

	public static ResourceLocation locate(String key)
	{
		return new ResourceLocation(MODID, key);
	}

	public void registerBiomeDictionary(final RegistryEvent.Register<Biome> event)
	{
		StructureGelMod.LOGGER.info("Setting up Biome Dictionary");
		ModList.get().forEachModContainer((s, mc) ->
		{
			if (mc instanceof FMLModContainer)
			{
				StructureGelMod.LOGGER.info("Found mod container for " + s);
				FMLModContainer fmlContainer = (FMLModContainer) mc;
				if (fmlContainer.getMod() instanceof IBiomeDictionary)
				{
					StructureGelMod.LOGGER.info("Found mod container class : " + fmlContainer.getMod().getClass().getName());
					((IBiomeDictionary) fmlContainer.getMod()).getBiomes();
				}
				else
					StructureGelMod.LOGGER.info("This was not a biome dictionary instance : " + fmlContainer.getMod().getClass().getName());
			}
		});
	}

	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Blocks
	{
		public static Set<Block> BLOCKS = new LinkedHashSet<Block>();
		public static Block RED_GEL, BLUE_GEL, GREEN_GEL, CYAN_GEL, ORANGE_GEL, YELLOW_GEL;

		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Block> event)
		{
			IForgeRegistry<Block> registry = event.getRegistry();
			RED_GEL = registerBlock(registry, "red_gel", new StructureGelBlock());
			BLUE_GEL = registerBlock(registry, "blue_gel", new StructureGelBlock(Behavior.PHOTOSENSITIVE));
			GREEN_GEL = registerBlock(registry, "green_gel", new StructureGelBlock(Behavior.DIAGONAL_SPREAD));
			CYAN_GEL = registerBlock(registry, "cyan_gel", new StructureGelBlock(Behavior.PHOTOSENSITIVE, Behavior.DIAGONAL_SPREAD));
			ORANGE_GEL = registerBlock(registry, "orange_gel", new StructureGelBlock(Behavior.DYNAMIC_SPREAD_DIST));
			YELLOW_GEL = registerBlock(registry, "yellow_gel", new AxisStructureGelBlock(Behavior.AXIS_SPREAD));
		}

		private static Block registerBlock(IForgeRegistry<Block> registry, String key, Block object)
		{
			BLOCKS.add(object);
			return RegistryHelper.register(registry, StructureGelMod.locate(key), object);
		}
	}

	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Items
	{
		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Item> event)
		{
			StructureGelMod.Blocks.BLOCKS.forEach(b -> RegistryHelper.register(event.getRegistry(), b.getRegistryName(), new StructureGelItem((StructureGelBlock) b)));
		}
	}

	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class StructureRegistry
	{
		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Structure<?>> event)
		{
			registerProcessors();
			registerDeserializers();
			registerStructurePieces();
		}

		private static void registerProcessors()
		{
			Processors.REMOVE_FILLER = Processors.register("remove_filler", RemoveGelStructureProcessor.CODEC);
			Processors.REPLACE_BLOCK = Processors.register("replace_block", RandomBlockSwapProcessor.CODEC);
			Processors.REPLACE_TAG = Processors.register("replace_tag", RandomTagSwapProcessor.CODEC);
			Processors.REPLACE_STATE = Processors.register("replace_state", RandomStateSwapProcessor.CODEC);
		}

		private static void registerDeserializers()
		{
			JigsawDeserializers.GEL_SINGLE_POOL_ELEMENT = JigsawDeserializers.register("gel_single_pool_element", GelJigsawPiece.CODEC);
		}

		private static void registerStructurePieces()
		{
			StructurePieceTypes.GEL_JIGSAW = RegistryHelper.registerStructurePiece(locate("gel_jigsaw"), GelStructurePiece::new);
		}
	}

	public static class Processors
	{
		public static IStructureProcessorType<RemoveGelStructureProcessor> REMOVE_FILLER;
		public static IStructureProcessorType<RandomBlockSwapProcessor> REPLACE_BLOCK;
		public static IStructureProcessorType<RandomTagSwapProcessor> REPLACE_TAG;
		public static IStructureProcessorType<RandomStateSwapProcessor> REPLACE_STATE;

		protected static <P extends StructureProcessor> IStructureProcessorType<P> register(String key, Codec<P> codec)
		{
			return Registry.register(Registry.STRUCTURE_PROCESSOR, locate(key), () -> codec);
		}
	}

	public static class JigsawDeserializers
	{
		public static IJigsawDeserializer<GelJigsawPiece> GEL_SINGLE_POOL_ELEMENT;

		protected static <P extends JigsawPiece> IJigsawDeserializer<P> register(String key, Codec<P> codec)
		{
			return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, locate(key), () -> codec);
		}
	}

	public static class StructurePieceTypes
	{
		public static IStructurePieceType GEL_JIGSAW;
	}
}

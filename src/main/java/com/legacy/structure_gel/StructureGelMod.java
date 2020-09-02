package com.legacy.structure_gel;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.legacy.structure_gel.blocks.AxisStructureGelBlock;
import com.legacy.structure_gel.blocks.IStructureGel.Behavior;
import com.legacy.structure_gel.blocks.StructureGelBlock;
import com.legacy.structure_gel.commands.GetSpawnsCommand;
import com.legacy.structure_gel.data.BiomeDictionary;
import com.legacy.structure_gel.data.BiomeDictionary.BiomeType;
import com.legacy.structure_gel.data.BiomeDictionaryEvent;
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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
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
public class StructureGelMod
{
	public static final String MODID = "structure_gel";
	public static final Logger LOGGER = LogManager.getLogger();
	private static final String[] BIOME_DICT_METHODS = new String[] { "getBiomesSG" };

	@Internal
	public StructureGelMod()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureGelConfig.COMMON_SPEC);
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::clientInit);
		modBus.addListener(this::commonInit);
		modBus.addGenericListener(Biome.class, this::registerBiomeDictionary);
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(this::registerCommands);
		
		forgeBus.addListener(this::registerBiomeDict);
	}
	
	@SuppressWarnings("unchecked")
	public void registerBiomeDict(final BiomeDictionaryEvent event)
	{
		System.out.println("STRUCTURE GEL Fired event");
		event.register(BiomeType.create(locate("tower_biomes")).biomes(Biomes.PLAINS, Biomes.FOREST, Biomes.DARK_FOREST, Biomes.BIRCH_FOREST, Biomes.MOUNTAINS));
		event.register(BiomeType.create(locate("leviathan_biomes")).biomes(Biomes.DESERT));
		event.register(BiomeType.create(locate("snowy_temple_biomes")).biomes(Biomes.SNOWY_TUNDRA, Biomes.SNOWY_TAIGA));
		event.register(BiomeType.create(locate("bigger_dungeon_biomes")).parents(BiomeDictionary.OVERWORLD));
		event.register(BiomeType.create(locate("end_ruins_biomes")).parents(BiomeDictionary.OUTER_END_ISLAND));
	}
	
	/**
	 * Create a method with the same name, arguments, and return type as this in
	 * your main mod class (annotated with {@link Mod}) to register your own biome
	 * dictionary entries. Structure Gel does not need to be a dependency for this
	 * to work. <br>
	 * <br>
	 * Returns a list containing the required data to register a biome dictionary
	 * entry. The {@link org.apache.commons.lang3.tuple.Triple} contains a
	 * {@link ResourceLocation} for the registry name, a {@link Set} of resource
	 * locations for parenting biome types that this should inherit biomes from, and
	 * a Set of biome {@link RegistryKey}s for what biomes should be in the
	 * dictionary entry.
	 * 
	 * @return {@link List}<{@link Triple}<{@link ResourceLocation},
	 *         {@link Set}<{@link ResourceLocation}>,
	 *         {@link Set}<{@link RegistryKey}<{@link Biome}>>>>
	 */
	public List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>> getBiomesSG()
	{
		/*
		List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>> list = new ArrayList<>();
		
		list.add(Triple.of(locate("plains"), ImmutableSet.of(locate("oak_forest"), new ResourceLocation("silly_willy")), ImmutableSet.of(Biomes.DESERT)));
		
		return list;
		*/
		return null;
	}

	@Internal
	public void clientInit(final FMLClientSetupEvent event)
	{
		Blocks.BLOCKS.forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getTranslucent()));
	}

	@Internal
	public void commonInit(final FMLCommonSetupEvent event)
	{

	}

	@Internal
	public void registerCommands(final RegisterCommandsEvent event)
	{
		GetSpawnsCommand.register(event.getDispatcher());
	}

	@Internal
	public static ResourceLocation locate(String key)
	{
		return new ResourceLocation(MODID, key);
	}

	@Internal
	@SuppressWarnings("unchecked")
	public void registerBiomeDictionary(final RegistryEvent.Register<Biome> event)
	{
		StructureGelMod.LOGGER.debug("Setting up Biome Dictionary.");
		BiomeDictionary.init();
		// Get biome dictionary entries from mod classes
		ModList.get().forEachModContainer((s, mc) ->
		{
			if (mc instanceof FMLModContainer)
			{
				FMLModContainer fmlContainer = (FMLModContainer) mc;

				try
				{
					for (Method method : fmlContainer.getMod().getClass().getMethods())
					{
						List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>> result = Lists.newArrayList();

						// 1.16.2-v1.3.0
						if (method.getName().equals(BIOME_DICT_METHODS[0]))
							result = (List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>>) method.invoke(fmlContainer.getMod());

						// Register
						if (result != null && !result.isEmpty())
							result.forEach(t -> BiomeDictionary.register(new BiomeType(t.getLeft(), t.getMiddle(), t.getRight())));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
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

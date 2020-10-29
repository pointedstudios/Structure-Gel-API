package com.legacy.structure_gel;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.legacy.structure_gel.biome_dictionary.BiomeDictionary;
import com.legacy.structure_gel.biome_dictionary.BiomeType;
import com.legacy.structure_gel.blocks.AxisStructureGelBlock;
import com.legacy.structure_gel.blocks.IStructureGel.Behavior;
import com.legacy.structure_gel.blocks.StructureGelBlock;
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
import net.minecraft.item.Item;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * This is an API with the purpose of giving access and shortcuts to various
 * aspects of worldgen, such as structures, biomes, and dimensions. Methods and
 * classes are documented with details on how they work and where to use them.
 * Anywhere that you see the {@link Internal} annotation means that you
 * shouldn't need to call the thing annotated.<br>
 * <br>
 * In order to get the Mixins working in your workspace, add<br>
 * arg '-mixin.config=structure_gel.mixins.json'<br>
 * to your run configurations.<br>
 * <br>
 * If you encounter issues or find any bugs, please report them to the issue
 * tracker. https://gitlab.com/modding-legacy/structure-gel-api/-/issues
 * 
 * @author David
 *
 */
@Mod(StructureGelMod.MODID)
public class StructureGelMod
{
	public static final String MODID = "structure_gel";
	public static final Logger LOGGER = LogManager.getLogger();

	public StructureGelMod()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureGelConfig.COMMON_SPEC);

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;

		SGEvents.init(modBus, forgeBus);
		modBus.addGenericListener(BiomeType.class, StructureGelMod::registerBiomeDictionary);
		modBus.addGenericListener(Block.class, GelBlocks::onRegistry);
		modBus.addGenericListener(Item.class, GelItems::onRegistry);
		modBus.addGenericListener(Structure.class, StructureRegistry::onRegistry);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
		{
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, StructureGelConfig.CLIENT_SPEC);

			com.legacy.structure_gel.SGClientEvents.init(modBus, forgeBus);
		});

		// Debugging stuff
		// com.legacy.structure_gel.SGDebug.init(modBus, forgeBus);
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
	 * @return {@link List}&lt;{@link Triple}&lt;{@link ResourceLocation},
	 *         {@link Set}&lt;{@link ResourceLocation}&gt;,
	 *         {@link Set}&lt;{@link RegistryKey}&lt;{@link Biome}&gt;&gt;&gt;&gt;
	 */
	public List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>> getBiomesSG()
	{
		/*
		List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>> list = new ArrayList<>();
		
		list.add(Triple.of(locate("plains"), ImmutableSet.of(new ResourceLocation(MODID, "oak_forest")), ImmutableSet.of(Biomes.DESERT)));
		
		return list;
		*/
		return null;
	}

	@Internal
	public static ResourceLocation locate(String key)
	{
		return new ResourceLocation(MODID, key);
	}

	// -------------------------- REGISTRY

	// TODO move to own class in 1.17
	@Internal
	@SuppressWarnings("unchecked")
	public static void registerBiomeDictionary(final RegistryEvent.Register<BiomeType> event)
	{
		BiomeDictionary.init();
		// Get biome dictionary entries from other mods' classes
		LOGGER.info("Checking for other mods' biome dictionary methods.");
		try
		{
			ModList.get().forEachModContainer((s, mc) ->
			{
				if (mc instanceof FMLModContainer)
				{
					FMLModContainer fmlContainer = (FMLModContainer) mc;
					Method[] methods = new Method[0];
					try
					{
						methods = fmlContainer.getMod().getClass().getMethods();
					}
					catch (Exception e)
					{
						// If it fails, just silently move on without it. Likely caused by trying to access client classes on server.
					}
					for (Method method : methods)
					{
						List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>> result = Lists.newArrayList();

						// As of 1.16.2-v1.3.0
						if (method.getName().equals("getBiomesSG"))
						{
							try
							{
								result = (List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>>) method.invoke(fmlContainer.getMod());
							}
							catch (Exception e)
							{
								LOGGER.info(String.format("Failed to invoke getBiomesSG from %s. Proceeding to the next mod.", s));
								e.printStackTrace();
							}
						}

						// Register
						if (result != null && !result.isEmpty())
							result.forEach(t -> BiomeDictionary.register(new BiomeType(t.getLeft(), t.getMiddle(), t.getRight())));
					}
				}
			});
		}
		catch (Exception e)
		{
			LOGGER.info("Encountered an issue trying to load other mods' biome dictionary methods. Skipping this step.");
			e.printStackTrace();
		}
	}

	// TODO move to own class in 1.17
	public static class GelBlocks
	{
		public static Set<Block> BLOCKS = new LinkedHashSet<Block>();
		public static StructureGelBlock RED_GEL, BLUE_GEL, GREEN_GEL, CYAN_GEL, ORANGE_GEL, YELLOW_GEL;

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

		private static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, String key, T object)
		{
			BLOCKS.add(object);
			return RegistryHelper.registerExact(registry, StructureGelMod.locate(key), object);
		}
	}

	// TODO move to own class in 1.17
	public static class GelItems
	{
		public static void onRegistry(final RegistryEvent.Register<Item> event)
		{
			StructureGelMod.GelBlocks.BLOCKS.forEach(b -> RegistryHelper.registerExact(event.getRegistry(), b.getRegistryName(), new StructureGelItem((StructureGelBlock) b)));
		}
	}

	// TODO move to own class in 1.17
	public static class StructureRegistry
	{
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

	// TODO move to own class in 1.17
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

	// TODO move to own class in 1.17
	public static class JigsawDeserializers
	{
		public static IJigsawDeserializer<GelJigsawPiece> GEL_SINGLE_POOL_ELEMENT;

		protected static <P extends JigsawPiece> IJigsawDeserializer<P> register(String key, Codec<P> codec)
		{
			return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, locate(key), () -> codec);
		}
	}

	// TODO move to own class in 1.17
	public static class StructurePieceTypes
	{
		public static IStructurePieceType GEL_JIGSAW;
	}
}

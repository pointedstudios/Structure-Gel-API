package com.legacy.structure_gel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.legacy.structure_gel.blocks.AxisStructureGelBlock;
import com.legacy.structure_gel.blocks.IStructureGel.Behavior;
import com.legacy.structure_gel.blocks.StructureGelBlock;
import com.legacy.structure_gel.data.JsonStructure;
import com.legacy.structure_gel.data.StructureData;
import com.legacy.structure_gel.items.StructureGelItem;
import com.legacy.structure_gel.structures.jigsaw.GelJigsawPiece;
import com.legacy.structure_gel.structures.processors.RandomBlockSwapProcessor;
import com.legacy.structure_gel.structures.processors.RandomStateSwapProcessor;
import com.legacy.structure_gel.structures.processors.RandomTagSwapProcessor;
import com.legacy.structure_gel.structures.processors.RemoveGelStructureProcessor;
import com.legacy.structure_gel.util.RegistryHelper;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
	public static final String NAME = "Structure Gel API";
	public static final String MODID = "structure_gel";
	public static final String VERSION = "1.1.0";
	public static final Logger LOGGER = LogManager.getLogger();

	public StructureGelMod()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureGelConfig.COMMON_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
	}

	public void clientInit(final FMLClientSetupEvent event)
	{
		Blocks.BLOCKS.forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getTranslucent()));
	}

	public void commonInit(final FMLCommonSetupEvent event)
	{
		FeatureRegistry.STRUCTURES.forEach(structure ->
		{
			if (structure instanceof JsonStructure)
			{
				((JsonStructure) structure).data.biomes.forEach(biome ->
				{
					RegistryHelper.addFeature(biome, Decoration.SURFACE_STRUCTURES, structure);
					RegistryHelper.addStructure(biome, structure);
				});
			}
		});
	}

	public static ResourceLocation locate(String key)
	{
		return new ResourceLocation(MODID, key);
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
	public static class FeatureRegistry
	{
		public static ImmutableList<Structure<NoFeatureConfig>> STRUCTURES = ImmutableList.of();
		public static IStructurePieceType JSON_PIECE;

		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Feature<?>> event)
		{
			FeatureRegistry.JSON_PIECE = RegistryHelper.registerStructurePiece(locate("json_piece"), JsonStructure.JsonPieces.Piece::new);
			registerProcessors(event);
			registerDeserializers(event);
			registerStructures(event);
		}

		private static void registerProcessors(final RegistryEvent.Register<Feature<?>> event)
		{
			Processors.REMOVE_FILLER = Processors.register("remove_filler", (dyn) -> RemoveGelStructureProcessor.INSTANCE);
			Processors.REPLACE_BLOCK = Processors.register("replace_block", RandomBlockSwapProcessor::new);
			Processors.REPLACE_TAG = Processors.register("replace_tag", RandomTagSwapProcessor::new);
			Processors.REPLACE_STATE = Processors.register("replace_state", RandomStateSwapProcessor::new);
		}

		private static void registerDeserializers(final RegistryEvent.Register<Feature<?>> event)
		{
			JigsawDeserializers.GEL_SINGLE_POOL_ELEMENT = JigsawDeserializers.register("gel_single_pool_element", GelJigsawPiece::new);
		}

		private static void registerStructures(final RegistryEvent.Register<Feature<?>> event)
		{
			List<Structure<NoFeatureConfig>> structures = new ArrayList<>();
			getJsonStructures().forEach((path, json) ->
			{
				try
				{
					StructureData data = StructureData.parse(json, path);
					structures.add(RegistryHelper.registerStructure(event.getRegistry(), data.registryName, new JsonStructure(data)));
					StructureGelMod.LOGGER.info(String.format("Registered \"%s\" from \"%s\"", data.registryName, path));
				}
				catch (JsonSyntaxException e)
				{
					StructureGelMod.LOGGER.error(String.format("Could not load the json structure \"%s\". It was either formatted wrong or required data is missing.", path));
					e.printStackTrace();
				}
			});
			FeatureRegistry.STRUCTURES = ImmutableList.copyOf(structures);
		}

		private static Map<String, JsonObject> getJsonStructures()
		{
			Gson gson = new Gson();
			Map<String, JsonObject> jsons = new HashMap<>();
			List<String> paths = getFilePaths("json_structures");
			paths.forEach(path ->
			{
				try
				{
					jsons.put(path, gson.fromJson(new FileReader(path), JsonObject.class));
				}
				catch (JsonSyntaxException | JsonIOException | FileNotFoundException e)
				{
					StructureGelMod.LOGGER.error(String.format("Failed to load the structure \"%s\"", path));
					e.printStackTrace();
				}
			});

			return jsons;
		}

		private static List<String> getFilePaths(String path)
		{
			List<String> paths = new ArrayList<>();
			for (File file : new File(path).listFiles())
			{
				if (!file.isDirectory())
					paths.add(file.getPath());
				else
					getFilePaths(file.getPath());
			}

			return paths;
		}
	}

	public static class Processors
	{
		public static IStructureProcessorType REMOVE_FILLER;
		public static IStructureProcessorType REPLACE_BLOCK;
		public static IStructureProcessorType REPLACE_TAG;
		public static IStructureProcessorType REPLACE_STATE;

		protected static IStructureProcessorType register(String key, IStructureProcessorType type)
		{
			return Registry.register(Registry.STRUCTURE_PROCESSOR, locate(key), type);
		}
	}

	public static class JigsawDeserializers
	{
		public static IJigsawDeserializer GEL_SINGLE_POOL_ELEMENT;

		protected static IJigsawDeserializer register(String key, IJigsawDeserializer type)
		{
			return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, locate(key), type);
		}
	}
}

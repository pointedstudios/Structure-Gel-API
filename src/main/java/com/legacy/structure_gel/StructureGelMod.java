package com.legacy.structure_gel;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.legacy.structure_gel.blocks.AxisStructureGelBlock;
import com.legacy.structure_gel.blocks.IStructureGel.Behavior;
import com.legacy.structure_gel.blocks.StructureGelBlock;
import com.legacy.structure_gel.items.StructureGelItem;
import com.legacy.structure_gel.structures.jigsaw.GelJigsawPiece;
import com.legacy.structure_gel.structures.processors.RandomBlockSwapProcessor;
import com.legacy.structure_gel.structures.processors.RandomStateSwapProcessor;
import com.legacy.structure_gel.structures.processors.RandomTagSwapProcessor;
import com.legacy.structure_gel.structures.processors.RemoveGelStructureProcessor;
import com.legacy.structure_gel.util.RegistryHelper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
	public static final String VERSION = "1.0.0";
	public static final Logger LOGGER = LogManager.getLogger();

	public StructureGelMod()
	{
		
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
			for (Block b : StructureGelMod.Blocks.BLOCKS)
				if (b instanceof StructureGelBlock)
					RegistryHelper.register(event.getRegistry(), b.getRegistryName(), new StructureGelItem((StructureGelBlock) b));
		}
	}

	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Processors
	{
		public static IStructureProcessorType REMOVE_FILLER;
		public static IStructureProcessorType REPLACE_BLOCK;
		public static IStructureProcessorType REPLACE_TAG;
		public static IStructureProcessorType REPLACE_STATE;

		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Feature<?>> event)
		{
			REMOVE_FILLER = register("remove_filler", (dyn) -> RemoveGelStructureProcessor.INSTANCE);
			REPLACE_BLOCK = register("replace_block", RandomBlockSwapProcessor::new);
			REPLACE_TAG = register("replace_tag", RandomTagSwapProcessor::new);
			REPLACE_STATE = register("replace_state", RandomStateSwapProcessor::new);
		}

		private static IStructureProcessorType register(String key, IStructureProcessorType type)
		{
			return Registry.register(Registry.STRUCTURE_PROCESSOR, locate(key), type);
		}
	}

	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class JigsawDeserializers
	{
		public static IJigsawDeserializer GEL_SINGLE_POOL_ELEMENT;

		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Feature<?>> event)
		{
			GEL_SINGLE_POOL_ELEMENT = register("gel_single_pool_element", GelJigsawPiece::new);
		}

		private static IJigsawDeserializer register(String key, IJigsawDeserializer type)
		{
			return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, locate(key), type);
		}
	}
}

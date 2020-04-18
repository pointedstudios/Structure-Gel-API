package com.legacy.structure_gel;

import java.util.LinkedHashSet;
import java.util.Set;

import com.legacy.structure_gel.blocks.IStructureGel.Behavior;
import com.legacy.structure_gel.blocks.StructureGelBlock;
import com.legacy.structure_gel.items.StructureGelItem;
import com.legacy.structure_gel.structures.jigsaw.GelJigsawPiece;
import com.legacy.structure_gel.structures.processors.RandomBlockSwapProcessor;
import com.legacy.structure_gel.structures.processors.RemoveGelStructureProcessor;

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
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod(StructureGelMod.MODID)
public class StructureGelMod
{
	public static final String NAME = "Structure Gel API";
	public static final String MODID = "structure_gel";
	public static final String VERSION = "1.0.0";

	public StructureGelMod()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
	}

	private void commonInit(final FMLCommonSetupEvent event)
	{

	}

	private void clientInit(final FMLClientSetupEvent event)
	{

	}

	public static ResourceLocation locate(String key)
	{
		return new ResourceLocation(MODID, key);
	}

	public static String find(String key)
	{
		return new String(MODID + ":" + key);
	}

	public static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, String name, T object)
	{
		object.setRegistryName(StructureGelMod.locate(name));
		registry.register(object);
		return object;
	}

	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Blocks
	{
		public static Set<Block> GELS = new LinkedHashSet<Block>();
		
		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Block> event)
		{
			registerBlock(event.getRegistry(), "red_gel", new StructureGelBlock());
			registerBlock(event.getRegistry(), "blue_gel", new StructureGelBlock(Behavior.PHOTOSENSITIVE));
			registerBlock(event.getRegistry(), "green_gel", new StructureGelBlock(Behavior.DIAGONAL_SPREAD));
			registerBlock(event.getRegistry(), "cyan_gel", new StructureGelBlock(Behavior.PHOTOSENSITIVE, Behavior.DIAGONAL_SPREAD));
			registerBlock(event.getRegistry(), "orange_gel", new StructureGelBlock(Behavior.DYNAMIC_SPREAD_DIST));
		}
		
		public static void registerBlock(IForgeRegistry<Block> registry, String name, Block object)
		{
			GELS.add(object);
			register(registry, name, object);
		}
	}

	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Items
	{
		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Item> event)
		{
			for (Block b : StructureGelMod.Blocks.GELS)
				register(event.getRegistry(), b.getRegistryName().getPath(), new StructureGelItem(b));
			
			StructureGelMod.Blocks.GELS.clear();
		}
	}
	
	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Processors
	{
		public static IStructureProcessorType REMOVE_FILLER;
		public static IStructureProcessorType REPLACE_RANDOMLY;

		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Feature<?>> event)
		{
			REMOVE_FILLER = register("remove_filler", (dyn) -> RemoveGelStructureProcessor.INSTANCE);
			REPLACE_RANDOMLY = register("replace_randomly", RandomBlockSwapProcessor::new);
		}

		static IStructureProcessorType register(String key, IStructureProcessorType type)
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

		static IJigsawDeserializer register(String key, IJigsawDeserializer type)
		{
			return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, locate(key), type);
		}
	}
}

package com.legacy.structure_gel;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.legacy.structure_gel.blocks.AxisStructureGelBlock;
import com.legacy.structure_gel.blocks.IStructureGel.Behavior;
import com.legacy.structure_gel.blocks.StructureGelBlock;
import com.legacy.structure_gel.items.StructureGelItem;
import com.legacy.structure_gel.structures.GelStructure;
import com.legacy.structure_gel.structures.GelStructurePiece;
import com.legacy.structure_gel.structures.GelStructureStart;
import com.legacy.structure_gel.structures.jigsaw.GelJigsawPiece;
import com.legacy.structure_gel.structures.jigsaw.JigsawRegistryHelper;
import com.legacy.structure_gel.structures.processors.RandomBlockSwapProcessor;
import com.legacy.structure_gel.structures.processors.RandomStateSwapProcessor;
import com.legacy.structure_gel.structures.processors.RandomTagSwapProcessor;
import com.legacy.structure_gel.structures.processors.RemoveGelStructureProcessor;
import com.legacy.structure_gel.util.RegistryHelper;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
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
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureGelConfig.COMMON_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::cmn);
	}

	public void clientInit(final FMLClientSetupEvent event)
	{
		Blocks.BLOCKS.forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getTranslucent()));
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

	// TODO REMOVE
	public void cmn(final FMLCommonSetupEvent event)
	{
		for (Biome biome : ForgeRegistries.BIOMES.getValues())
		{
			RegistryHelper.addFeature(biome, Decoration.SURFACE_STRUCTURES, Features.ZOMBIE.getFirst());
			RegistryHelper.addStructure(biome, Features.ZOMBIE.getFirst());
		}
	}

	// TODO Remove
	@Mod.EventBusSubscriber(modid = StructureGelMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Features
	{
		public static Pair<Structure<NoFeatureConfig>, IStructurePieceType> ZOMBIE;

		@SubscribeEvent
		public static void onRegistry(final RegistryEvent.Register<Feature<?>> event)
		{
			ZOMBIE = RegistryHelper.registerStructureAndPiece(event.getRegistry(), locate("zombie"), new Zombie(NoFeatureConfig::deserialize), Zombie.Piece::new);
		}
	}

	// TODO Remove
	public static class Zombie extends GelStructure<NoFeatureConfig>
	{

		public Zombie(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
		{
			super(configFactoryIn);
		}

		@Override
		public int getSeed()
		{
			return 973181;
		}

		@Override
		public IStartFactory getStartFactory()
		{
			return Zombie.Start::new;
		}

		@Override
		public int getSize()
		{
			return 1;
		}

		@Override
		public double getProbability()
		{
			return 1;
		}

		@Override
		public int getSpacing()
		{
			return 2;
		}

		@Override
		public int getOffset()
		{
			return 0;
		}

		public static class Start extends GelStructureStart
		{

			public Start(Structure<?> structureIn, int chunkX, int chunkZ, MutableBoundingBox boundsIn, int referenceIn, long seed)
			{
				super(structureIn, chunkX, chunkZ, boundsIn, referenceIn, seed);
			}

			@Override
			public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn)
			{
				Zombie.assemble(generator, templateManagerIn, new BlockPos(chunkX * 16, 90, chunkZ * 16), this.components, this.rand);
				this.recalculateStructureSize();
			}
		}

		public static void assemble(ChunkGenerator<?> chunkGen, TemplateManager template, BlockPos pos, List<StructurePiece> pieces, SharedSeedRandom seed)
		{
			JigsawManager.func_214889_a(StructureGelMod.locate("zombie/zombie"), 7, Zombie.Piece::new, chunkGen, template, pos, pieces, seed);
		}

		static
		{
			JigsawRegistryHelper registry = new JigsawRegistryHelper(StructureGelMod.MODID, "zombie/");
			registry.register("zombie", registry.builder().names("zombie").build());
		}

		public static class Piece extends GelStructurePiece
		{
			public Piece(TemplateManager template, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox boundingBox)
			{
				super(StructureGelMod.Features.ZOMBIE.getSecond(), template, jigsawPiece, pos, groundLevelDelta, rotation, boundingBox);
			}

			public Piece(TemplateManager template, CompoundNBT nbt)
			{
				super(template, nbt, StructureGelMod.Features.ZOMBIE.getSecond());
			}

			@Override
			public void handleDataMarker(String key, IWorld worldIn, BlockPos pos, Random rand, MutableBoundingBox bounds)
			{

			}
		}
	}
}

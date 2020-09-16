package com.legacy.structure_gel;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.access_helpers.BiomeAccessHelper;
import com.legacy.structure_gel.access_helpers.DimensionAccessHelper;
import com.legacy.structure_gel.blocks.GelPortalBlock;
import com.legacy.structure_gel.events.RegisterDimensionEvent;
import com.legacy.structure_gel.registrars.DimensionRegistrar;
import com.legacy.structure_gel.registrars.StructureRegistrar;
import com.legacy.structure_gel.util.DimensionTypeBuilder;
import com.legacy.structure_gel.util.GelTeleporter;
import com.legacy.structure_gel.util.RegistryHelper;
import com.legacy.structure_gel.worldgen.structure.GelStructure;

/*import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;*/

/**
 * Contains a bunch of debug code for testing or examples. This may be commented
 * out.
 * 
 * @author David
 *
 */
@com.legacy.structure_gel.util.Internal
public class SGDebug
{
	/*public static void init(net.minecraftforge.eventbus.api.IEventBus modBus, net.minecraftforge.eventbus.api.IEventBus forgeBus)
	{

		forgeBus.addListener(SGDebug::registerDim);
		forgeBus.addListener(SGDebug::spawnPortal);

		modBus.addGenericListener(Block.class, SGDebug::registerBlocks);
		modBus.addGenericListener(PointOfInterestType.class, SGDebug::registerPOI);
		modBus.addGenericListener(Structure.class, SGDebug::registerStructure);
		modBus.addListener(SGDebug::commonInit);

	}

	// Dimension registry
	public static RegistryKey<World> CUSTOM_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, StructureGelMod.locate("custom"));

	public static void registerDim(RegisterDimensionEvent event)
	{
		Function<RegistryKey<DimensionSettings>, DimensionSettings> settings = (rk) -> DimensionAccessHelper.newFloatingIslandSettings(new DimensionStructuresSettings(true), Blocks.STONE.getDefaultState(), Blocks.AIR.getDefaultState(), CUSTOM_WORLD.getLocation(), false, false);

		BiFunction<RegisterDimensionEvent, DimensionSettings, ChunkGenerator> generator = (evnt, sttngs) -> new NoiseChunkGenerator(new OverworldBiomeProvider(evnt.getSeed(), false, false, evnt.getBiomeRegistry()), evnt.getSeed(), () -> sttngs);

		Supplier<DimensionType> dimensionType = () -> DimensionTypeBuilder.of().ambientLight(0.1F).build();

		RegistryHelper.handleRegistrar(new DimensionRegistrar(event, CUSTOM_WORLD.getLocation(), dimensionType, settings, generator));
	}

	// Portal registry
	public static PointOfInterestType PORTAL_POI;
	public static Block PORTAL;

	public static void registerBlocks(final RegistryEvent.Register<Block> event)
	{
		PORTAL = RegistryHelper.register(event.getRegistry(), StructureGelMod.locate("portal"), new GelPortalBlock(Properties.from(Blocks.NETHER_PORTAL), (s) -> new GelTeleporter(s, () -> World.OVERWORLD, () -> CUSTOM_WORLD, () -> PORTAL_POI, () -> (GelPortalBlock) PORTAL, () -> Blocks.SMOOTH_QUARTZ.getDefaultState(), GelTeleporter.CreatePortalBehavior.NETHER)));
	}

	public static void registerPOI(final RegistryEvent.Register<PointOfInterestType> event)
	{
		PORTAL_POI = RegistryHelper.registerPOI(event.getRegistry(), new PointOfInterestType(StructureGelMod.locate("portal").toString(), PointOfInterestType.getAllStates(PORTAL), 0, 1));
	}

	// Event to create portal. By placing soul sand in a smooth quartz portal in
	// this case.
	public static void spawnPortal(final BlockEvent.EntityPlaceEvent event)
	{
		GelPortalBlock.fillPortal((World) event.getWorld(), event.getPos(), (GelPortalBlock) PORTAL, ImmutableList.of(Blocks.SOUL_SOIL));
	}

	// Register a debug structure for basic testing
	public static StructureRegistrar<NoFeatureConfig, DebugStructure> DEBUG_STRUCTURE = StructureRegistrar.of(StructureGelMod.locate("debug"), new DebugStructure(), DebugStructure.Pieces.Piece::new, NoFeatureConfig.field_236559_b_, Decoration.SURFACE_STRUCTURES).handle();

	public static void registerStructure(final RegistryEvent.Register<Structure<?>> event)
	{
		DEBUG_STRUCTURE.handleForge(event.getRegistry());
	}

	public static void commonInit(final FMLCommonSetupEvent event)
	{
		ForgeRegistries.BIOMES.forEach(b -> BiomeAccessHelper.addStructure(b, DEBUG_STRUCTURE.getStructureFeature()));
	}

	public static class DebugStructure extends GelStructure<NoFeatureConfig>
	{
		public DebugStructure()
		{
			super(NoFeatureConfig.field_236558_a_);
			this.setSpawnList(EntityClassification.MONSTER, ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.ZOMBIFIED_PIGLIN, 1, 1, 1)));
		}

		@Override
		public int getSeed()
		{
			return 0;
		}

		@Override
		public double getProbability()
		{
			return 1;
		}

		@Override
		public int getSpacing()
		{
			return 24;
		}

		@Override
		public int getOffset()
		{
			return 0;
		}

		@Override
		public IStartFactory<NoFeatureConfig> getStartFactory()
		{
			return Start::new;
		}

		public static class Start extends StructureStart<NoFeatureConfig>
		{

			public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox bounds, int references, long seed)
			{
				super(structure, chunkX, chunkZ, bounds, references, seed);
			}

			@Override
			public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGen, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig config)
			{
				Pieces.setup(templateManager, new BlockPos(chunkX * 16, 180, chunkX * 16), this.components);
				this.recalculateStructureSize();
			}

		}

		public static class Pieces
		{
			private static final ResourceLocation PIECE = new ResourceLocation("end_city/base_floor");

			public static void setup(TemplateManager templateManagerIn, BlockPos pos, List<StructurePiece> pieces)
			{
				for (int x = 0; x < 5; x++)
					for (int z = 0; z < 5; z++)
						pieces.add(new Piece(templateManagerIn, pos.add(x * 10, 0, z * 10), PIECE));
			}

			public static class Piece extends TemplateStructurePiece
			{
				private final ResourceLocation templateName;

				public Piece(TemplateManager templateManager, BlockPos pos, ResourceLocation name)
				{
					super(DEBUG_STRUCTURE.getPieceType(), 0);
					this.templateName = name;
					this.templatePosition = pos;
					this.setup(templateManager);
				}

				public Piece(TemplateManager templateManager, CompoundNBT nbt)
				{
					super(DEBUG_STRUCTURE.getPieceType(), nbt);
					this.templateName = new ResourceLocation(nbt.getString("Template"));
					this.setup(templateManager);
				}

				@Override
				protected void readAdditional(CompoundNBT tagCompound)
				{
					super.readAdditional(tagCompound);
					tagCompound.putString("Template", this.templateName.toString());
				}

				private void setup(TemplateManager templateManagerIn)
				{
					Template template = templateManagerIn.getTemplateDefaulted(this.templateName);
					PlacementSettings placementsettings = new PlacementSettings().addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
					this.setup(template, this.templatePosition, placementsettings);
				}

				@Override
				protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb)
				{

				}
			}
		}
	}*/
}

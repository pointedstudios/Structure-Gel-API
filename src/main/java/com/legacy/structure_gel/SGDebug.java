package com.legacy.structure_gel;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.access_helpers.BiomeAccessHelper;
import com.legacy.structure_gel.access_helpers.DimensionAccessHelper;
import com.legacy.structure_gel.biome_dictionary.BiomeDictionary;
import com.legacy.structure_gel.blocks.GelPortalBlock;
import com.legacy.structure_gel.events.RegisterDimensionEvent;
import com.legacy.structure_gel.events.RenderRainEvent;
import com.legacy.structure_gel.registrars.DimensionRegistrar;
import com.legacy.structure_gel.registrars.StructureRegistrar;
import com.legacy.structure_gel.util.ConfigTemplates;
import com.legacy.structure_gel.util.DimensionTypeBuilder;
import com.legacy.structure_gel.util.GelTeleporter;
import com.legacy.structure_gel.util.RegistryHelper;
import com.legacy.structure_gel.worldgen.structure.GelConfigStructure;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import net.minecraft.world.gen.feature.Features;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;

/**
 * Contains a bunch of debug code for testing or examples. This should all be
 * commented out, but feel free to reference from it.<br>
 * <br>
 * Comment code from these places. {@link StructureGelMod}
 * {@link StructureGelConfig}
 * 
 * @author David
 *
 */
public class SGDebug
{
	public static void init(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addGenericListener(Block.class, SGDebug::registerBlocks);
		modBus.addGenericListener(PointOfInterestType.class, SGDebug::registerPOI);
		modBus.addGenericListener(Structure.class, SGDebug::registerStructure);

		forgeBus.addListener(SGDebug::registerDim);
		forgeBus.addListener(SGDebug::spawnPortal);
		forgeBus.addListener(SGDebug::biomeLoad);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
		{
			forgeBus.addListener(SGDebug::renderRain);
		});
	}

	public static ResourceLocation locate(String key)
	{
		return StructureGelMod.locate(key);
	}

	// ------------------------ Dimension registry ------------------------
	public static RegistryKey<World> CUSTOM_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, locate("custom"));

	public static void registerDim(RegisterDimensionEvent event)
	{
		Function<RegistryKey<DimensionSettings>, DimensionSettings> settings = (rk) -> DimensionAccessHelper.newFloatingIslandSettings(new DimensionStructuresSettings(true), Blocks.STONE.getDefaultState(), Blocks.AIR.getDefaultState(), CUSTOM_WORLD.getLocation(), false, false);

		BiFunction<RegisterDimensionEvent, DimensionSettings, ChunkGenerator> generator = (evnt, sttngs) -> new NoiseChunkGenerator(new OverworldBiomeProvider(evnt.getSeed(), false, false, evnt.getBiomeRegistry()), evnt.getSeed(), () -> sttngs);

		Supplier<DimensionType> dimensionType = () -> DimensionTypeBuilder.of().ambientLight(0.1F).build();

		RegistryHelper.handleRegistrar(new DimensionRegistrar(event, CUSTOM_WORLD.getLocation(), dimensionType, settings, generator));
	}

	// ------------------------ Portal registry ------------------------
	public static PointOfInterestType PORTAL_POI;
	public static TestPortalBlock PORTAL;

	public static void registerBlocks(final RegistryEvent.Register<Block> event)
	{
		PORTAL = RegistryHelper.registerExact(event.getRegistry(), locate("portal"), new TestPortalBlock(Properties.from(Blocks.NETHER_PORTAL), (s) -> new GelTeleporter(s, () -> World.OVERWORLD, () -> CUSTOM_WORLD, () -> PORTAL_POI, () -> (GelPortalBlock) PORTAL, () -> Blocks.GLOWSTONE.getDefaultState(), GelTeleporter.CreatePortalBehavior.NETHER)));
	}

	private static final class TestPortalBlock extends GelPortalBlock
	{
		public TestPortalBlock(Properties properties, Function<ServerWorld, GelTeleporter> teleporter)
		{
			super(properties, teleporter);
		}

		@OnlyIn(Dist.CLIENT)
		@Override
		public TextureAtlasSprite getPortalTexture()
		{
			Minecraft mc = Minecraft.getInstance();
			return mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.ICE.getDefaultState());
		}
	}

	public static void registerPOI(final RegistryEvent.Register<PointOfInterestType> event)
	{
		PORTAL_POI = RegistryHelper.registerPOI(event.getRegistry(), new PointOfInterestType(locate("portal").toString(), PointOfInterestType.getAllStates(PORTAL), 0, 1));
	}

	// Event to create portal. By placing soul sand in a smooth quartz portal in
	// this case.
	public static void spawnPortal(final BlockEvent.EntityPlaceEvent event)
	{
		if (event.getPlacedBlock().getBlock() == Blocks.ICE)
			GelPortalBlock.fillPortal((World) event.getWorld(), event.getPos(), PORTAL, ImmutableList.of(Blocks.ICE));
	}

	// ------------------------ Structure registry ------------------------
	// Register a debug structure for basic testing
	public static StructureRegistrar<NoFeatureConfig, DebugStructure> DEBUG_STRUCTURE = StructureRegistrar.of(locate("debug"), new DebugStructure(StructureGelConfig.COMMON.structureConfig), DebugStructure.Pieces.Piece::new, NoFeatureConfig.field_236559_b_, Decoration.SURFACE_STRUCTURES).handle();

	public static void registerStructure(final RegistryEvent.Register<Structure<?>> event)
	{
		DEBUG_STRUCTURE.handleForge(event.getRegistry());
	}

	public static void biomeLoad(final BiomeLoadingEvent event)
	{
		// Add GelStructure to biome
		BiomeAccessHelper.addStructureIfAllowed(event, DEBUG_STRUCTURE.getStructureFeature());

		// Add bamboo to anything tagged as structure_gel:plains
		if (BiomeDictionary.PLAINS.contains(event.getName()))
			event.getGeneration().withFeature(Decoration.VEGETAL_DECORATION, Features.BAMBOO_LIGHT);
	}

	public static class DebugStructure extends GelConfigStructure<NoFeatureConfig>
	{
		public DebugStructure(ConfigTemplates.StructureConfig config)
		{
			super(NoFeatureConfig.field_236558_a_, config);
			this.setSpawnList(EntityClassification.MONSTER, ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.ZOMBIFIED_PIGLIN, 1, 1, 1)));
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
				Pieces.setup(templateManager, new BlockPos(chunkX * 16, 80, chunkX * 16), this.components);
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
					super(SGDebug.DEBUG_STRUCTURE.getPieceType(), 0);
					this.templateName = name;
					this.templatePosition = pos;
					this.setup(templateManager);
				}

				public Piece(TemplateManager templateManager, CompoundNBT nbt)
				{
					super(SGDebug.DEBUG_STRUCTURE.getPieceType(), nbt);
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
	}

	// ------------------------ Client Renders ------------------------
	@OnlyIn(Dist.CLIENT)
	public static void renderRain(final RenderRainEvent event)
	{
		if (event.getWorld().getDimensionKey() == CUSTOM_WORLD)
			event.setCanceled(true);
	}
}

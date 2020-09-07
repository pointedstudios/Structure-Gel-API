package com.legacy.structure_gel;

import com.legacy.structure_gel.util.Internal;

import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Contains a bunch of debug code for testing or examples.
 * 
 * @author David
 *
 */
@Internal
public class SGDebug
{
	public static void init(IEventBus modBus, IEventBus forgeBus)
	{
		/*
		forgeBus.addListener(SGDebug::registerDim);
		forgeBus.addListener(SGDebug::registerBlocks);
		forgeBus.addListener(SGDebug::registerPOI);
		forgeBus.addListener(SGDebug::spawnPortal);
		*/
	}
/*

	// Dimension registry
	public static RegistryKey<World> CUSTOM_WORLD = RegistryKey.func_240903_a_(Registry.WORLD_KEY, StructureGelMod.locate("custom"));

	public static void registerDim(RegisterDimensionEvent event)
	{
		Function<RegistryKey<DimensionSettings>, DimensionSettings> settings = (rk) -> DimensionAccessHelper.newFloatingIslandSettings(new DimensionStructuresSettings(true), Blocks.STONE.getDefaultState(), Blocks.AIR.getDefaultState(), CUSTOM_WORLD.func_240901_a_(), false, false);

		BiFunction<RegisterDimensionEvent, DimensionSettings, ChunkGenerator> generator = (evnt, sttngs) -> new NoiseChunkGenerator(new OverworldBiomeProvider(evnt.getSeed(), false, false, evnt.getBiomeRegistry()), evnt.getSeed(), () -> sttngs);

		Supplier<DimensionType> dimensionType = () -> DimensionTypeBuilder.of().ambientLight(0.1F).build();

		RegistryHelper.handleRegistrar(new DimensionRegistrar(event, CUSTOM_WORLD.func_240901_a_(), dimensionType, settings, generator));
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
		PORTAL_POI = RegistryHelper.registerPOI(event.getRegistry(), StructureGelMod.locate("portal"), PointOfInterestType.getAllStates(PORTAL), 0, 1);
	}

	// Event to create portal. By placing soul sand in a smooth quartz portal in this case.
	public static void spawnPortal(final BlockEvent.EntityPlaceEvent event)
	{
		GelPortalBlock.fillPortal((World) event.getWorld(), event.getPos(), (GelPortalBlock) PORTAL, ImmutableList.of(Blocks.SOUL_SOIL));
	}

*/
}

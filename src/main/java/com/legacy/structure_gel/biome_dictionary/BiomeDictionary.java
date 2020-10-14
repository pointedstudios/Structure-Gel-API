package com.legacy.structure_gel.biome_dictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.util.ConfigTemplates;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

/**
 * A current replacement for the forge biome dictionary. To register to the
 * biome dictionary, use the event for
 * {@link RegistryEvent.Register}&lt;{@link BiomeType}&gt;. Please use
 * {@link BiomeDictionary#register(BiomeType)} as opposed to
 * {@link IForgeRegistry#register(IForgeRegistryEntry)} since I perform special
 * checks to merge entries together.<br>
 * <br>
 * To add support for your mod without needing the API as a dependency, create a
 * method in your main mod class like this one
 * {@link StructureGelMod#getBiomesSG()}.<br>
 * <br>
 * When using a {@link ConfigTemplates.StructureConfig} and adding biome tags to
 * the config, it will reference this dictionary.
 * 
 * @see StructureGelMod#getBiomesSG()
 * @author David
 *
 */
@SuppressWarnings("unchecked")
public class BiomeDictionary
{
	@Internal
	private static final String BOP = "biomesoplenty";

	public static final IForgeRegistry<BiomeType> REGISTRY = RegistryManager.ACTIVE.getRegistry(BiomeType.class);

	@Internal
	private static final Map<RegistryKey<Biome>, Set<BiomeType>> BIOME_TO_BIOMETYPE_CACHE = new HashMap<>();

	@Internal
	protected static final ResourceLocation EMPTY_NAME = StructureGelMod.locate("empty");
	@Internal
	public static final BiomeType EMPTY = new BiomeType(EMPTY_NAME, new HashSet<>(), new HashSet<>());

	// Feature
	public static final BiomeType FROZEN_OCEAN = register(BiomeType.create("frozen_ocean").biomes(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN));
	public static final BiomeType COLD_OCEAN = register(BiomeType.create("cold_ocean").biomes(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN));
	public static final BiomeType WARM_OCEAN = register(BiomeType.create("warm_ocean").biomes(Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN));
	public static final BiomeType OCEAN = register(BiomeType.create("ocean").parents(COLD_OCEAN, WARM_OCEAN, FROZEN_OCEAN).biomes(Biomes.OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.LUKEWARM_OCEAN));
	public static final BiomeType PLAINS = register(BiomeType.create("plains").biomes(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS).biomes(BOP, "steppe", "highland", "tundra", "lush_grassland", "grassland", "shrubland", "poppy_field", "scrubland", "flower_meadow", "prarie", "pasture"));
	public static final BiomeType SNOWY_PLAINS = register(BiomeType.create("snowy_plains").biomes(Biomes.SNOWY_TUNDRA));
	public static final BiomeType DESERT = register(BiomeType.create("desert").biomes(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES).biomes(BOP, "xeric_shrubland", "outback"));
	public static final BiomeType SAVANNA = register(BiomeType.create("savanna").biomes(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU).biomes(BOP, "brushland"));
	public static final BiomeType MOUNTAIN_SAVANNA = register(BiomeType.create("mountain_savanna").biomes(Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU));
	public static final BiomeType FLOWERY = register(BiomeType.create("flowery").biomes(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS).biomes(BOP, "lavender_field", "poppy_field", "flower_meadow"));
	public static final BiomeType OAK_FOREST = register(BiomeType.create("oak_forest").biomes(Biomes.FOREST).biomes(BOP, "maple_woods", "orchard", "origin_hills", "rainforest", "silkglade", "snowy_forest", "woodland"));
	public static final BiomeType BIRCH_FOREST = register(BiomeType.create("birch_forest").biomes(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS).biomes(BOP, "boreal_forest", "rainbow_valley"));
	public static final BiomeType SPRUCE_FOREST = register(BiomeType.create("spruce_forest").biomes(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS).biomes(BOP, "shield", "grove", "meadow"));
	public static final BiomeType SNOWY_SPRUCE_FOREST = register(BiomeType.create("snowy_spruce_forest").biomes(Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS));
	public static final BiomeType LARGE_SPRUCE_FOREST = register(BiomeType.create("large_spruce_forest").biomes(Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS));
	public static final BiomeType BAMBOO_JUNGLE = register(BiomeType.create("bamboo_jungle").biomes(Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS));
	public static final BiomeType JUNGLE = register(BiomeType.create("jungle").parents(BAMBOO_JUNGLE).biomes(Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE).biomes(BOP, "overgrown_cliffs"));
	public static final BiomeType DARK_FOREST = register(BiomeType.create("dark_forest").biomes(Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS));
	public static final BiomeType CONIFEROUS_FOREST = register(BiomeType.create("coniferous_forest").biomes(BOP, "snowy_coniferous_forest", "coniferous_forest", "fir_clearing", "snowy_fir_clearing"));
	public static final BiomeType REDWOOD_FOREST = register(BiomeType.create("redwood_forest").biomes(BOP, "redwood_forest", "redwood_forest_edge", "temperate_rainforest", "temperate_rainforest_hills"));
	public static final BiomeType AUTUMN_FOREST = register(BiomeType.create("autumn_forest").biomes(BOP, "maple_woods", "seasonal_forest"));
	public static final BiomeType MOUNTAIN = register(BiomeType.create("mountain").biomes(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.WOODED_MOUNTAINS).biomes(BOP, "steppe", "overgrown_cliffs", "highland_moor", "highland", "chaparral"));
	public static final BiomeType SNOWY_MOUNTAIN = register(BiomeType.create("snowy_mountain").biomes(Biomes.SNOWY_MOUNTAINS).biomes(BOP, "muskeg", "alps", "alps_foothills"));
	public static final BiomeType SWAMP = register(BiomeType.create("swamp").biomes(Biomes.SWAMP, Biomes.SWAMP_HILLS).biomes(BOP, "mire", "floodplain", "bayou", "mangrove", "wetland", "lush_swamp", "bog", "marsh"));
	public static final BiomeType BADLANDS = register(BiomeType.create("bandlands").biomes(Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU).biomes(BOP, "outback"));
	public static final BiomeType MUSHROOM = register(BiomeType.create("mushroom").biomes(Biomes.MUSHROOM_FIELD_SHORE, Biomes.MUSHROOM_FIELDS).biomes(BOP, "fungal_jungle"));
	public static final BiomeType RIVER = register(BiomeType.create("river").biomes(Biomes.RIVER, Biomes.FROZEN_RIVER));
	public static final BiomeType LAKE = register(BiomeType.create("lake").biomes(BOP, "oasis"));
	public static final BiomeType BEACH = register(BiomeType.create("beach").biomes(Biomes.BEACH).biomes(BOP, "tropic_beach"));
	public static final BiomeType GRAVELLY = register(BiomeType.create("gravelly").biomes(BOP, "gravel_beach", "cold_desert"));
	public static final BiomeType WOODED = register(BiomeType.create("wooded").parents(OAK_FOREST, BIRCH_FOREST, SPRUCE_FOREST, SNOWY_SPRUCE_FOREST, LARGE_SPRUCE_FOREST, JUNGLE, DARK_FOREST, CONIFEROUS_FOREST, REDWOOD_FOREST).biomes(Biomes.FLOWER_FOREST).biomes(BOP, "seasonal_forest", "lavender_field", "tropical_rainforest", "dead_forest", "muskeg", "cherry_blossom_grove"));
	public static final BiomeType SANDY = register(BiomeType.create("sandy").parents(DESERT, BEACH).biomes(Biomes.SOUL_SAND_VALLEY, Biomes.BADLANDS));
	public static final BiomeType TROPICAL = register(BiomeType.create("tropical").biomes(BOP, "tropic_beach", "tropics", "rainforest", "tropical_rainforest", "overgrown_cliffs"));
	public static final BiomeType VOLCANIC = register(BiomeType.create("volcanic").biomes(BOP, "volcanic_plains", "volcano"));
	public static final BiomeType DEAD = register(BiomeType.create("dead").biomes(Biomes.NETHER_WASTES).biomes(BOP, "dead_forest", "wasteland", "silkglade", "xeric_shrubland", "brushland", "muskeg"));

	// Temperature
	public static final BiomeType FROZEN = register(BiomeType.create("frozen").biomes(Biomes.FROZEN_RIVER, Biomes.ICE_SPIKES));
	public static final BiomeType SNOWY = register(BiomeType.create("snowy").parents(SNOWY_SPRUCE_FOREST, SNOWY_PLAINS).biomes(Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH).biomes(BOP, "snowy_forest", "snowy_coniferous_forest", "snowy_fir_clearing", "muskeg", "alps", "alps_foothills"));
	public static final BiomeType COLD = register(BiomeType.create("cold").parents(SPRUCE_FOREST, LARGE_SPRUCE_FOREST, CONIFEROUS_FOREST).biomes(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.WOODED_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.STONE_SHORE).biomes(BOP, "tundra", "cold_desert"));
	public static final BiomeType NEUTRAL_TEMP = register(BiomeType.create("neutral_temp").parents(PLAINS, OAK_FOREST, BIRCH_FOREST, DARK_FOREST).biomes(Biomes.FLOWER_FOREST));
	public static final BiomeType WARM = register(BiomeType.create("warm").parents(SWAMP, JUNGLE, MUSHROOM, TROPICAL).biomes(BOP, "temperate_rainforest", "temperate_rainforest_hills"));
	public static final BiomeType HOT = register(BiomeType.create("hot").parents(DESERT, BADLANDS, SAVANNA, MOUNTAIN_SAVANNA, VOLCANIC));
	public static final BiomeType FIERY = register(BiomeType.create("fiery").biomes(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS));

	// Humidity
	public static final BiomeType HUMID = register(BiomeType.create("humid").parents(SWAMP, JUNGLE, TROPICAL));
	public static final BiomeType DRY = register(BiomeType.create("dry").parents(DESERT, BADLANDS, SAVANNA, MOUNTAIN_SAVANNA, DEAD));

	// Nether
	public static final BiomeType OVERGROWN_NETHER = register(BiomeType.create("overgrown_nether").biomes(BOP, "undergrowth"));
	public static final BiomeType NETHER_FOREST = register(BiomeType.create("nether_forest").biomes(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST));
	public static final BiomeType NETHER = register(BiomeType.create("nether").parents(NETHER_FOREST, OVERGROWN_NETHER).biomes(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS).biomes(BOP, "crystalline_chasm", "visceral_heap", "withered_abyss"));

	// End
	public static final BiomeType OUTER_END_ISLAND = register(BiomeType.create("outer_end_island").biomes(Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS).biomes("endergetic", "poise_forest", "chorus_plains", "end_midlands", "end_highlands"));
	public static final BiomeType OUTER_END = register(BiomeType.create("outer_end").parents(OUTER_END_ISLAND).biomes(Biomes.END_BARRENS, Biomes.SMALL_END_ISLANDS));
	public static final BiomeType END = register(BiomeType.create("end").parents(OUTER_END).biomes(Biomes.THE_END));

	// Overworld
	public static final BiomeType OVERWORLD = register(BiomeType.create("overworld").setBiomesSafe(getOverworldBiomesSafe()));

	// Special
	public static final BiomeType VOID = register(BiomeType.create("void").biomes(Biomes.THE_VOID));
	public static final BiomeType MAGICAL = register(BiomeType.create("magical").biomes(BOP, "mystic_grove", "rainbow_valley"));
	public static final BiomeType SPOOKY = register(BiomeType.create("spooky").parents(DARK_FOREST).biomes(BOP, "ominous_woods", "pumpkin_patch", "silkglade", "visceral_heap", "withered_abyss"));
	public static final BiomeType RARE = register(BiomeType.create("rare").parents(MUSHROOM).biomes(Biomes.JUNGLE_EDGE).biomes(BOP, "mystic_grove", "origin_hills", "rainbow_valley"));

	public static void init()
	{
		// Can't register EMPTY directly since I'm preventing it, so I do it like this.
		REGISTRY.register(EMPTY);
	}

	/**
	 * Replacement for {@link IForgeRegistry#register(IForgeRegistryEntry)}. Please
	 * use this instead as it has special functionality to allow extending existing
	 * registries.
	 * 
	 * @param biomeType
	 * @return {@link BiomeType}
	 */
	public static BiomeType register(BiomeType biomeType)
	{
		ResourceLocation key = biomeType.getRegistryName();
		if (biomeType.getRegistryName().equals(EMPTY_NAME))
		{
			StructureGelMod.LOGGER.warn(String.format("Registry failed: Attempted to register a biome dictionary entry under the name \"%s\" and that shouldn't be done.", EMPTY_NAME.toString()));
			return biomeType;
		}
		else
		{
			// Add to existing registry
			if (REGISTRY.containsKey(key))
				REGISTRY.getValue(key).addBiomes(biomeType.getBiomes()).addParents(biomeType.getParents());
			// Create new registry
			else
				REGISTRY.register(biomeType);
			if (!BIOME_TO_BIOMETYPE_CACHE.isEmpty())
				BIOME_TO_BIOMETYPE_CACHE.clear();

			return biomeType;
		}
	}

	/**
	 * Replacement for {@link IForgeRegistry#registerAll(IForgeRegistryEntry...)}
	 * 
	 * @param biomeTypes
	 */
	public static void registerAll(BiomeType... biomeTypes)
	{
		for (BiomeType type : biomeTypes)
			register(type);
	}

	/**
	 * Returns all registed vanilla biomes that aren't tagged as nether or end.
	 * 
	 * @return {@link Set}
	 * @deprecated use {@link #getOverworldBiomesSafe()}. TODO Remove in 1.17.
	 */
	@Deprecated
	public static Set<RegistryKey<Biome>> getOverworldBiomes()
	{
		Set<RegistryKey<Biome>> biomes = new HashSet<>();

		ForgeRegistries.BIOMES.getValues().stream().filter(b -> (b.getRegistryName().getNamespace().equals("minecraft") || b.getRegistryName().getNamespace().equals(BOP)) && !BiomeDictionary.NETHER.contains(b) && !BiomeDictionary.END.contains(b)).forEach(b ->
		{
			if (getBiomeKey(b) != null)
				biomes.add(getBiomeKey(b));
		});

		return biomes;
	}

	/**
	 * Returns all registed vanilla biomes that aren't tagged as nether or end.
	 * 
	 * @return {@link Set}
	 */
	public static Set<ResourceLocation> getOverworldBiomesSafe()
	{
		Set<ResourceLocation> biomes = new HashSet<>();

		ForgeRegistries.BIOMES.getValues().stream().filter(b -> (b.getRegistryName().getNamespace().equals("minecraft") || b.getRegistryName().getNamespace().equals(BOP)) && !BiomeDictionary.NETHER.contains(b) && !BiomeDictionary.END.contains(b)).forEach(b ->
		{
			if (b.getRegistryName() != null)
				biomes.add(b.getRegistryName());
			else if (getBiomeKey(b) != null)
				biomes.add(getBiomeKey(b).getLocation());
		});

		return biomes;
	}

	/**
	 * Returns the {@link RegistryKey} for the passed {@link Biome}.
	 * 
	 * @param biome
	 * @return {@link RegistryKey}
	 */
	public static RegistryKey<Biome> getBiomeKey(Biome biome)
	{
		return RegistryKey.getOrCreateKey(ForgeRegistries.Keys.BIOMES, biome.getRegistryName());
	}

	/**
	 * Returns all {@link BiomeType}s containing this biome.
	 * 
	 * @param biome
	 * @return {@link Set}
	 */
	public static Set<BiomeType> getAllTypes(RegistryKey<Biome> biome)
	{
		if (BIOME_TO_BIOMETYPE_CACHE.containsKey(biome))
			return BIOME_TO_BIOMETYPE_CACHE.get(biome);
		else
		{
			Set<BiomeType> types = new HashSet<>();
			REGISTRY.forEach(type ->
			{
				if (type.getAllBiomes().contains(biome))
					types.add(type);
			});

			BIOME_TO_BIOMETYPE_CACHE.put(biome, types);
			return types;
		}
	}

	/**
	 * Shorthand for BiomeDictionary.REGISTRY.containsKey(ResourceLocation).
	 * 
	 * @param name
	 * @return {@link Boolean}
	 */
	public static boolean contains(ResourceLocation name)
	{
		return REGISTRY.containsKey(name);
	}

	/**
	 * Shorthand for BiomeDictionary.REGISTRY.getValue(ResourceLocation). Returns
	 * {@link #EMPTY} if no value is present.
	 * 
	 * @return {@link BiomeType}
	 */
	public static BiomeType get(ResourceLocation name)
	{
		return REGISTRY.getValue(name);
	}

	/**
	 * Shorthand for BiomeDictionary.REGISTRY.forEach(Consumer).
	 * 
	 * @param action
	 */
	public static void forEach(Consumer<BiomeType> action)
	{
		REGISTRY.forEach(action);
	}
}

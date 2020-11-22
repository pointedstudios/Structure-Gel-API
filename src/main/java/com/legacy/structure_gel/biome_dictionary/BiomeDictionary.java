package com.legacy.structure_gel.biome_dictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.StructureGelConfig;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.util.ConfigTemplates;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.block.GravelBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.BiomeDictionary.Type;
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
	private static final String bop = "biomesoplenty", nethercraft = "nethercraft", endergetic = "endergetic",
			rediscovered = "rediscovered", moo = "moolands", pagamos = "pagamos", glacidus = "glacidus", byg = "byg",
			aether = "aether";

	public static final IForgeRegistry<BiomeType> REGISTRY = RegistryManager.ACTIVE.getRegistry(BiomeType.class);

	@Internal
	private static final Map<ResourceLocation, Set<BiomeType>> BIOME_TO_BIOMETYPE_CACHE = new HashMap<>();

	@Internal
	protected static final ResourceLocation EMPTY_NAME = StructureGelMod.locate("empty");
	@Internal
	public static final BiomeType EMPTY = new BiomeType(EMPTY_NAME, new HashSet<>(), new HashSet<>());

	// Feature
	public static final BiomeType FROZEN_OCEAN = register(BiomeType.create("frozen_ocean").biomes(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN));
	public static final BiomeType COLD_OCEAN = register(BiomeType.create("cold_ocean").biomes(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN));
	public static final BiomeType WARM_OCEAN = register(BiomeType.create("warm_ocean").biomes(Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN));
	public static final BiomeType OCEAN = register(BiomeType.create("ocean").parents(COLD_OCEAN, WARM_OCEAN, FROZEN_OCEAN).biomes(Biomes.OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.LUKEWARM_OCEAN).biomes(byg, "dead_sea"));
	public static final BiomeType PLAINS = register(BiomeType.create("plains").biomes(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS).biomes(bop, "steppe", "highland", "tundra", "lush_grassland", "jade_grassland", "shrubland", "lush_savanna", "scrubland", "flower_meadow", "prairie", "golden_prairie").biomes(byg, "allium_fields", "amaranth_fields", "meadow", "prairie", "prairie_clearing", "shrublands", "wooded_meadow"));
	public static final BiomeType SNOWY_PLAINS = register(BiomeType.create("snowy_plains").biomes(Biomes.SNOWY_TUNDRA));
	public static final BiomeType DESERT = register(BiomeType.create("desert").biomes(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES).biomes(bop, "xeric_shrubland", "outback", "lush_desert").biomes(byg, "dunes", "lush_red_desert", "mojave_desert", "red_desert", "red_desert_dunes"));
	public static final BiomeType SAVANNA = register(BiomeType.create("savanna").biomes(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU).biomes(bop, "brushland").biomes(byg, "baobab_savanna"));
	public static final BiomeType MOUNTAIN_SAVANNA = register(BiomeType.create("mountain_savanna").biomes(Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU));
	public static final BiomeType FLOWERY = register(BiomeType.create("flowery").biomes(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS).biomes(bop, "lavender_field", "lush_savanna", "flower_meadow").biomes(byg, "allium_fields", "amaranth_fields", "flowering_enchanted_grove", "flowering_grove", "flowering_meadow"));
	public static final BiomeType OAK_FOREST = register(BiomeType.create("oak_forest").biomes(Biomes.FOREST).biomes(bop, "maple_forest", "snowy_maple_forest", "orchard", "origin_hills", "rainforest", "silkglade", "snowy_forest").biomes(byg, "deciduous_clearing", "deciduous_forest", "deciduous_forest_hills", "orchard", "red_oak_forest", "red_oak_forest_hills", "snowy_deciduous_clearing", "snowy_deciduous_forest", "snowy_deciduous_forest_hills", "woodlands"));
	public static final BiomeType BIRCH_FOREST = register(BiomeType.create("birch_forest").biomes(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS).biomes(bop, "boreal_forest", "rainbow_valley").biomes(byg, "aspen_clearing", "aspen_forest", "aspen_forest_hills"));
	public static final BiomeType SPRUCE_FOREST = register(BiomeType.create("spruce_forest").biomes(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS).biomes(bop, "coniferous_lakes", "grove", "meadow", "jade_cliffs").biomes(byg, "blue_taiga", "blue_taiga_hills", "boreal_clearing", "boreal_forest", "boreal_forest_hills", "evergreen_clearing", "evergreen_hills", "evergreen_taiga"));
	public static final BiomeType SNOWY_SPRUCE_FOREST = register(BiomeType.create("snowy_spruce_forest").biomes(Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS).biomes(byg, "apline_foothills", "snowy_blue_taiga", "snowy_blue_taiga_hills", "snowy_evergreen_clearing", "snowy_evergreen_hills", "snowy_evergreen_taiga"));
	public static final BiomeType LARGE_SPRUCE_FOREST = register(BiomeType.create("large_spruce_forest").biomes(Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS).biomes(byg, "blue_giant_taiga", "snowy_blue_giant_taiga"));
	public static final BiomeType BAMBOO_JUNGLE = register(BiomeType.create("bamboo_jungle").biomes(Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS));
	public static final BiomeType JUNGLE = register(BiomeType.create("jungle").parents(BAMBOO_JUNGLE).biomes(Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE).biomes(bop, "rainforest_cliffs").biomes(byg, "guiana_clearing", "guiana_shield"));
	public static final BiomeType DARK_FOREST = register(BiomeType.create("dark_forest").biomes(Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS).biomes(byg, "ancient_forest", "ebony_woods", "ebony_hills", "flowering_ancient_forest"));
	public static final BiomeType CONIFEROUS_FOREST = register(BiomeType.create("coniferous_forest").biomes(bop, "snowy_coniferous_forest", "coniferous_forest", "fir_clearing", "snowy_fir_clearing").biomes(byg, "coniferous_clearing", "coniferous_forest", "coniferous_forest_hills", "snowy_coniferous_clearing", "snowy_coniferous_forest", "snowy_coniferous_forest_hills"));
	public static final BiomeType REDWOOD_FOREST = register(BiomeType.create("redwood_forest").biomes(bop, "redwood_forest", "redwood_forest_edge", "redwood_forest", "redwood_forest_hills").biomes(byg, "redwood_clearing", "redwood_mountains", "redwood_tropics"));
	public static final BiomeType AUTUMN_FOREST = register(BiomeType.create("autumn_forest").biomes(bop, "maple_forest", "seasonal_forest").biomes(byg, "maple_hills", "maple_taiga", "seasonal_birch_forest", "seasonal_birch_forest_hills", "seasonal_deciduous_clearing", "seasonal_deciduous_forest", "seasonal_deciduous_forest_hills", "seasonal_forest", "seasonal_forest_hills", "seasonal_giant_taiga", "seasonal_taiga", "seasonal_taiga_hills", "zelkova_clearing", "zelkova_forest", "zelkova_forest_hills"));
	public static final BiomeType CHERRY_FOREST = register(BiomeType.create("cherry_forest").biomes(bop, "cherry_blossom_grove").biomes(byg, "cherry_blossom_clearing", "cherry_blossom_forest", "skyris_highlands"));
	public static final BiomeType BAMBOO = register(BiomeType.create("bamboo").parents(BAMBOO_JUNGLE).biomes(byg, "jacaranda_clearing", "jacaranda_forest", "jacaranda_forest_hills", "bamboo_forest", "crag_gardens"));
	public static final BiomeType MOUNTAIN = register(BiomeType.create("mountain").biomes(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.WOODED_MOUNTAINS).biomes(bop, "steppe", "rainforest_cliffs", "highland_moor", "highland", "shrubland_hills", "jade_cliffs").biomes(byg, "bluff_steeps", "cika_mountains", "crag_gardens", "dover_mountains", "grassland_plateau", "guiana_clearing", "guiana_shield", "redwood_mountains", "skyris_highlands", "wooded_grassland_plateau"));
	public static final BiomeType SNOWY_MOUNTAIN = register(BiomeType.create("snowy_mountain").biomes(Biomes.SNOWY_MOUNTAINS).biomes(bop, "alps", "alps_foothills").biomes(byg, "apls", "apline_foothills", "bluff_peaks", "bluff_steeps"));
	public static final BiomeType SWAMP = register(BiomeType.create("swamp").biomes(Biomes.SWAMP, Biomes.SWAMP_HILLS).biomes(bop, "dead_swamp", "rainforest_floodplain", "bayou", "wetland", "lush_swamp", "bog", "wetland_marsh").biomes(byg, "bayou", "tundra_bog", "cold_swamplands", "cypress_swamplands", "glowshroom_bayou", "marshlands", "vibrant_swamplands"));
	public static final BiomeType MANGROVE = register(BiomeType.create("mangrove").biomes(bop, "bayou_mangrove").biomes(byg, "coral_mangroves", "cypress_swamplands", "mangrove_marshes"));
	public static final BiomeType BADLANDS = register(BiomeType.create("badlands").biomes(Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU).biomes(bop, "outback").biomes(byg, "lush_red_desert", "red_desert", "red_desert_dunes", "red_rock_highlands", "red_rock_lowlands", "red_rock_mountains", "sierra_range", "sierra_valley", "wooded_red_rock_mountains"));
	public static final BiomeType MUSHROOM = register(BiomeType.create("mushroom").biomes(Biomes.MUSHROOM_FIELD_SHORE, Biomes.MUSHROOM_FIELDS).biomes(bop, "fungal_jungle").biomes(byg, "glowing_ancient_forest", "glowshroom_bayou"));
	public static final BiomeType FUNGAL = register(BiomeType.create("fungal").parents(MUSHROOM).biomes(byg, "fungal_patch"));
	public static final BiomeType RIVER = register(BiomeType.create("river").biomes(Biomes.RIVER, Biomes.FROZEN_RIVER));
	public static final BiomeType LAKE = register(BiomeType.create("lake").biomes(bop, "oasis").biomes(byg, "fresh_water_lake", "oasis", "frozen_lake", "great_lakes", "polluted_lake"));
	public static final BiomeType BEACH = register(BiomeType.create("beach").biomes(Biomes.BEACH).biomes(bop, "tropic_beach").biomes(byg, "rainbow_beach", "snowy_black_beach", "white_beach"));
	public static final BiomeType WOODED = register(BiomeType.create("wooded").parents(OAK_FOREST, BIRCH_FOREST, SPRUCE_FOREST, SNOWY_SPRUCE_FOREST, DARK_FOREST, AUTUMN_FOREST, CHERRY_FOREST).biomes(Biomes.FLOWER_FOREST).biomes(bop, "seasonal_forest", "lavender_field", "tropical_rainforest", "dead_forest").biomes(byg, "jacaranda_forest", "the_black_forest", "black_forest_hills", "bluff_peaks", "bluff_steeps", "enchanted_forest", "enchanted_forest_hills", "flowering_meadow", "forest_fault", "glowing_ancient_forest", "great_lake_isles", "grove", "weeping_witch_forest", "weeping_witch_clearing", "wooded_grassland_plateau", "wooded_meadow", "wooded_red_rock_mountains"));
	public static final BiomeType LARGE_WOODED = register(BiomeType.create("large_wooded").parents(LARGE_SPRUCE_FOREST, JUNGLE, CONIFEROUS_FOREST, REDWOOD_FOREST).biomes(byg, "cika_wooded", "cika_mountains", "dover_mountains", "northern_forest"));
	public static final BiomeType SANDY = register(BiomeType.create("sandy").parents(DESERT, BEACH).biomes(Biomes.SOUL_SAND_VALLEY, Biomes.BADLANDS));
	public static final BiomeType GRAVELLY = register(BiomeType.create("gravelly").biomes(bop, "gravel_beach", "tundra_basin"));
	public static final BiomeType DIRTY = register(BiomeType.create("dirty").biomes(Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS).biomes(byg, "black_forest_clearing"));
	public static final BiomeType TROPICAL = register(BiomeType.create("tropical").biomes(bop, "tropic_beach", "tropics", "rainforest", "tropical_rainforest", "rainforest_cliffs").biomes(byg, "tropical_fungal_forest", "tropical_fungal_rainforest_hills", "tropical_island", "tropical_rainforest", "tropical_rainforest_hills"));
	public static final BiomeType VOLCANIC = register(BiomeType.create("volcanic").biomes(bop, "volcanic_plains", "volcano"));
	public static final BiomeType DEAD = register(BiomeType.create("dead").biomes(Biomes.NETHER_WASTES).biomes(bop, "burnt_forest", "dead_forest", "wasteland", "silkglade", "xeric_shrubland", "brushland", "dead_swamp", "dryland").biomes(byg, "dead_sea"));
	public static final BiomeType SKY = register(BiomeType.create("sky").biomes(rediscovered, "skylands"));

	// Temperature
	public static final BiomeType FROZEN = register(BiomeType.create("frozen").biomes(Biomes.FROZEN_RIVER, Biomes.ICE_SPIKES).parents(FROZEN_OCEAN).biomes(byg, "frozen_lake", "shattered_glacier"));
	public static final BiomeType SNOWY = register(BiomeType.create("snowy").parents(SNOWY_SPRUCE_FOREST, SNOWY_PLAINS).biomes(Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH).biomes(bop, "snowy_forest", "snowy_coniferous_forest", "snowy_fir_clearing", "muskeg", "alps", "alps_foothills", "snowy_black_beach", "snowy_blue_taiga", "snowy_blue_taiga_hills", "snowy_giant_blue_taiga", "snowy_coniferous_clearing", "snowy_coniferous_forest", "snowy_coniferous_forest_hills", "snowy_deciduous_clearing", "snowy_deciduous_forest", "snowy_deciduous_forest_hills", "snowy_evergreen_clearing", "snowy_evergreen_hills", "snowy_evergreen_taiga", "snowy_maple_forest", "snowy_rocky_black_beach"));
	public static final BiomeType COLD = register(BiomeType.create("cold").parents(SPRUCE_FOREST, LARGE_SPRUCE_FOREST, CONIFEROUS_FOREST).biomes(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.WOODED_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.STONE_SHORE).biomes(bop, "tundra", "tundra_basin").biomes(byg, "cold_swamplands", "lush_tundra", "rocky_beach"));
	public static final BiomeType NEUTRAL_TEMP = register(BiomeType.create("neutral_temp").parents(PLAINS, OAK_FOREST, BIRCH_FOREST, DARK_FOREST).biomes(Biomes.FLOWER_FOREST));
	public static final BiomeType WARM = register(BiomeType.create("warm").parents(SWAMP, MANGROVE, JUNGLE, MUSHROOM, TROPICAL));
	public static final BiomeType HOT = register(BiomeType.create("hot").parents(DESERT, BADLANDS, SAVANNA, MOUNTAIN_SAVANNA, VOLCANIC));
	public static final BiomeType FIERY = register(BiomeType.create("fiery").biomes(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS).biomes(byg, "embur_bog"));

	// Humidity
	public static final BiomeType HUMID = register(BiomeType.create("humid").parents(SWAMP, JUNGLE, TROPICAL));
	public static final BiomeType DRY = register(BiomeType.create("dry").parents(DESERT, BADLANDS, SAVANNA, MOUNTAIN_SAVANNA, DEAD).biomes(bop, "dryland"));

	// Special
	public static final BiomeType VOID = register(BiomeType.create("void").biomes(Biomes.THE_VOID));
	public static final BiomeType MAGICAL = register(BiomeType.create("magical").biomes(bop, "mystic_grove", "rainbow_hills").biomes(byg, "enchanted_forest", "enchanted_forest_hills", "enchanted_grove", "flowering_enchanted_grove"));
	public static final BiomeType SPOOKY = register(BiomeType.create("spooky").parents(DARK_FOREST).biomes(bop, "ominous_woods", "pumpkin_patch", "silkglade", "visceral_heap", "withered_abyss", "dryland").biomes(byg, "pumpkin_forest", "weeping_witch_forest", "weeping_witch_clearing"));
	public static final BiomeType RARE = register(BiomeType.create("rare").biomes(Biomes.JUNGLE_EDGE, Biomes.MUSHROOM_FIELD_SHORE, Biomes.MUSHROOM_FIELDS, Biomes.ICE_SPIKES).biomes(bop, "mystic_grove", "origin_valley", "rainbow_hills").biomes(byg, "rainbow_beach", "tropical_island"));
	public static final BiomeType SPACE = register(BiomeType.create("space").biomes(glacidus, "glacidus"));
	public static final BiomeType PUMPKIN = register(BiomeType.create("pumpkin").biomes(bop, "pumpkin_patch").biomes(byg, "pumpkin_forest", "autumnal_valley", "cika_wooded", "cika_mountains"));

	// Nether
	public static final BiomeType OVERGROWN_NETHER = register(BiomeType.create("overgrown_nether").biomes(bop, "undergrowth"));
	public static final BiomeType WARPED = register(BiomeType.create("warped").biomes(Biomes.WARPED_FOREST).biomes(byg, "warped_desert"));
	public static final BiomeType CRIMSON = register(BiomeType.create("crimson").biomes(Biomes.CRIMSON_FOREST));
	public static final BiomeType NETHER_FOREST = register(BiomeType.create("nether_forest").biomes(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST).biomes(nethercraft, "glowing_grove"));
	public static final BiomeType NETHER_FUNGAL = register(BiomeType.create("nether_fungal").biomes(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST).biomes(nethercraft, "glowshroom_garden").biomes(byg, "glowstone_gardens"));
	public static final BiomeType NETHER_SANDY = register(BiomeType.create("nether_sandy").biomes(Biomes.SOUL_SAND_VALLEY).biomes(nethercraft, "volcanic_rushes").biomes(byg, "warped_desert"));
	public static final BiomeType NETHER_EXTREME = register(BiomeType.create("nether_extreme").biomes(Biomes.BASALT_DELTAS));
	public static final BiomeType NETHER_FLESHY = register(BiomeType.create("nether_fleshy").biomes(bop, "visceral_heap"));

	// End
	public static final BiomeType OUTER_END_ISLAND = register(BiomeType.create("outer_end_island").biomes(Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS).biomes(endergetic, "poise_forest", "chorus_plains", "end_midlands", "end_highlands").biomes(byg, "ivis_fields"));
	public static final BiomeType OUTER_END = register(BiomeType.create("outer_end").parents(OUTER_END_ISLAND).biomes(Biomes.END_BARRENS, Biomes.SMALL_END_ISLANDS));

	// Dimension
	public static final BiomeType NETHER = register(BiomeType.create("nether").parents(WARPED, CRIMSON, NETHER_FOREST, OVERGROWN_NETHER, NETHER_FUNGAL, NETHER_SANDY, NETHER_EXTREME, NETHER_FLESHY).biomes(Biomes.NETHER_WASTES).biomes(bop, "crystalline_chasm", "withered_abyss").biomes(byg, "embur_bog", "sythian_torrids"));
	public static final BiomeType END = register(BiomeType.create("end").parents(OUTER_END).biomes(Biomes.THE_END));
	public static final BiomeType AETHER = register(BiomeType.create("aether").biomes(aether, "aether_skylands"));
	public static final BiomeType OVERWORLD = register(BiomeType.create("overworld").setBiomes(getOverworldBiomes()));
	public static final BiomeType SKYLANDS = register(BiomeType.create("skylands").biomes(rediscovered, "skylands"));
	public static final BiomeType MOOLANDS = register(BiomeType.create("moolands").biomes(moo, "awkward_heights"));
	public static final BiomeType PAGAMOS = register(BiomeType.create("pagamos").biomes(pagamos, "frozen_hell"));
	public static final BiomeType GLACIDUS = register(BiomeType.create("glacidus").biomes(glacidus, "glacidus"));
	public static final BiomeType GOOD_DREAM = register(BiomeType.create("good_dream"));
	public static final BiomeType NIGHTMARE = register(BiomeType.create("nightmare"));

	// Forge
	public static final ForgeType FORGE_HOT = register(ForgeType.create(Type.HOT));
	public static final ForgeType FORGE_COLD = register(ForgeType.create(Type.COLD));
	public static final ForgeType FORGE_SPARSE = register(ForgeType.create(Type.SPARSE));
	public static final ForgeType FORGE_DENSE = register(ForgeType.create(Type.DENSE));
	public static final ForgeType FORGE_WET = register(ForgeType.create(Type.WET));
	public static final ForgeType FORGE_DRY = register(ForgeType.create(Type.DRY));
	public static final ForgeType FORGE_SAVANNA = register(ForgeType.create(Type.SAVANNA));
	public static final ForgeType FORGE_CONIFEROUS = register(ForgeType.create(Type.CONIFEROUS));
	public static final ForgeType FORGE_JUNGLE = register(ForgeType.create(Type.JUNGLE));
	public static final ForgeType FORGE_SPOOKY = register(ForgeType.create(Type.SPOOKY));
	public static final ForgeType FORGE_DEAD = register(ForgeType.create(Type.DEAD));
	public static final ForgeType FORGE_LUSH = register(ForgeType.create(Type.LUSH));
	public static final ForgeType FORGE_MUSHROOM = register(ForgeType.create(Type.MUSHROOM));
	public static final ForgeType FORGE_MAGICAL = register(ForgeType.create(Type.MAGICAL));
	public static final ForgeType FORGE_RARE = register(ForgeType.create(Type.RARE));
	public static final ForgeType FORGE_PLATEAU = register(ForgeType.create(Type.PLATEAU));
	public static final ForgeType FORGE_MODIFIED = register(ForgeType.create(Type.MODIFIED));
	public static final ForgeType FORGE_OCEAN = register(ForgeType.create(Type.OCEAN));
	public static final ForgeType FORGE_RIVER = register(ForgeType.create(Type.RIVER));
	public static final ForgeType FORGE_WATER = register(ForgeType.create(Type.WATER));
	public static final ForgeType FORGE_MESA = register(ForgeType.create(Type.MESA));
	public static final ForgeType FORGE_FOREST = register(ForgeType.create(Type.FOREST));
	public static final ForgeType FORGE_PLAINS = register(ForgeType.create(Type.PLAINS));
	public static final ForgeType FORGE_MOUNTAIN = register(ForgeType.create(Type.MOUNTAIN));
	public static final ForgeType FORGE_HILLS = register(ForgeType.create(Type.HILLS));
	public static final ForgeType FORGE_SWAMP = register(ForgeType.create(Type.SWAMP));
	public static final ForgeType FORGE_SANDY = register(ForgeType.create(Type.SANDY));
	public static final ForgeType FORGE_SNOWY = register(ForgeType.create(Type.SNOWY));
	public static final ForgeType FORGE_WASTELAND = register(ForgeType.create(Type.WASTELAND));
	public static final ForgeType FORGE_BEACH = register(ForgeType.create(Type.BEACH));
	public static final ForgeType FORGE_VOID = register(ForgeType.create(Type.VOID));
	public static final ForgeType FORGE_OVERWORLD = register(ForgeType.create(Type.OVERWORLD));
	public static final ForgeType FORGE_NETHER = register(ForgeType.create(Type.NETHER));
	public static final ForgeType FORGE_END = register(ForgeType.create(Type.END));

	public static void init()
	{
		// Can't register EMPTY directly since I'm preventing it, so I do it like this.
		if (!REGISTRY.containsKey(EMPTY_NAME))
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
	public static <T extends BiomeType> T register(T biomeType)
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
	 * Used for registering forge biome dictionary entries to this dictionary for
	 * compatibility.
	 * 
	 * @param forgeType
	 * @return {@link ForgeType}
	 */
	@Internal
	private static ForgeType register(ForgeType forgeType)
	{
		REGISTRY.register(forgeType);
		return forgeType;
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
	 */
	public static Set<ResourceLocation> getOverworldBiomes()
	{
		Set<ResourceLocation> biomes = new HashSet<>();

		ForgeRegistries.BIOMES.getValues().stream().filter(b -> ImmutableList.of("minecraft", bop, byg).contains(b.getRegistryName().getNamespace()) && !BiomeDictionary.NETHER.contains(b) && !BiomeDictionary.END.contains(b)).forEach(b ->
		{
			if (b.getRegistryName() != null)
				biomes.add(b.getRegistryName());
			else if (getBiomeKey(b) != null)
				biomes.add(getBiomeKey(b).getLocation());
		});

		return biomes;
	}

	/**
	 * Makes assumptions as to which biome type a biome should be registered to if
	 * it is not already registered to a type.<br>
	 * <br>
	 * This can be disabled via configs.
	 * 
	 * @return The biomes registered and where they were registered to
	 */
	@Internal
	public static Map<ResourceLocation, Set<BiomeType>> makeGuess()
	{
		Map<ResourceLocation, Set<BiomeType>> newlyRegistered = new HashMap<>();

		Map<Biome.Category, Function<Biome, BiomeType>> categoryToType = new HashMap<>();
		categoryToType.put(Biome.Category.TAIGA, (b) -> b.getPrecipitation() == RainType.SNOW ? SNOWY_SPRUCE_FOREST : SPRUCE_FOREST);
		categoryToType.put(Biome.Category.EXTREME_HILLS, (b) -> b.getPrecipitation() == RainType.SNOW ? SNOWY_MOUNTAIN : MOUNTAIN);
		categoryToType.put(Biome.Category.JUNGLE, (b) -> JUNGLE);
		categoryToType.put(Biome.Category.MESA, (b) -> BADLANDS);
		categoryToType.put(Biome.Category.PLAINS, (b) -> b.getPrecipitation() == RainType.SNOW ? SNOWY_PLAINS : PLAINS);
		categoryToType.put(Biome.Category.SAVANNA, (b) -> SAVANNA);
		categoryToType.put(Biome.Category.ICY, (b) -> SNOWY);
		categoryToType.put(Biome.Category.THEEND, (b) -> END);
		categoryToType.put(Biome.Category.BEACH, (b) -> BEACH);
		categoryToType.put(Biome.Category.FOREST, (b) -> WOODED);
		categoryToType.put(Biome.Category.OCEAN, (b) -> b.getPrecipitation() == RainType.SNOW ? FROZEN_OCEAN : OCEAN);
		categoryToType.put(Biome.Category.DESERT, (b) -> DESERT);
		categoryToType.put(Biome.Category.RIVER, (b) -> RIVER);
		categoryToType.put(Biome.Category.SWAMP, (b) -> SWAMP);
		categoryToType.put(Biome.Category.MUSHROOM, (b) -> MUSHROOM);
		categoryToType.put(Biome.Category.NETHER, (b) -> NETHER);

		Map<Type, Function<Biome, BiomeType>> forgeToType = new HashMap<>();
		forgeToType.put(Type.OVERWORLD, (b) -> OVERWORLD);
		forgeToType.put(Type.NETHER, (b) -> NETHER);
		forgeToType.put(Type.END, (b) -> END);
		forgeToType.put(Type.BEACH, (b) -> BEACH);
		forgeToType.put(Type.CONIFEROUS, (b) -> CONIFEROUS_FOREST);
		forgeToType.put(Type.DEAD, (b) -> DEAD);
		forgeToType.put(Type.DRY, (b) -> DRY);
		forgeToType.put(Type.FOREST, (b) -> WOODED);
		forgeToType.put(Type.HOT, (b) -> HOT);
		forgeToType.put(Type.JUNGLE, (b) -> JUNGLE);
		forgeToType.put(Type.MAGICAL, (b) -> MAGICAL);
		forgeToType.put(Type.MESA, (b) -> BADLANDS);
		forgeToType.put(Type.MOUNTAIN, (b) -> b.getPrecipitation() == RainType.SNOW ? SNOWY_MOUNTAIN : MOUNTAIN);
		forgeToType.put(Type.MUSHROOM, (b) -> MUSHROOM);
		forgeToType.put(Type.OCEAN, (b) -> b.getPrecipitation() == RainType.SNOW ? FROZEN_OCEAN : OCEAN);
		forgeToType.put(Type.PLAINS, (b) -> b.getPrecipitation() == RainType.SNOW ? SNOWY_PLAINS : PLAINS);
		forgeToType.put(Type.RARE, (b) -> RARE);
		forgeToType.put(Type.RIVER, (b) -> RIVER);
		forgeToType.put(Type.SANDY, (b) -> SANDY);
		forgeToType.put(Type.SAVANNA, (b) -> SAVANNA);
		forgeToType.put(Type.SNOWY, (b) -> SNOWY);
		forgeToType.put(Type.SPOOKY, (b) -> SPOOKY);
		forgeToType.put(Type.SWAMP, (b) -> SWAMP);
		forgeToType.put(Type.VOID, (b) -> VOID);
		forgeToType.put(Type.WASTELAND, (b) -> DEAD);

		Map<String, BiomeType> nameToType = new HashMap<>();
		nameToType.put("frozen", FROZEN);
		nameToType.put("snowy", SNOWY);
		nameToType.put("redwood", REDWOOD_FOREST);
		nameToType.put("coniferous", CONIFEROUS_FOREST);
		nameToType.put("bamboo", BAMBOO);
		nameToType.put("flower", FLOWERY);
		nameToType.put("tulip", FLOWERY);
		nameToType.put("orchid", FLOWERY);
		nameToType.put("dandelion", FLOWERY);
		nameToType.put("allium", FLOWERY);
		nameToType.put("poppy", FLOWERY);
		nameToType.put("daisy", FLOWERY);
		nameToType.put("lilac", FLOWERY);
		nameToType.put("peony", FLOWERY);
		nameToType.put("ocean", OCEAN);
		nameToType.put("river", RIVER);
		nameToType.put("beach", BEACH);
		nameToType.put("shroom", MUSHROOM);
		nameToType.put("fungal", FUNGAL);
		nameToType.put("seasonal", AUTUMN_FOREST);
		nameToType.put("autumn", AUTUMN_FOREST);

		List<String> ignoredMods = StructureGelConfig.COMMON.getIgnoredMods();

		ForgeRegistries.BIOMES.getValues().stream().filter(biome -> !ignoredMods.contains(biome.getRegistryName().getNamespace()) && !getAllTypes(biome).stream().filter(BiomeDictionary::filterTypeForAutoRegister).findAny().isPresent()).forEach(biome ->
		{
			// Vanilla category
			if (categoryToType.containsKey(biome.getCategory()))
				categoryToType.get(biome.getCategory()).apply(biome).addBiome(biome);

			// Temp (based on vanilla biome temperatures)
			float t = biome.getTemperature();
			if (t <= 0.05F)
				SNOWY.addBiome(biome);
			else if (t <= 0.25F)
				COLD.addBiome(biome);
			else if (t <= 1.0F)
				NEUTRAL_TEMP.addBiome(biome);
			else if (t <= 1.5F)
				WARM.addBiome(biome);
			else
				HOT.addBiome(biome);

			// Humidity
			if (biome.isHighHumidity())
				HUMID.addBiome(biome);

			// Rain type
			switch (biome.getPrecipitation())
			{
			case NONE:
				if (biome.getDepth() > 0)
					DRY.addBiome(biome);
				break;
			case SNOW:
				SNOWY.addBiome(biome);
				break;
			default:
				break;
			}

			if (biome.getRegistryName() != null)
			{
				// Forge type
				net.minecraftforge.common.BiomeDictionary.getTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, biome.getRegistryName())).forEach(type ->
				{
					if (forgeToType.containsKey(type))
						forgeToType.get(type).apply(biome).addBiome(biome);
				});

				// Name based
				String name = biome.getRegistryName().getPath();
				nameToType.forEach((string, type) ->
				{
					if (name.contains(string))
						type.addBiome(biome);
				});
			}

			// Surface builder
			if (biome.getGenerationSettings().getSurfaceBuilderConfig().getTop().getMaterial() == Material.SAND)
				SANDY.addBiome(biome);
			if (biome.getGenerationSettings().getSurfaceBuilderConfig().getTop().getMaterial() == Material.EARTH)
				DIRTY.addBiome(biome);
			if (biome.getGenerationSettings().getSurfaceBuilderConfig().getTop().getBlock() instanceof GravelBlock)
				GRAVELLY.addBiome(biome);

			BIOME_TO_BIOMETYPE_CACHE.clear();
			newlyRegistered.put(biome.getRegistryName(), getAllTypes(biome));
		});

		BIOME_TO_BIOMETYPE_CACHE.clear();
		return newlyRegistered;
	}
	
	@Internal
	public static boolean filterTypeForAutoRegister(BiomeType type)
	{
		return !(type instanceof ForgeType) && type != OVERWORLD;
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
		return getAllTypes(biome.getLocation());
	}

	/**
	 * Returns all {@link BiomeType}s containing this biome.
	 * 
	 * @param biome
	 * @return {@link Set}
	 */
	public static Set<BiomeType> getAllTypes(Biome biome)
	{
		return getAllTypes(biome.getRegistryName());
	}

	/**
	 * Returns all {@link BiomeType}s containing this biome.
	 * 
	 * @param biome
	 * @return {@link Set}
	 */
	public static Set<BiomeType> getAllTypes(ResourceLocation biome)
	{
		if (BIOME_TO_BIOMETYPE_CACHE.containsKey(biome))
			return BIOME_TO_BIOMETYPE_CACHE.get(biome);
		else
		{
			Set<BiomeType> types = new HashSet<>();
			REGISTRY.forEach(type ->
			{
				if (type.getAllBiomes().stream().map(RegistryKey::getLocation).anyMatch(biome::equals))
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

package com.legacy.structure_gel.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.util.ConfigTemplates;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A current replacement for the forge biome dictionary. To add support for your
 * mod, create a method in your main mod class like the one in
 * {@link StructureGelMod}<br>
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
	/**
	 * Reference {@link #getRegistry()} or {@link #get(ResourceLocation)}
	 */
	private static final Map<ResourceLocation, BiomeType> REGISTRY = new HashMap<>();

	@Internal
	private static final Map<RegistryKey<Biome>, Set<BiomeType>> BIOME_TO_BIOMETYPE_CACHE = new HashMap<>();

	@Internal
	private static final ResourceLocation EMPTY_NAME = StructureGelMod.locate("empty");
	@Internal
	private static final BiomeType EMPTY = new BiomeType(EMPTY_NAME, new HashSet<>(), new HashSet<>());

	// Feature
	public static final BiomeType FROZEN_OCEAN = register(BiomeType.create("frozen_ocean").biomes(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN));
	public static final BiomeType COLD_OCEAN = register(BiomeType.create("cold_ocean").biomes(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN));
	public static final BiomeType WARM_OCEAN = register(BiomeType.create("warm_ocean").biomes(Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN));
	public static final BiomeType OCEAN = register(BiomeType.create("ocean").parents(COLD_OCEAN, WARM_OCEAN, FROZEN_OCEAN).biomes(Biomes.OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.LUKEWARM_OCEAN));
	public static final BiomeType PLAINS = register(BiomeType.create("plains").biomes(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS));
	public static final BiomeType SNOWY_PLAINS = register(BiomeType.create("snowy_plains").biomes(Biomes.SNOWY_TUNDRA));
	public static final BiomeType DESERT = register(BiomeType.create("desert").biomes(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES));
	public static final BiomeType SAVANNA = register(BiomeType.create("savanna").biomes(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU));
	public static final BiomeType MOUNTAIN_SAVANNA = register(BiomeType.create("mountain_savanna").biomes(Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU));
	public static final BiomeType FLOWERY = register(BiomeType.create("flowery").biomes(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS));
	public static final BiomeType OAK_FOREST = register(BiomeType.create("oak_forest").biomes(Biomes.FOREST));
	public static final BiomeType BIRCH_FOREST = register(BiomeType.create("birch_forest").biomes(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS));
	public static final BiomeType SPRUCE_FOREST = register(BiomeType.create("spruce_forest").biomes(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS));
	public static final BiomeType SNOWY_SPRUCE_FOREST = register(BiomeType.create("snowy_spruce_forest").biomes(Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS));
	public static final BiomeType LARGE_SPRUCE_FOREST = register(BiomeType.create("large_spruce_forest").biomes(Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS));
	public static final BiomeType BAMBOO_JUNGLE = register(BiomeType.create("bamboo_jungle").biomes(Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS));
	public static final BiomeType JUNGLE = register(BiomeType.create("jungle").parents(BAMBOO_JUNGLE).biomes(Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE));
	public static final BiomeType DARK_FOREST = register(BiomeType.create("dark_forest").biomes(Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS));
	public static final BiomeType MOUNTAIN = register(BiomeType.create("mountain").biomes(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_MOUNTAINS));
	public static final BiomeType SWAMP = register(BiomeType.create("swamp").biomes(Biomes.SWAMP, Biomes.SWAMP_HILLS));
	public static final BiomeType BADLANDS = register(BiomeType.create("bandlands").biomes(Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU));
	public static final BiomeType MUSHROOM = register(BiomeType.create("mushroom").biomes(Biomes.MUSHROOM_FIELD_SHORE, Biomes.MUSHROOM_FIELDS));
	public static final BiomeType RIVER = register(BiomeType.create("river").biomes(Biomes.RIVER, Biomes.FROZEN_RIVER));
	public static final BiomeType BEACH = register(BiomeType.create("beach").biomes(Biomes.BEACH));

	// Temperature
	public static final BiomeType FROZEN = register(BiomeType.create("frozen").biomes(Biomes.FROZEN_RIVER, Biomes.ICE_SPIKES));
	public static final BiomeType SNOWY = register(BiomeType.create("snowy").parents(SNOWY_SPRUCE_FOREST, SNOWY_PLAINS).biomes(Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH));
	public static final BiomeType COLD = register(BiomeType.create("cold").parents(SPRUCE_FOREST, LARGE_SPRUCE_FOREST).biomes(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.WOODED_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.STONE_SHORE));
	public static final BiomeType NEUTRAL_TEMP = register(BiomeType.create("neutral_temp").parents(PLAINS, OAK_FOREST, BIRCH_FOREST, DARK_FOREST).biomes(Biomes.FLOWER_FOREST));
	public static final BiomeType WARM = register(BiomeType.create("warm").parents(SWAMP, JUNGLE, MUSHROOM));
	public static final BiomeType HOT = register(BiomeType.create("hot").parents(DESERT, BADLANDS, SAVANNA, MOUNTAIN_SAVANNA));
	public static final BiomeType FIERY = register(BiomeType.create("fiery").biomes(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS));

	// Humidity
	public static final BiomeType HUMID = register(BiomeType.create("humid").parents(SWAMP, JUNGLE));
	public static final BiomeType DRY = register(BiomeType.create("dry").parents(DESERT, BADLANDS, SAVANNA, MOUNTAIN_SAVANNA));

	// Nether
	public static final BiomeType NETHER_FOREST = register(BiomeType.create("nether_forest").biomes(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST));
	public static final BiomeType NETHER = register(BiomeType.create("nether").parents(NETHER_FOREST).biomes(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS));

	// End
	public static final BiomeType OUTER_END_ISLAND = register(BiomeType.create("outer_end_island").biomes(Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS).biomes("endergetic", "poise_forest", "chorus_plains", "end_midlands", "end_highlands"));
	public static final BiomeType OUTER_END = register(BiomeType.create("outer_end").parents(OUTER_END_ISLAND).biomes(Biomes.END_BARRENS, Biomes.SMALL_END_ISLANDS));
	public static final BiomeType END = register(BiomeType.create("end").parents(OUTER_END).biomes(Biomes.THE_END));

	// Overworld
	public static final BiomeType OVERWORLD = register(BiomeType.create("overworld").setBiomes(getOverworldBiomes()));

	// Special
	public static final BiomeType VOID = register(BiomeType.create("void").biomes(Biomes.THE_VOID));
	public static final BiomeType MAGICAL = register(BiomeType.create("magical"));
	public static final BiomeType SPOOKY = register(BiomeType.create("spooky").parents(DARK_FOREST));
	public static final BiomeType RARE = register(BiomeType.create("rare").parents(MUSHROOM).biomes(Biomes.JUNGLE_EDGE));

	public static void init()
	{
		// Can't register EMPTY directly since I'm preventing it, so I do it like this.
		REGISTRY.put(EMPTY_NAME, EMPTY);

		// Post event to register biome dictionary entries
		MinecraftForge.EVENT_BUS.post(new BiomeDictionaryEvent());
	}

	/**
	 * Returns all registed vanilla biomes that aren't tagged as nether or end.
	 * 
	 * @return {@link Set}
	 */
	public static Set<RegistryKey<Biome>> getOverworldBiomes()
	{
		Set<RegistryKey<Biome>> biomes = new HashSet<>();

		ForgeRegistries.BIOMES.getValues().stream().filter(b -> b.getRegistryName().getNamespace().equals("minecraft") && !BiomeDictionary.NETHER.contains(b) && !BiomeDictionary.END.contains(b)).forEach(b ->
		{
			if (getBiomeKey(b) != null)
				biomes.add(getBiomeKey(b));
		});

		return biomes;
	}

	/**
	 * Registers the {@link BiomeType} to {@link #REGISTRY}.
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
				REGISTRY.get(key).addBiomes(biomeType.getBiomes()).addParents(biomeType.getParents());
			// Create new registry
			else
				REGISTRY.put(key, biomeType);

			if (!BIOME_TO_BIOMETYPE_CACHE.isEmpty())
				BIOME_TO_BIOMETYPE_CACHE.clear();

			return biomeType;
		}
	}

	/**
	 * Returns the {@link RegistryKey} for the passed {@link Biome}.
	 * 
	 * @param biome
	 * @return {@link RegistryKey}
	 */
	protected static RegistryKey<Biome> getBiomeKey(Biome biome)
	{
		return RegistryKey.func_240903_a_(ForgeRegistries.Keys.BIOMES, biome.getRegistryName());
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
			REGISTRY.forEach((name, type) ->
			{
				if (type.getAllBiomes().contains(biome))
					types.add(type);
			});

			BIOME_TO_BIOMETYPE_CACHE.put(biome, types);
			return types;
		}
	}

	/**
	 * Returns an immutable copy of the biome dictionary. This should only be used
	 * as a reference. Use {@link #register(BiomeType)} to register things.
	 * 
	 * @return {@link ImmutableMap}
	 */
	public static ImmutableMap<ResourceLocation, BiomeType> getRegistry()
	{
		return ImmutableMap.copyOf(REGISTRY);
	}

	/**
	 * Shorthand for getRegistry().containsKey(name)
	 * 
	 * @param name
	 * @return {@link Boolean}
	 */
	public static boolean contains(ResourceLocation name)
	{
		return REGISTRY.containsKey(name);
	}

	/**
	 * Returns the {@link BiomeType} from the registry under the name passed or
	 * {@link #EMPTY} if no entry exists.
	 * 
	 * @return {@link BiomeType}
	 */
	public static BiomeType get(ResourceLocation name)
	{
		return REGISTRY.containsKey(name) ? REGISTRY.get(name) : BiomeDictionary.EMPTY;
	}

	/**
	 * Stores a collection of biomes that should have similar traits for referencing
	 * in worldgen. A desert biome would be considered similar to a desert hills
	 * biome, so they would make sense to be together.
	 * 
	 * @see BiomeDictionary
	 * @author David
	 *
	 */
	public static class BiomeType
	{
		/**
		 * The name that this is registered as.
		 */
		private ResourceLocation registryName = EMPTY_NAME;
		/**
		 * The biomes in this instance.
		 */
		private Set<ResourceLocation> biomes = new HashSet<>();
		/**
		 * When running {@link #getAllBiomes()}, it will return all of the biomes of
		 * this entry and all of the biomes of any parent listed.
		 */
		private Set<ResourceLocation> parents = new HashSet<>();

		/**
		 * Ensures none of the values will be set to null just in case.
		 * 
		 * @param name
		 * @param parents
		 * @param biomes
		 */
		public BiomeType(ResourceLocation name, Set<ResourceLocation> parents, Set<RegistryKey<Biome>> biomes)
		{
			if (name != null)
				this.registryName = name;
			if (parents != null)
				this.parents = new HashSet<>(parents);
			if (biomes != null)
				this.biomes = biomes.stream().map(r -> r.func_240901_a_()).collect(Collectors.toSet());
		}

		/**
		 * @see BiomeType#create(ResourceLocation)
		 */
		@Internal
		private BiomeType()
		{

		}

		/**
		 * Start creating the {@link BiomeType}.
		 * 
		 * @param name
		 * @return {@link BiomeType}
		 */
		public static BiomeType create(ResourceLocation name)
		{
			BiomeType biomeType = new BiomeType();
			biomeType.registryName = name;
			return biomeType;
		}

		/**
		 * Start creating the {@link BiomeType}.
		 * 
		 * @see #create(ResourceLocation)
		 * @param name
		 * @return {@link BiomeType}
		 */
		@Internal
		private static BiomeType create(String name)
		{
			return create(StructureGelMod.locate(name));
		}

		/**
		 * Adds the biomes to this instance
		 * 
		 * @param biomes
		 * @return {@link BiomeType}
		 */
		public BiomeType biomes(RegistryKey<Biome>... biomes)
		{
			this.addBiomes(Arrays.asList(biomes).stream().map(r -> r.func_240901_a_()).collect(Collectors.toSet()));
			return this;
		}

		/**
		 * Adds the biomes to this instance for the modid provided.
		 * 
		 * @param modid
		 * @param biomes
		 * @return {@link BiomeType}
		 */
		public BiomeType biomes(String modid, String... biomes)
		{
			Set<ResourceLocation> set = new HashSet<>();
			for (String biome : biomes)
				set.add(new ResourceLocation(modid, biome));
			this.addBiomes(set);
			return this;
		}

		/**
		 * Adds the parents to this instance
		 * 
		 * @param parents
		 * @return {@link BiomeType}
		 */
		public BiomeType parents(ResourceLocation... parents)
		{
			this.setParents(new HashSet<>(Arrays.asList(parents)));
			return this;
		}

		/**
		 * Set the parents for this instance.
		 * 
		 * @param parents
		 * @return {@link BiomeType}
		 */
		public BiomeType parents(BiomeType... parents)
		{
			this.parents = Arrays.asList(parents).stream().map(b -> b.getRegistryName()).collect(Collectors.toSet());
			return this;
		}

		/**
		 * Gets the registered name of the {@link BiomeType}.
		 * 
		 * @return {@link ResourceLocation}
		 */
		public ResourceLocation getRegistryName()
		{
			return this.registryName;
		}

		/**
		 * Adds the listed biomes to this instance.
		 * 
		 * @param biomes
		 * @return {@link BiomeType}
		 */
		public BiomeType addBiomes(Set<ResourceLocation> biomes)
		{
			this.getBiomes().addAll(biomes);
			return this;
		}

		/**
		 * Add the biome to this instance.
		 * 
		 * @param biome
		 * @return {@link BiomeType}
		 */
		public BiomeType addBiome(RegistryKey<Biome> biome)
		{
			this.getBiomes().add(biome.func_240901_a_());
			return this;
		}

		/**
		 * Gets the biomes from this instance.
		 * 
		 * @return {@link Set}
		 */
		public Set<ResourceLocation> getBiomes()
		{
			return this.biomes;
		}

		/**
		 * Sets the biomes of this entry manually.
		 * 
		 * @param biomes
		 * @return {@link BiomeType}
		 */
		public BiomeType setBiomes(Set<RegistryKey<Biome>> biomes)
		{
			this.biomes = biomes.stream().map(r -> r.func_240901_a_()).collect(Collectors.toSet());
			return this;
		}

		/**
		 * Gets the biomes from this instance and it's parents by trying to find their
		 * registry keys.
		 * 
		 * @return {@link Set}
		 */
		public Set<RegistryKey<Biome>> getAllBiomes()
		{
			Set<RegistryKey<Biome>> biomes = this.getBiomes().stream().filter(ForgeRegistries.BIOMES::containsKey).map(r -> RegistryKey.func_240903_a_(ForgeRegistries.Keys.BIOMES, r)).collect(Collectors.toSet());
			for (ResourceLocation parent : this.getParents())
			{
				if (REGISTRY.containsKey(parent))
					biomes.addAll(REGISTRY.get(parent).getAllBiomes());
			}

			return biomes;
		}

		/**
		 * Does not allow duplicates.
		 * 
		 * @param parents
		 * @return {@link BiomeType}
		 */
		public BiomeType addParents(Set<ResourceLocation> parents)
		{
			this.getParents().addAll(parents);
			return this;
		}

		/**
		 * Does not allow duplicates.
		 * 
		 * @param parent
		 * @return {@link BiomeType}
		 */
		public BiomeType addParent(ResourceLocation parent)
		{
			this.getParents().add(parent);
			return this;
		}

		/**
		 * @return {@link Set}
		 */
		public Set<ResourceLocation> getParents()
		{
			return this.parents;
		}

		/**
		 * Sets the parents of this entry manually.
		 * 
		 * @param parents
		 * @return {@link BiomeType}
		 */
		public BiomeType setParents(Set<ResourceLocation> parents)
		{
			this.parents = new HashSet<>(parents);
			return this;
		}

		/**
		 * Does this {@link BiomeType} or it's parents contain the biome passed.
		 * 
		 * @param biome
		 * @return {@link Boolean}
		 */
		public boolean contains(Biome biome)
		{
			if (getBiomeKey(biome) != null)
				return this.getAllBiomes().contains(getBiomeKey(biome));
			return false;
		}

		/**
		 * Compares the registry name of the {@link BiomeType} passed in to this.
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof BiomeType)
				return ((BiomeType) obj).getRegistryName().equals(this.getRegistryName());
			else
				return false;
		}

		@Override
		public String toString()
		{
			String parents = "";
			Iterator<ResourceLocation> iterator = this.getParents().iterator();
			while (iterator.hasNext())
			{
				parents = parents + iterator.next().toString();
				if (iterator.hasNext())
					parents = parents + ", ";
			}

			String biomes = "";
			Iterator<ResourceLocation> iterator2 = this.getBiomes().iterator();
			while (iterator2.hasNext())
			{
				biomes = biomes + iterator2.next().toString();
				if (iterator2.hasNext())
					biomes = biomes + ", ";
			}

			return String.format("name = %s, parents = [%s], biomes = [%s]", this.getRegistryName().toString(), parents, biomes);
		}
	}
}

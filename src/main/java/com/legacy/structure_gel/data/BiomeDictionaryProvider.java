package com.legacy.structure_gel.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Used for data generation.
 * 
 * @author David
 *
 */
@SuppressWarnings("unchecked")
@Internal
public class BiomeDictionaryProvider implements IDataProvider
{
	protected final DataGenerator generator;
	private final Map<ResourceLocation, BiomeType> providers = new HashMap<>();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

	public BiomeDictionaryProvider(DataGenerator generator)
	{
		this.generator = generator;
	}

	public void registerBiomeTypes()
	{
		// Feature
		registerProvider(BiomeType.create(StructureGelMod.locate("ocean")).biomes(Biomes.OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.FROZEN_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN));
		registerProvider(BiomeType.create(StructureGelMod.locate("plains")).biomes(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS));
		registerProvider(BiomeType.create(StructureGelMod.locate("snowy_plains")).biomes(Biomes.SNOWY_TUNDRA));
		registerProvider(BiomeType.create(StructureGelMod.locate("desert")).biomes(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES));
		registerProvider(BiomeType.create(StructureGelMod.locate("savanna")).biomes(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU));
		registerProvider(BiomeType.create(StructureGelMod.locate("mountain_savanna")).biomes(Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU));
		registerProvider(BiomeType.create(StructureGelMod.locate("flowery")).biomes(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS));
		registerProvider(BiomeType.create(StructureGelMod.locate("oak_forest")).biomes(Biomes.FOREST));
		registerProvider(BiomeType.create(StructureGelMod.locate("birch_forest")).biomes(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS));
		registerProvider(BiomeType.create(StructureGelMod.locate("spruce_forest")).biomes(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS));
		registerProvider(BiomeType.create(StructureGelMod.locate("snowy_spruce_forest")).biomes(Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS));
		registerProvider(BiomeType.create(StructureGelMod.locate("large_spruce_forest")).biomes(Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS));
		registerProvider(BiomeType.create(StructureGelMod.locate("jungle")).biomes(Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE));
		registerProvider(BiomeType.create(StructureGelMod.locate("bamboo_jungle")).biomes(Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS));
		registerProvider(BiomeType.create(StructureGelMod.locate("dark_forest")).biomes(Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS));
		registerProvider(BiomeType.create(StructureGelMod.locate("mountain")).biomes(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_MOUNTAINS));
		registerProvider(BiomeType.create(StructureGelMod.locate("swamp")).biomes(Biomes.SWAMP, Biomes.SWAMP_HILLS));
		registerProvider(BiomeType.create(StructureGelMod.locate("bandlands")).biomes(Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU));
		registerProvider(BiomeType.create(StructureGelMod.locate("mushroom")).biomes(Biomes.MUSHROOM_FIELD_SHORE, Biomes.MUSHROOM_FIELDS));
		registerProvider(BiomeType.create(StructureGelMod.locate("river")).biomes(Biomes.RIVER, Biomes.FROZEN_RIVER));
		registerProvider(BiomeType.create(StructureGelMod.locate("beach")).biomes(Biomes.BEACH));

		// Temperature
		registerProvider(BiomeType.create(StructureGelMod.locate("frozen")).biomes(Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.DEEP_FROZEN_OCEAN, Biomes.ICE_SPIKES));
		registerProvider(BiomeType.create(StructureGelMod.locate("snowy")).parents(BiomeDictionary.SNOWY_SPRUCE_FOREST, BiomeDictionary.SNOWY_PLAINS).biomes(Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH));
		registerProvider(BiomeType.create(StructureGelMod.locate("cold")).parents(BiomeDictionary.SPRUCE_FOREST, BiomeDictionary.LARGE_SPRUCE_FOREST).biomes(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.WOODED_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.STONE_SHORE));
		registerProvider(BiomeType.create(StructureGelMod.locate("neutral_temp")).parents(BiomeDictionary.PLAINS, BiomeDictionary.OAK_FOREST, BiomeDictionary.BIRCH_FOREST, BiomeDictionary.DARK_FOREST).biomes(Biomes.FLOWER_FOREST));
		registerProvider(BiomeType.create(StructureGelMod.locate("warm")).parents(BiomeDictionary.SWAMP, BiomeDictionary.JUNGLE, BiomeDictionary.MUSHROOM));
		registerProvider(BiomeType.create(StructureGelMod.locate("hot")).parents(BiomeDictionary.DESERT, BiomeDictionary.BADLANDS, BiomeDictionary.SAVANNA, BiomeDictionary.MOUNTAIN_SAVANNA));
		registerProvider(BiomeType.create(StructureGelMod.locate("fiery")).biomes(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY));

		// Humidity
		registerProvider(BiomeType.create(StructureGelMod.locate("humid")).parents(BiomeDictionary.SWAMP, BiomeDictionary.JUNGLE));
		registerProvider(BiomeType.create(StructureGelMod.locate("dry")).parents(BiomeDictionary.DESERT, BiomeDictionary.BADLANDS, BiomeDictionary.SAVANNA, BiomeDictionary.MOUNTAIN_SAVANNA));

		// Dimension
		registerProvider(BiomeType.create(StructureGelMod.locate("nether")).biomes(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS));
		registerProvider(BiomeType.create(StructureGelMod.locate("end")).biomes(Biomes.END_BARRENS, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.THE_END));
		registerProvider(BiomeType.create(StructureGelMod.locate("overworld")).biomes(getOverworldBiomes()));

		// Special
		registerProvider(BiomeType.create(StructureGelMod.locate("magical")));
		registerProvider(BiomeType.create(StructureGelMod.locate("oil")));
		registerProvider(BiomeType.create(StructureGelMod.locate("void")).biomes(Biomes.THE_VOID));
		registerProvider(BiomeType.create(StructureGelMod.locate("strange")));
	}

	@Override
	public void act(DirectoryCache cache) throws IOException
	{
		this.registerBiomeTypes();
		this.providers.forEach((name, type) ->
		{
			JsonObject jsonobject = BiomeType.serialize(type);
			Path path = this.generator.getOutputFolder().resolve("data/" + name.getNamespace() + "/biome_dictionary/" + name.getPath() + ".json");
			try
			{
				String jsonString = GSON.toJson((JsonElement) jsonobject);
				String hashJsonString = HASH_FUNCTION.hashUnencodedChars(jsonString).toString();
				if (!Objects.equals(cache.getPreviousHash(path), hashJsonString) || !Files.exists(path))
				{
					Files.createDirectories(path.getParent());

					try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path))
					{
						bufferedwriter.write(jsonString);
					}
				}

				cache.recordHash(path, hashJsonString);
			}
			catch (IOException ex)
			{
				StructureGelMod.LOGGER.error("Couldn't save biome dictionary entry to {}", path, ex);
			}
		});
	}

	@Override
	public String getName()
	{
		return "Structure Gel Biome Dictionary";
	}

	protected RegistryKey<Biome>[] getOverworldBiomes()
	{
		List<RegistryKey<Biome>> biomes = new ArrayList<>();

		ForgeRegistries.BIOMES.getValues().stream().filter(b -> b.getRegistryName().getNamespace().equals("minecraft") && !BiomeDictionary.NETHER.contains(b) && !BiomeDictionary.END.contains(b)).forEach(b ->
		{
			if (getBiomeKey(b).isPresent())
				biomes.add(getBiomeKey(b).get());
		});

		return (RegistryKey<Biome>[]) biomes.toArray(new RegistryKey<?>[biomes.size()]);
	}

	protected void registerProvider(BiomeType biomeType)
	{
		ResourceLocation key = biomeType.getRegistryName();
		if (!providers.containsKey(key))
		{
			providers.put(key, biomeType);
			StructureGelMod.biomeDictionary.getRegistry().put(key, biomeType);
		}
		else
			StructureGelMod.LOGGER.warn(String.format("Duplicate biome dictionary entry registered with name \"%s\"! This could be an intentional override, but should be avoided.", key.toString()));
	}

	protected static Optional<RegistryKey<Biome>> getBiomeKey(Biome biome)
	{
		return Optional.of(RegistryKey.func_240903_a_(ForgeRegistries.Keys.BIOMES, biome.getRegistryName()));
	}

	/**
	 * Stores a collection of biomes that should have similar traits for referencing
	 * in worldgen. A desert biome would be considered similar to a desert hills
	 * biome, so they would make sense to be together. You can also use this to
	 * define biomes that a specific feature or structure shuold generate in, such
	 * as plains, desert, and taiga all containing a village.
	 * 
	 * @see BiomeDictionaryProvider
	 * @author David
	 *
	 */
	public static class BiomeType
	{
		public static BiomeType EMPTY = new BiomeType();
		private ResourceLocation registryName = StructureGelMod.locate("empty");
		private Set<RegistryKey<Biome>> biomes = new HashSet<>();
		private Set<ResourceLocation> parents = new HashSet<>();
		private boolean replace = false;

		/**
		 * @param name
		 * @param parents
		 * @param biomes
		 */
		public BiomeType(ResourceLocation name, Set<ResourceLocation> parents, Set<RegistryKey<Biome>> biomes)
		{
			this.registryName = name;
			this.parents = parents;
			this.biomes = biomes;
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
		 * Set the biomes for this instance.
		 * 
		 * @param biomes
		 * @return {@link BiomeType}
		 */
		public BiomeType biomes(RegistryKey<Biome>... biomes)
		{
			this.biomes = new HashSet<>(Arrays.asList(biomes));
			return this;
		}

		/**
		 * Set the parents for this instance.
		 * 
		 * @param parents
		 * @return {@link BiomeType}
		 */
		public BiomeType parents(ResourceLocation... parents)
		{
			this.parents = new HashSet<>(Arrays.asList(parents));
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
		 * Does not allow duplicates.
		 * 
		 * @param biomes
		 * @return {@link BiomeType}
		 */
		public BiomeType addBiomes(Set<RegistryKey<Biome>> biomes)
		{
			this.getBiomes().forEach(this::addBiome);
			return this;
		}

		/**
		 * Does not allow duplicates.
		 * 
		 * @param biome
		 * @return {@link BiomeType}
		 */
		public BiomeType addBiome(RegistryKey<Biome> biome)
		{
			this.getBiomes().add(biome);
			return this;
		}

		/**
		 * Gets the biomes from this instance.
		 * 
		 * @return {@link Set}
		 */
		public Set<RegistryKey<Biome>> getBiomes()
		{
			return this.biomes;
		}

		/**
		 * Sets the biomes of this entry manually.
		 * 
		 * @param biomes
		 */
		public void setBiomes(Set<RegistryKey<Biome>> biomes)
		{
			this.biomes = biomes;
		}

		/**
		 * Gets the biomes from this instance and it's parents.
		 * 
		 * @return {@link Set}
		 */
		public Set<RegistryKey<Biome>> getAllBiomes()
		{
			Set<RegistryKey<Biome>> biomes = this.getBiomes();
			for (ResourceLocation parent : this.getParents())
			{
				Optional<BiomeType> entry = StructureGelMod.biomeDictionary.get(parent);
				if (entry.isPresent())
					biomes.addAll(entry.get().getAllBiomes());
			}

			return biomes;
		}

		/**
		 * Does not allow duplicates.
		 * 
		 * @param parents
		 * @return {@link BiomeType}
		 */
		public BiomeType addParents(Set<ResourceLocation> biomes)
		{
			this.getParents().forEach(this::addParent);
			return this;
		}

		/**
		 * Does not allow duplicates.
		 * 
		 * @param parents
		 * @return {@link BiomeType}
		 */
		public BiomeType addParent(ResourceLocation biome)
		{
			this.getParents().add(biome);
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
		 */
		public void setParents(Set<ResourceLocation> parents)
		{
			this.parents = parents;
		}

		/**
		 * Does this {@link BiomeType} or it's parents contain the biome passed.
		 * 
		 * @param biome
		 * @return {@link Boolean}
		 */
		public boolean contains(Biome biome)
		{
			if (BiomeDictionaryProvider.getBiomeKey(biome).isPresent())
				return this.getAllBiomes().contains(BiomeDictionaryProvider.getBiomeKey(biome).get());
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
			Iterator<RegistryKey<Biome>> iterator2 = this.getBiomes().iterator();
			while (iterator2.hasNext())
			{
				biomes = biomes + iterator2.next().func_240901_a_().toString();
				if (iterator2.hasNext())
					biomes = biomes + ", ";
			}

			return String.format("name = %s, parents = [%s], biomes = [%s]", this.getRegistryName().toString(), parents, biomes);
		}

		public static JsonObject serialize(BiomeType biomeType)
		{
			JsonObject json = new JsonObject();
			JsonArray parentsArray = new JsonArray();
			JsonArray biomesArray = new JsonArray();

			biomeType.getParents().forEach(p -> parentsArray.add(p.toString()));
			biomeType.getBiomes().forEach(b -> biomesArray.add(RegistryKey.func_240903_a_(ForgeRegistries.Keys.BIOMES, b.func_240901_a_()).func_240901_a_().toString()));

			json.addProperty("replace", biomeType.replace);
			json.add("parents", parentsArray);
			json.add("biomes", biomesArray);
			return json;
		}

		public static BiomeType deserialize(JsonObject json, ResourceLocation name)
		{
			Boolean replace = JSONUtils.getBoolean(json, "replace", false);

			Set<ResourceLocation> parents = new HashSet<>();
			JsonArray parentsArray = JSONUtils.getJsonArray(json, "parents", new JsonArray());
			parentsArray.forEach(j -> parents.add(new ResourceLocation(j.getAsString())));

			Set<RegistryKey<Biome>> biomes = new HashSet<>();
			JsonArray biomesArray = JSONUtils.getJsonArray(json, "biomes", new JsonArray());
			biomesArray.forEach(j -> biomes.add(RegistryKey.func_240903_a_(ForgeRegistries.Keys.BIOMES, new ResourceLocation(j.getAsString()))));

			BiomeType biomeType = new BiomeType(name, parents, biomes);
			biomeType.replace = replace;

			return biomeType;
		}
	}
}

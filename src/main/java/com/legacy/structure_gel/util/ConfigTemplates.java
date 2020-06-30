package com.legacy.structure_gel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.legacy.structure_gel.structures.GelStructure;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * These are templates that you can use for your structure's config settings.
 * 
 * @author David
 *
 */
public class ConfigTemplates
{
	public static class StructureConfigBuilder
	{
		private final ForgeConfigSpec.Builder builder;
		private final String name;
		private ForgeConfigSpec.DoubleValue probability;
		private ForgeConfigSpec.IntValue spacing;
		private ForgeConfigSpec.IntValue offset;
		private ForgeConfigSpec.BooleanValue isWhitelist;
		private ForgeConfigSpec.ConfigValue<String> biomeString;
		private List<Biome> biomes = new ArrayList<>();
		private Map<EntityClassification, ForgeConfigSpec.ConfigValue<String>> spawnsStrings = new HashMap<>();
		private Map<EntityClassification, List<SpawnListEntry>> spawns = new HashMap<>();

		public StructureConfigBuilder(ForgeConfigSpec.Builder builder, String name)
		{
			this.builder = builder;
			this.name = name;
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoad);
		}

		public StructureConfigBuilder probability(double probability)
		{
			this.probability = builder.comment("Chance of generating in an allowed chunk").defineInRange(name + ".probability", probability, 0.0D, 1.0D);
			return this;
		}

		public StructureConfigBuilder spacing(int spacing)
		{
			this.spacing = builder.comment("Spacing between structures").defineInRange(name + ".spacing", spacing, 1, Integer.MAX_VALUE);
			return this;
		}

		public StructureConfigBuilder offset(int offset)
		{
			this.offset = builder.comment("Offsets the spacing of the structures randomly").defineInRange(name + ".offset", offset, 0, Integer.MAX_VALUE);
			return this;
		}

		public StructureConfigBuilder biomes(boolean isWhitelist, String biomes)
		{
			this.biomeString = builder.comment("A biome filter to determine where the structure should generate. Works with the biome dictionary (#overworld) and \"not\" statements (!plains). These can be combined (!#nether). Operates in the order presented. So \"#forest, !flower_forest\" will add all forests and then remove the flower forest.").define(name + ".biomes", biomes);
			this.isWhitelist = builder.comment("How should the code treate biomes? true = whitelist, false = blacklist. Biomes defined with ! do the opposite.").define(name + ".is_whitelist", isWhitelist);
			return this;
		}

		public StructureConfigBuilder spawns(Map<EntityClassification, String> spawns)
		{
			for (EntityClassification classification : EntityClassification.values())
				if (spawns.containsKey(classification))
					this.getSpawnsStrings().put(classification, builder.define(name + ".spawns." + classification.getName(), spawns.get(classification)));
			return this;
		}

		/**
		 * Gets the probability for the structure to generate. 1.0 by default.
		 * 
		 * @return double
		 */
		public double getProbability()
		{
			return this.probability != null ? this.probability.get() : 1.0D;
		}

		/**
		 * Gets the spacing for the structure in chunks. 16 by default.
		 * 
		 * @return int
		 */
		public int getSpacing()
		{
			return this.spacing != null ? this.spacing.get() : 16;
		}

		/**
		 * Gets the offset for the structure in chunks. 7 by default.
		 * 
		 * @return int
		 */
		public int getOffset()
		{
			return this.offset != null ? this.offset.get() : 7;
		}

		/**
		 * Gets the whitelist mode for picking what biomes a structure should generate
		 * in. True by default.
		 * 
		 * @return boolean
		 */
		public boolean isWhitelist()
		{
			return this.isWhitelist != null ? this.isWhitelist.get() : true;
		}

		/**
		 * Returns the biome filter for determining where the structure should be
		 * allowed to generate. "" by default.
		 * 
		 * @return String
		 */
		public String getBiomeString()
		{
			return this.biomeString != null ? this.biomeString.get() : "";
		}

		/**
		 * Gets the list of biomes for the filter. Use
		 * {@link BiomeStructureConfig#isBiomeAllowed(Biome)} to use the
		 * whitelist/blacklist setting.
		 * 
		 * @return {@link List}
		 */
		public List<Biome> getBiomes()
		{
			return this.biomes;
		}

		/**
		 * Returns all spawns strings as a map.
		 * 
		 * @return Map
		 */
		public Map<EntityClassification, ForgeConfigSpec.ConfigValue<String>> getSpawnsStrings()
		{
			return this.spawnsStrings != null ? this.spawnsStrings : new HashMap<>();
		}

		/**
		 * Returns the specific spawns string for the classification passed. Null if not
		 * present. You shouldn't need this one. Check
		 * {@link #getSpawnsForClassification(EntityClassification)}
		 * 
		 * @param classification
		 * @return String
		 */
		public String getSpawnsString(EntityClassification classification)
		{
			return this.getSpawnsStrings().containsKey(classification) ? this.getSpawnsStrings().get(classification).get() : "";
		}

		/**
		 * Returns the spawn list for the specific classification.
		 * 
		 * @param classification
		 * @return List
		 */
		@Nullable
		public List<SpawnListEntry> getSpawnsForClassification(EntityClassification classification)
		{
			return this.getSpawns().get(classification);
		}

		/**
		 * Returns all spawn entries.
		 * 
		 * @return Map
		 */
		public Map<EntityClassification, List<SpawnListEntry>> getSpawns()
		{
			return this.spawns;
		}

		/**
		 * 
		 * @param event
		 */
		protected void onConfigLoad(ModConfig.ModConfigEvent event)
		{
			this.biomes = parseBiomes(this.getBiomeString());
			this.spawns = new HashMap<EntityClassification, List<SpawnListEntry>>()
			{
				private static final long serialVersionUID = 64168135463438L;

				{
					for (EntityClassification EC : EntityClassification.values())
						if (!getSpawnsString(EC).isEmpty())
							put(EC, parseSpawns(getSpawnsString(EC)));
				}
			};
		}

		/**
		 * Checks if the input biome is or isn't in the biomes list depending on if you
		 * use whitelist or blacklist mode.
		 * 
		 * @param biome
		 * @return boolean
		 */
		public boolean isBiomeAllowed(Biome biome)
		{
			return this.biomes.contains(biome) == this.isWhitelist();
		}

		/**
		 * Reads the biomes and tags from the config string and assigns them to the
		 * biomes list. Used internally.<br>
		 * "#overworld, !#forest, !minecraft:snowy_taiga, minecraft:flower_forest"
		 * 
		 * @param key
		 * @return List
		 */
		public static List<Biome> parseBiomes(String key)
		{
			List<Biome> biomes = new ArrayList<>();
			if (!key.isEmpty())
			{
				Arrays.asList(key.replace(" ", "").split(",")).stream().forEach(s ->
				{
					boolean not = s.startsWith("!");
					boolean isTag = s.replace("!", "").startsWith("#");
					String biomeString = s.replace("!", "").replace("#", "");

					if (!isTag)
					{
						ResourceLocation biome = new ResourceLocation(biomeString);
						if (ForgeRegistries.BIOMES.containsKey(biome))
							updateBiomeList(biomes, ForgeRegistries.BIOMES.getValue(biome), not);
					}
					else if (BiomeDictionary.Type.getType(biomeString) != null)
					{
						BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(biomeString)).forEach(biome -> updateBiomeList(biomes, biome, not));
					}
				});
			}
			return biomes;
		}

		/**
		 * Adds/removes the biome to/from the biomes list. Used internally.
		 * 
		 * @param biome
		 * @param not
		 */
		protected static void updateBiomeList(List<Biome> biomes, Biome biome, boolean not)
		{
			if (not)
			{
				if (biomes.contains(biome))
					biomes.remove(biome);
			}
			else if (!biomes.contains(biome))
				biomes.add(biome);
		}

		/**
		 * Reads the spawns set from the string and puts them into a list. Used
		 * internally.<br>
		 * "[zombie, 1, 2, 4][skeleton, 2, 2, 4]"
		 * 
		 * @param key
		 * @return List
		 */
		public static List<SpawnListEntry> parseSpawns(String key)
		{
			List<SpawnListEntry> spawns = new ArrayList<>();
			if (!key.isEmpty())
			{
				try
				{
					Matcher matcher = Pattern.compile("(\\[([a-z0-9/_:[-][.]]*),\\s*([0-9]*),\\s*([0-9]*),\\s*([0-9*])\\])").matcher(key);
					while (matcher.find())
					{
						ResourceLocation entity = new ResourceLocation(matcher.group(2));
						if (ForgeRegistries.ENTITIES.containsKey(entity))
							spawns.add(new SpawnListEntry(ForgeRegistries.ENTITIES.getValue(entity), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5))));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			return spawns;
		}
	}

	/**
	 * Useful in conjunction with {@link GelStructure} to make generation settings
	 * configurable.
	 * 
	 * @author David
	 *
	 */
	public static class StructureConfig extends StructureConfigBuilder
	{
		/**
		 * 
		 * @param builder
		 * @param name
		 * @param probability
		 * @param spacing
		 * @param offset
		 */
		public StructureConfig(ForgeConfigSpec.Builder builder, String name, double probability, int spacing, int offset)
		{
			super(builder, name);
			this.probability(probability).spacing(spacing).offset(offset);
		}
	}

	/**
	 * Extension of {@link StructureConfig} that allows allows stores what biomes a
	 * structure should generate in. Use this setting in {@link FMLCommonSetupEvent}
	 * to register the structure in the appropriate biomes.
	 * 
	 * @author David
	 *
	 */
	public static class BiomeStructureConfig extends StructureConfig
	{

		/**
		 * 
		 * @param builder
		 * @param name
		 * @param probability
		 * @param spacing
		 * @param offset
		 * @param biomes
		 * @param isWhitelist
		 */
		public BiomeStructureConfig(ForgeConfigSpec.Builder builder, String name, double probability, int spacing, int offset, String biomes, boolean isWhitelist)
		{
			super(builder, name, probability, spacing, offset);
			this.biomes(isWhitelist, biomes);
		}

		/**
		 * 
		 * @param builder
		 * @param name
		 * @param probability
		 * @param spacing
		 * @param offset
		 * @param biomes : Entered as a comma separated string of resource locations.
		 *            You can put spaces, but you don't need to. Ex: "plains,
		 *            minecraft:swamp, biomesoplenty:origin_beach"
		 */
		public BiomeStructureConfig(ForgeConfigSpec.Builder builder, String name, double probability, int spacing, int offset, String biomes)
		{
			this(builder, name, probability, spacing, offset, biomes, true);
		}
	}
}

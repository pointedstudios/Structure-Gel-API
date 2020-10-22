package com.legacy.structure_gel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.legacy.structure_gel.biome_dictionary.BiomeDictionary;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.DimensionSettings;
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
	public static class StructureConfig
	{
		private final ForgeConfigSpec.Builder builder;
		private final String name;

		// Placement settings
		private ForgeConfigSpec.DoubleValue probability;
		private ForgeConfigSpec.IntValue spacing;
		private ForgeConfigSpec.IntValue offset;
		// Biome settings
		private ForgeConfigSpec.BooleanValue isWhitelist;
		private ForgeConfigSpec.ConfigValue<String> biomeString;
		private List<ResourceLocation> biomes = new ArrayList<>();
		// Mob spawn settings
		private Map<EntityClassification, ForgeConfigSpec.ConfigValue<String>> spawnsStrings = new HashMap<>();
		private Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawns = new HashMap<>();
		// Worldgen noise settings... settings. That's a mouthful
		private ForgeConfigSpec.ConfigValue<String> noiseSettingsString;
		@Nullable
		private List<DimensionSettings> noiseSettings = null;

		/**
		 * 
		 * @param builder
		 * @param name
		 */
		public StructureConfig(ForgeConfigSpec.Builder builder, String name)
		{
			this.builder = builder;
			this.name = name;
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoad);
		}

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
			this(builder, name);
			this.probability(probability).spacing(spacing).offset(offset);
		}

		public StructureConfig probability(double probability)
		{
			this.probability = builder.comment("Chance of generating in an allowed chunk").defineInRange(name + ".probability", probability, 0.0D, 1.0D);
			return this;
		}

		public StructureConfig spacing(int spacing)
		{
			this.spacing = builder.comment("Spacing between structures").defineInRange(name + ".spacing", spacing, 1, Integer.MAX_VALUE);
			return this;
		}

		public StructureConfig offset(int offset)
		{
			this.offset = builder.comment("Offsets the spacing of the structures randomly").defineInRange(name + ".offset", offset, 0, Integer.MAX_VALUE);
			return this;
		}

		public StructureConfig biomes(boolean isWhitelist, String biomes)
		{
			this.biomeString = builder.comment("A biome filter to determine where the structure should generate. Works with the biome dictionary (#overworld) and \"not\" statements (!plains). These can be combined (!#nether). Operates in the order presented. So \"#forest, !flower_forest\" will add all forests and then remove the flower forest.").define(name + ".biomes", biomes);
			this.isWhitelist = builder.comment("How should the code treate biomes? true = whitelist, false = blacklist. Biomes defined with ! do the opposite.").define(name + ".is_whitelist", isWhitelist);
			return this;
		}

		public StructureConfig spawns(Map<EntityClassification, String> spawns)
		{
			for (EntityClassification classification : EntityClassification.values())
				if (spawns.containsKey(classification))
					this.getSpawnsStrings().put(classification, builder.define(name + ".spawns." + classification.getName(), spawns.get(classification)));
			return this;
		}

		public StructureConfig noiseSettings(String noiseSettings)
		{
			this.noiseSettingsString = builder.comment("What dimension noise settings should this structure be placed in. Default options are \"overworld\", \"amplified\", \"end\", \"nether\", \"caves\", and \"floating_islands\"").define(name + ".noise_settings", noiseSettings);
			return this;
		}

		public StructureConfig noiseSettings(DimensionSettings... noiseSettings)
		{
			String string = "";
			for (int i = 0; i < noiseSettings.length; i++)
			{
				string = string + WorldGenRegistries.NOISE_SETTINGS.getKey(noiseSettings[i]).toString();
				if (i < noiseSettings.length - 1)
					string = string + ", ";
			}
			return this.noiseSettings(string);
		}

		/**
		 * Gets the probability for the structure to generate. 1.0 by default.
		 * 
		 * @return {@link Double}
		 */
		public double getProbability()
		{
			return this.probability != null ? this.probability.get() : 1.0D;
		}

		/**
		 * Gets the spacing for the structure in chunks. 16 by default.
		 * 
		 * @return {@link Integer}
		 */
		public int getSpacing()
		{
			return this.spacing != null ? this.spacing.get() : 16;
		}

		/**
		 * Gets the offset for the structure in chunks. 7 by default.
		 * 
		 * @return {@link Integer}
		 */
		public int getOffset()
		{
			return this.offset != null ? this.offset.get() : 7;
		}

		/**
		 * Gets the whitelist mode for picking what biomes a structure should generate
		 * in. True by default.
		 * 
		 * @return {@link Boolean}
		 */
		public boolean isWhitelist()
		{
			return this.isWhitelist != null ? this.isWhitelist.get() : true;
		}

		/**
		 * Returns the biome filter for determining where the structure should be
		 * allowed to generate. "" by default.
		 * 
		 * @return {@link String}
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
		public List<ResourceLocation> getBiomes()
		{
			return this.biomes;
		}

		/**
		 * Returns all spawns strings as a map.
		 * 
		 * @return {@link Map}
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
		 * @return {@link String}
		 */
		public String getSpawnsString(EntityClassification classification)
		{
			return this.getSpawnsStrings().containsKey(classification) ? this.getSpawnsStrings().get(classification).get() : "";
		}

		/**
		 * Returns the spawn list for the specific classification.
		 * 
		 * @param classification
		 * @return {@link List}
		 */
		@Nullable
		public List<MobSpawnInfo.Spawners> getSpawnsForClassification(EntityClassification classification)
		{
			return this.getSpawns().get(classification);
		}

		/**
		 * Returns all spawn entries.
		 * 
		 * @return {@link Map}
		 */
		public Map<EntityClassification, List<MobSpawnInfo.Spawners>> getSpawns()
		{
			return this.spawns;
		}

		/**
		 * Returns the dimension noise settings that this structure is allowed to
		 * generate with. "minecraft:overworld, minecraft:amplified, minecraft:nether,
		 * minecraft:end, minecraft:caves, minecraft:floating_islands" by default.
		 * 
		 * @return {@link String}
		 */
		@Nullable
		public String getNoiseSettingsString()
		{
			return this.noiseSettingsString != null ? this.noiseSettingsString.get() : null;
		}

		/**
		 * Returns the dimension noise settings that this structure is allowed to
		 * generate with. Null if no value is set.
		 * 
		 * @return {@link List}
		 */
		@Nullable
		public List<DimensionSettings> getNoiseSettings()
		{
			return this.noiseSettings;
		}

		/**
		 * Parses config strings and resets lists. Some things may need restarting.
		 * 
		 * @param event
		 */
		protected void onConfigLoad(ModConfig.ModConfigEvent event)
		{
			this.spawns = new HashMap<EntityClassification, List<MobSpawnInfo.Spawners>>()
			{
				private static final long serialVersionUID = 64168135463438L;

				{
					for (EntityClassification EC : EntityClassification.values())
						if (!getSpawnsString(EC).isEmpty())
							put(EC, parseSpawns(getSpawnsString(EC)));
				}
			};

			this.biomes = parseBiomes(this.getBiomeString());
			this.noiseSettings = parseNoiseSettings(this.getNoiseSettingsString());
		}

		/**
		 * Checks if the input biome is or isn't in the biomes list depending on if you
		 * use whitelist or blacklist mode.
		 * 
		 * @param biome
		 * @return {@link Boolean}
		 */
		public boolean isBiomeAllowed(Biome biome)
		{
			return this.isBiomeAllowed(biome.getRegistryName());
		}

		/**
		 * Checks if the input biome is or isn't in the biomes list depending on if you
		 * use whitelist or blacklist mode.
		 * 
		 * @param biome
		 * @return {@link Boolean}
		 */
		public boolean isBiomeAllowed(RegistryKey<Biome> biome)
		{
			return this.isBiomeAllowed(biome.getLocation());
		}

		/**
		 * Checks if the input biome is or isn't in the biomes list depending on if you
		 * use whitelist or blacklist mode.
		 * 
		 * @param biome
		 * @return {@link Boolean}
		 */
		public boolean isBiomeAllowed(ResourceLocation biome)
		{
			return this.biomes.contains(biome) == this.isWhitelist();
		}

		/**
		 * Reads the biomes and tags from the config string and assigns them to the
		 * biomes list. Used internally.<br>
		 * "#overworld, !#forest, !minecraft:snowy_taiga, minecraft:flower_forest"
		 * 
		 * @param key
		 * @return {@link List}
		 */
		public List<ResourceLocation> parseBiomes(String key)
		{
			List<ResourceLocation> biomes = new ArrayList<>();
			if (!key.isEmpty())
			{
				Arrays.asList(key.replace(" ", "").split(",")).stream().forEach(s ->
				{
					boolean not = s.startsWith("!");
					boolean isTag = s.replace("!", "").startsWith("#");
					String biomeString = s.replace("!", "").replace("#", "");
					ResourceLocation value = new ResourceLocation(biomeString);

					if (!isTag)
						updateBiomeList(biomes, value, not);
					else if (BiomeDictionary.contains(value))
						BiomeDictionary.get(value).getAllBiomes().forEach(b -> updateBiomeList(biomes, b.getLocation(), not));
				});
			}
			return biomes;
		}

		/**
		 * Adds/removes the biome to/from the biomes list.
		 * 
		 * @param biome
		 * @param not
		 */
		@Internal
		protected static void updateBiomeList(List<ResourceLocation> biomes, ResourceLocation biome, boolean not)
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
		 * @return {@link List}
		 */
		public List<MobSpawnInfo.Spawners> parseSpawns(String key)
		{
			List<MobSpawnInfo.Spawners> spawns = new ArrayList<>();
			if (!key.isEmpty())
			{
				try
				{
					Matcher matcher = Pattern.compile("(\\[([a-z0-9/_:[-][.]]*),\\s*([0-9]*),\\s*([0-9]*),\\s*([0-9*])\\])").matcher(key);
					while (matcher.find())
					{
						ResourceLocation entity = new ResourceLocation(matcher.group(2));
						if (ForgeRegistries.ENTITIES.containsKey(entity))
							spawns.add(new MobSpawnInfo.Spawners(ForgeRegistries.ENTITIES.getValue(entity), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5))));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			return spawns;
		}

		/**
		 * Reads the dimension noise settings listed in the string and returns a list
		 * containing their registered versions.
		 * 
		 * @param key
		 * @return {@link List}
		 */
		@Nullable
		public List<DimensionSettings> parseNoiseSettings(String key)
		{
			if (key == null)
				return null;
			
			List<DimensionSettings> noiseSettings = new ArrayList<>();
			if (!key.isEmpty())
			{		
				Arrays.asList(key.replace(" ", "").split(",")).stream().forEach(s ->
				{
					ResourceLocation settings = new ResourceLocation(s);
					if (WorldGenRegistries.NOISE_SETTINGS.getOptional(settings).isPresent())
						noiseSettings.add(WorldGenRegistries.NOISE_SETTINGS.getOptional(settings).get());
				});
			}
			return noiseSettings;
		}
	}

	/**
	 * Extension of {@link StructureConfig} that allows allows stores what biomes a
	 * structure should generate in. Use this setting in {@link FMLCommonSetupEvent}
	 * to register the structure in the appropriate biomes.
	 * 
	 * @author David
	 * @deprecated Use {@link StructureConfig}
	 */
	@Deprecated
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

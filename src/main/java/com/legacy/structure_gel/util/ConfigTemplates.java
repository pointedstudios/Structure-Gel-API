package com.legacy.structure_gel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.legacy.structure_gel.structures.GelStructure;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * These are templates that you can use for your structure's config settings. I
 * reccomend using {@link BiomeStructureConfig} as it lets you configure what
 * biome your structure can spawn in.
 * 
 * @author David
 *
 */
public class ConfigTemplates
{
	/**
	 * Useful in conjunction with {@link GelStructure} to make generation settings
	 * configurable.
	 * 
	 * @author David
	 *
	 */
	public static class StructureConfig
	{
		private final ForgeConfigSpec.DoubleValue probability;
		private final ForgeConfigSpec.IntValue spacing;
		private final ForgeConfigSpec.IntValue offset;

		/**
		 * 
		 * @param builder
		 * @param name
		 * @param defaultProbability
		 * @param defaultSpacing
		 * @param defaultOffset
		 */
		public StructureConfig(ForgeConfigSpec.Builder builder, String name, double defaultProbability, int defaultSpacing, int defaultOffset)
		{
			this.probability = builder.comment("Chance of generating in an allowed chunk").defineInRange(name + ".probability", defaultProbability, 0.0D, 1.0D);
			this.spacing = builder.comment("Spacing between structures").defineInRange(name + ".spacing", defaultSpacing, 1, Integer.MAX_VALUE);
			this.offset = builder.comment("Offsets the spacing of the structures randomly").defineInRange(name + ".offset", defaultOffset, 0, Integer.MAX_VALUE);
		}

		public double getProbability()
		{
			return this.probability.get();
		}

		public int getSpacing()
		{
			return this.spacing.get();
		}

		public int getOffset()
		{
			return this.offset.get();
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
		private final ForgeConfigSpec.ConfigValue<String> biomeString;
		private final ForgeConfigSpec.BooleanValue isWhitelist;
		private List<Biome> biomes = new ArrayList<>();

		/**
		 * @see BiomeDictionary.Type
		 * @param builder
		 * @param name
		 * @param defaultProbability
		 * @param defaultSpacing
		 * @param defaultOffset
		 * @param defaultBiomesString
		 * @param defaultIsWhitelist : true by default
		 */
		public BiomeStructureConfig(ForgeConfigSpec.Builder builder, String name, double defaultProbability, int defaultSpacing, int defaultOffset, String defaultBiomesString, boolean defaultIsWhitelist)
		{
			super(builder, name, defaultProbability, defaultSpacing, defaultOffset);
			this.biomeString = builder.comment("A biome filter to determine where the structure should generate. Works with the biome dictionary (#overworld) and \"not\" statements (!plains). These can be combined (!#nether). Operates in the order presented. So \"#forest, !flower_forest\" will add all forests and then remove the flower forest.").define(name + ".biomes", defaultBiomesString);
			this.isWhitelist = builder.comment("How should the code treate biomes? true = whitelist, false = blacklist. Biomes defined with ! do the opposite.").define(name + ".is_whitelist", defaultIsWhitelist);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoad);
		}

		/**
		 * 
		 * @param builder
		 * @param name
		 * @param defaultProbability
		 * @param defaultSpacing
		 * @param defaultOffset
		 * @param defaultBiomesString : Entered as a comma separated string of resource
		 *            locations. You can put spaces, but you don't need to. Ex: "plains,
		 *            minecraft:swamp, biomesoplenty:origin_beach"
		 */
		public BiomeStructureConfig(ForgeConfigSpec.Builder builder, String name, double defaultProbability, int defaultSpacing, int defaultOffset, String defaultBiomesString)
		{
			this(builder, name, defaultProbability, defaultSpacing, defaultOffset, defaultBiomesString, true);
		}

		/**
		 * Converts {@link #biomeString} to a list of {@link Biome} that can be checked
		 * when adding structures to a biome to see which biomes it can be added to.
		 * Automatically called on {@link ModConfig.ModConfigEvent} to refresh the list
		 * when the config changes.<br>
		 * <br>
		 * Invalid biome IDs are defaulted to minecraft:plains
		 * 
		 * @param event
		 */
		@SubscribeEvent
		protected void onConfigLoad(ModConfig.ModConfigEvent event)
		{
			this.biomes.clear();
			parseBiomes(this.biomeString.get());
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
		 * Checks if the input biome is or isn't in the biomes list depending on if you
		 * use whitelist or blacklist mode.
		 * 
		 * @param biome
		 * @return
		 */
		public boolean isBiomeAllowed(Biome biome)
		{
			return this.biomes.contains(biome) == this.isWhitelist.get();
		}

		/**
		 * Reads the biomes and tags from the config string and assigns them to the
		 * biomes list. Used internally.
		 * 
		 * @param key
		 */
		protected void parseBiomes(String key)
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
						updateBiomeList(ForgeRegistries.BIOMES.getValue(biome), not);
				}
				else if (BiomeDictionary.Type.getType(biomeString) != null)
				{
					BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(biomeString)).forEach(biome -> updateBiomeList(biome, not));
				}
			});
		}

		/**
		 * Adds/removes the biome to/from the biomes list. Used internally.
		 * 
		 * @param biome
		 * @param not
		 */
		protected void updateBiomeList(Biome biome, boolean not)
		{
			if (not)
			{
				if (this.biomes.contains(biome))
					this.biomes.remove(biome);
			}
			else if (!this.biomes.contains(biome))
				this.biomes.add(biome);
		}
	}
}

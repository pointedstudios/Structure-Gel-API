package com.legacy.structure_gel;

import java.util.Arrays;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.structures.GelStructure;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

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
		private ImmutableList<Biome> biomes = ImmutableList.of();

		/**
		 * 
		 * @param builder
		 * @param name
		 * @param defaultProbability
		 * @param defaultSpacing
		 * @param defaultOffset
		 * @param defaultBiomesString
		 * @param biomeDescription : By default, this string is written as if you'd use
		 *            it as a whitelist. You can change the description with this
		 */
		public BiomeStructureConfig(ForgeConfigSpec.Builder builder, String name, double defaultProbability, int defaultSpacing, int defaultOffset, String defaultBiomesString, String biomeDescription)
		{
			super(builder, name, defaultProbability, defaultSpacing, defaultOffset);
			this.biomeString = builder.comment(biomeDescription).define(name + ".biomes", defaultBiomesString);
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
			this(builder, name, defaultProbability, defaultSpacing, defaultOffset, defaultBiomesString, "Biomes that this can generate in, separated by comma. Spaces are allowed but not needed.");
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
			this.biomes = parseBiomes(this.biomeString.get());
		}

		/**
		 * Gets the list of biomes that a structure is allowed to spawn in.<br>
		 * <br>
		 * You can call this during {@link FMLCommonSetupEvent} to register only to
		 * biomes in the config (requires restart, but slightly more optimal for
		 * performance since it only happens on startup), or when the structure checks
		 * to see if it can be placed in a given chunk (no restart required, but
		 * potentially worse performance since it happens every time a chunk generates)
		 * 
		 * @return {@link ImmutableList}
		 */
		public ImmutableList<Biome> getBiomes()
		{
			return this.biomes;
		}

		public static ImmutableList<Biome> parseBiomes(String key)
		{
			if (key.isEmpty())
				return ImmutableList.of();
			return Arrays.asList(key.replace(" ", "").split(",")).stream().map(s ->
			{
				if (ForgeRegistries.BIOMES.containsKey(new ResourceLocation(s)))
					return ForgeRegistries.BIOMES.getValue(new ResourceLocation(s));
				else
				{
					StructureGelMod.LOGGER.warn(String.format("Tried to parse the biome \"%s\" but it is not registered. Defaulting to plains.", s, key));
					return Biomes.PLAINS;
				}
			}).collect(ImmutableList.toImmutableList());
		}
	}
}

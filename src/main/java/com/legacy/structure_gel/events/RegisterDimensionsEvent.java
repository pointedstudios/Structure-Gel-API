package com.legacy.structure_gel.events;

import com.mojang.serialization.Lifecycle;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.eventbus.api.Event;

/**
 * Called when creating a new world. Register your dimensions in here to create
 * one without being stuck with a set seed. Do not have your dimension in the
 * data folder of your mod or else the dimension registered here will be
 * overritten.
 * 
 * @author David
 *
 */
public class RegisterDimensionsEvent extends Event
{
	private final SimpleRegistry<Dimension> dimensionRegistry;
	private final Registry<DimensionType> dimensionTypeRegistry;
	private final Registry<Biome> biomeRegistry;
	private final Registry<DimensionSettings> dimensionSettingsRegistry;
	private final long seed;

	public RegisterDimensionsEvent(SimpleRegistry<Dimension> dimensionRegistry, Registry<DimensionType> dimensionTypeRegistry, Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed)
	{
		this.dimensionRegistry = dimensionRegistry;
		this.dimensionTypeRegistry = dimensionTypeRegistry;
		this.biomeRegistry = biomeRegistry;
		this.dimensionSettingsRegistry = dimensionSettingsRegistry;
		this.seed = seed;
	}

	public SimpleRegistry<Dimension> getDimensionRegistry()
	{
		return this.dimensionRegistry;
	}

	public Registry<DimensionType> getDimensionTypeRegistry()
	{
		return dimensionTypeRegistry;
	}

	public Registry<Biome> getBiomeRegistry()
	{
		return biomeRegistry;
	}

	public Registry<DimensionSettings> getDimensionSettingsRegistry()
	{
		return dimensionSettingsRegistry;
	}

	/**
	 * This is the seed of the overworld.
	 * 
	 * @return {@link Long}
	 */
	public long getSeed()
	{
		return seed;
	}

	/**
	 * Registers the passed dimension to the world.
	 * 
	 * @param registryKey
	 * @param dimension
	 */
	public void register(RegistryKey<Dimension> registryKey, Dimension dimension)
	{
		this.dimensionRegistry.register(registryKey, dimension, Lifecycle.stable());
	}
}

package com.legacy.structure_gel.registrars;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.legacy.structure_gel.events.RegisterDimensionEvent;
import com.legacy.structure_gel.util.DimensionTypeBuilder;
import com.mojang.serialization.Lifecycle;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;

/**
 * Handles registering dimensions. Create a new instance of this during the
 * {@link RegisterDimensionEvent} to have it automatically register a dimension
 * with the values passed in.<br>
 * <br>
 * Any values present in the data folder will override the values set here.
 * 
 * @author David
 *
 */
public class DimensionRegistrar implements IRegistrar<DimensionRegistrar>
{
	private final RegisterDimensionEvent event;
	private final RegistryKey<Dimension> dimensionKey;
	private final RegistryKey<DimensionType> typeKey;
	private final RegistryKey<DimensionSettings> settingsKey;
	private final RegistryKey<World> worldKey;
	private final Supplier<DimensionType> type;
	private final Function<RegistryKey<DimensionSettings>, DimensionSettings> settings;
	private final BiFunction<RegisterDimensionEvent, DimensionSettings, ChunkGenerator> chunkGenerator;

	/**
	 * 
	 * @param event The event that stores data needed to register your dimension.
	 * @param key The name of your dimension, dimension type, and dimension
	 *            settings.
	 * @param type A {@link Supplier} that returns your {@link DimensionType}. Check
	 *            {@link DimensionTypeBuilder}.
	 * @param settings A {@link Function} that creates your
	 *            {@link DimensionSettings} with the passed {@link RegistryKey}.
	 * @param chunkGenerator A {@link BiFunction} that creates your
	 *            {@link ChunkGenerator} with the passed
	 *            {@link RegisterDimensionEvent} and {@link DimensionSettings}.
	 */
	public DimensionRegistrar(RegisterDimensionEvent event, ResourceLocation key, Supplier<DimensionType> type, Function<RegistryKey<DimensionSettings>, DimensionSettings> settings, BiFunction<RegisterDimensionEvent, DimensionSettings, ChunkGenerator> chunkGenerator)
	{
		this.event = event;
		this.dimensionKey = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, key);
		this.typeKey = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, key);
		this.settingsKey = RegistryKey.getOrCreateKey(Registry.NOISE_SETTINGS_KEY, key);
		this.worldKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, key);
		this.type = type;
		this.settings = settings;
		this.chunkGenerator = chunkGenerator;
	}

	/**
	 * Returns the dimension registry key.
	 * 
	 * @return {@link RegistryKey}
	 */
	public RegistryKey<Dimension> getDimensionKey()
	{
		return this.dimensionKey;
	}

	/**
	 * Returns the dimension type registry key.
	 * 
	 * @return {@link RegistryKey}
	 */
	public RegistryKey<DimensionType> getTypeKey()
	{
		return this.typeKey;
	}

	/**
	 * Returns the dimension settings registry key.
	 * 
	 * @return {@link RegistryKey}
	 */
	public RegistryKey<DimensionSettings> getSettingsKey()
	{
		return this.settingsKey;
	}

	/**
	 * Returns the world key.
	 * 
	 * @return {@link RegistryKey}
	 */
	public RegistryKey<World> getWorldKey()
	{
		return this.worldKey;
	}

	@Override
	public DimensionRegistrar handle()
	{
		DimensionSettings settings = WorldGenRegistries.register(WorldGenRegistries.NOISE_SETTINGS, this.settingsKey.getLocation(), this.settings.apply(this.settingsKey));
		DimensionType type = DynamicRegistries.func_239770_b_().getRegistry(Registry.DIMENSION_TYPE_KEY).register(this.typeKey, this.type.get(), Lifecycle.stable());

		event.register(this.dimensionKey, new Dimension(() -> type, this.chunkGenerator.apply(event, settings)));
		return this;
	}
}

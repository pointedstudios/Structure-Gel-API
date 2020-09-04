package com.legacy.structure_gel.registrars;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.legacy.structure_gel.events.RegisterDimensionEvent;
import com.legacy.structure_gel.util.DimensionTypeBuilder;
import com.legacy.structure_gel.util.Internal;
import com.mojang.serialization.Lifecycle;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
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
public class DimensionRegistrar
{
	private final RegistryKey<Dimension> dimensionKey;
	private final RegistryKey<DimensionType> typeKey;
	private final RegistryKey<DimensionSettings> settingsKey;
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
		this.dimensionKey = RegistryKey.func_240903_a_(Registry.DIMENSION_KEY, key);
		this.typeKey = RegistryKey.func_240903_a_(Registry.DIMENSION_TYPE_KEY, key);
		this.settingsKey = RegistryKey.func_240903_a_(Registry.field_243549_ar, key);
		this.type = type;
		this.settings = settings;
		this.chunkGenerator = chunkGenerator;
		this.register(event);
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
	 * Registers the dimension with the provided values.
	 * 
	 * @param event
	 */
	@Internal
	public void register(RegisterDimensionEvent event)
	{
		DimensionSettings settings = WorldGenRegistries.func_243664_a(WorldGenRegistries.field_243658_j, this.settingsKey.func_240901_a_(), this.settings.apply(this.settingsKey));
		DimensionType type = DynamicRegistries.func_239770_b_().func_243612_b(Registry.DIMENSION_TYPE_KEY).register(this.typeKey, this.type.get(), Lifecycle.stable());

		event.register(this.dimensionKey, new Dimension(() -> type, this.chunkGenerator.apply(event, settings)));
	}
}

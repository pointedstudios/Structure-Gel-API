package com.legacy.structure_gel.registrars;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.util.RegistryHelper;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Stores a {@link Biome} and its {@link RegistryKey} and registers with
 * {@link #handle()} or {@link RegistryHelper#handleRegistrar(IRegistrar)}.
 * 
 * @author David
 *
 */
public class BiomeRegistrar implements IForgeRegistrar<BiomeRegistrar, Biome>
{
	private final ResourceLocation name;
	private final Supplier<Biome> biomeMaker;
	@Nullable
	private RegistryKey<Biome> biomeKey;
	@Nullable
	private Biome biome;

	public BiomeRegistrar(ResourceLocation name, Supplier<Biome> biomeMaker)
	{
		this.name = name;
		this.biomeMaker = biomeMaker;
	}

	/**
	 * Gets the {@link Biome} held by this registrar.<br>
	 * <br>
	 * Returns null if it hasn't been registered yet.
	 * 
	 * @return {@link Biome}
	 */
	@Nullable
	public Biome getBiome()
	{
		return this.biome;
	}

	/**
	 * Gets the {@link RegistryKey} held by this registrar.<br>
	 * <br>
	 * Returns null if it hasn't been registered yet.
	 * 
	 * @return {@link RegistryKey}
	 */
	@Nullable
	public RegistryKey<Biome> getKey()
	{
		return this.biomeKey;
	}

	/**
	 * Creates the biome from the supplier passed. You shouldn't need to call this
	 * as it's done automatically during registry.
	 * 
	 * @return {@link Biome}
	 */
	@Internal
	public Biome makeBiome()
	{
		if (this.biome == null)
			this.biome = biomeMaker.get();
		return this.biome;
	}

	@Override
	public BiomeRegistrar handle()
	{
		RegistryKey.getOrCreateKey(Registry.BIOME_KEY, this.name);
		return this;
	}

	@Override
	public BiomeRegistrar handleForge(IForgeRegistry<Biome> registry)
	{
		RegistryHelper.register(registry, this.name, this.makeBiome());
		return this;
	}
}

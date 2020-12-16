package com.legacy.structure_gel.registrars;

import com.legacy.structure_gel.util.RegistryHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

/**
 * Stores a {@link Biome} and its {@link RegistryKey} and registers with
 * {@link #handle()} or {@link RegistryHelper#handleRegistrar(IRegistrar)}.
 *
 * @author David
 */
public class BiomeRegistrar implements IForgeRegistrar<BiomeRegistrar, Biome>
{
	private final ResourceLocation name;
	private final NonNullSupplier<Biome> biomeMaker;
	private RegistryKey<Biome> biomeKey;
	private Biome biome;

	public BiomeRegistrar(ResourceLocation name, NonNullSupplier<Biome> biomeMaker)
	{
		this.name = name;
		this.biomeMaker = biomeMaker;
	}

	public BiomeRegistrar(ResourceLocation name, @Nonnull Biome biome)
	{
		this(name, () -> biome);
	}

	/**
	 * Gets the name used in registry. Effectively the same as getting the registry
	 * name of the biome through it.
	 *
	 * @return {@link ResourceLocation}
	 */
	public ResourceLocation getName()
	{
		return this.name;
	}

	/**
	 * Gets the {@link Biome} held by this registrar.
	 *
	 * @return {@link Biome}
	 * @throws NullPointerException If the Biome has not yet been created through
	 *                              {@link #handleForge(IForgeRegistry)}
	 */
	public Biome getBiome() throws NullPointerException
	{
		if (this.biome == null)
			throw new NullPointerException(String.format("The biome %s has not been regitered yet. Use BiomeRegistrar.handleForge(IForgeRegistry)", this.name.toString()));
		return this.biome;
	}

	/**
	 * Gets the {@link RegistryKey} held by this registrar.
	 *
	 * @return {@link RegistryKey}
	 * @throws NullPointerException If the RegistryKey has not yet been created
	 *                              through {@link #handle()}
	 */
	public RegistryKey<Biome> getKey() throws NullPointerException
	{
		if (this.biomeKey == null)
			throw new NullPointerException(String.format("The biome registry key for %s has not been regitered yet. Use BiomeRegistrar.handle()", this.name.toString()));
		return this.biomeKey;
	}

	@Override
	public BiomeRegistrar handle()
	{
		this.biomeKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, this.name);
		return this;
	}

	@Override
	public BiomeRegistrar handleForge(IForgeRegistry<Biome> registry)
	{
		this.biome = RegistryHelper.register(registry, this.name, this.biomeMaker.get());
		return this;
	}
}

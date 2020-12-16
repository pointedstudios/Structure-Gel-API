package com.legacy.structure_gel.biome_dictionary;

import com.google.common.collect.Sets;
import com.legacy.structure_gel.registrars.BiomeRegistrar;
import com.legacy.structure_gel.util.Internal;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A forge biome dictionary compatible version of {@link BiomeType}. This is
 * effectively immutable, so you cannot add biomes/parents to it.
 *
 * @author David
 */
@Internal
public class ForgeType extends BiomeType
{
	/**
	 * The forge biome dictionary type held by this instance
	 */
	private final BiomeDictionary.Type type;

	/**
	 * @param type
	 * @see #create(net.minecraftforge.common.BiomeDictionary.Type)
	 */
	private ForgeType(BiomeDictionary.Type type)
	{
		super(new ResourceLocation("forge", type.getName().toLowerCase(Locale.ENGLISH)), Sets.newHashSet(), Sets.newHashSet());
		this.type = type;
	}

	/**
	 * Creates a new instance of this.
	 *
	 * @param type
	 * @return {@link ForgeType}
	 */
	public static ForgeType create(BiomeDictionary.Type type)
	{
		return new ForgeType(type);
	}

	/**
	 * Returns the forge biome dictionary {@link Type} referenced by this instance.
	 *
	 * @return {@link Type}
	 */
	public BiomeDictionary.Type getType()
	{
		return this.type;
	}

	/**
	 * Gets the biomes from this instance.
	 *
	 * @return {@link Set}
	 */
	@Override
	public Set<ResourceLocation> getBiomes()
	{
		return BiomeDictionary.getBiomes(this.type).stream().map(RegistryKey::getLocation).collect(Collectors.toSet());
	}

	/**
	 * Gets the biomes from this instance.
	 *
	 * @return {@link Set}
	 */
	@Override
	public Set<RegistryKey<Biome>> getAllBiomes()
	{
		return this.getBiomes().stream().filter(ForgeRegistries.BIOMES::containsKey).map(r -> RegistryKey.getOrCreateKey(ForgeRegistries.Keys.BIOMES, r)).collect(Collectors.toSet());
	}

	/**
	 * Does this instance contain the biome passed.
	 *
	 * @param key
	 * @return {@link Boolean}
	 */
	@Override
	public boolean contains(RegistryKey<Biome> key)
	{
		return this.contains(key.getLocation());
	}

	/**
	 * Does this instance contain the biome passed.
	 *
	 * @param biome
	 * @return {@link Boolean}
	 */
	@Override
	public boolean contains(Biome biome)
	{
		return this.contains(biome.getRegistryName());
	}

	/**
	 * Does this instance contain the biome passed.
	 *
	 * @param key
	 * @return {@link Boolean}
	 */
	@Override
	public boolean contains(ResourceLocation key)
	{
		return BiomeDictionary.getBiomes(this.type).stream().anyMatch(registryKey -> registryKey.getLocation().equals(key));
	}

	/**
	 * Compares the registry name of the {@link ForgeType} passed in to this.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ForgeType)
			return ((ForgeType) obj).getRegistryName().equals(this.getRegistryName());
		else
			return false;
	}

	@Override
	public String toString()
	{
		String biomes = String.join(", ", this.getBiomes().stream().map(ResourceLocation::toString).sorted().collect(Collectors.toSet()));
		return String.format("name = %s, biomes = [%s]", this.getRegistryName().toString(), biomes);
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	@Override
	public ForgeType biomes(RegistryKey<Biome>... biomes)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType biomes(Biome... biomes)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType biomes(ResourceLocation... biomes)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType biomes(BiomeRegistrar... biomes)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType biomes(String modid, String... biomes)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType addBiomes(Set<ResourceLocation> biomes)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType addBiome(RegistryKey<Biome> biome)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType addBiome(Biome biome)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType addBiome(ResourceLocation biome)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType addBiome(BiomeRegistrar biome)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType setBiomes(Set<ResourceLocation> biomes)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType parents(ResourceLocation... parents)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType parents(BiomeType... parents)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType addParents(Set<ResourceLocation> parents)
	{
		return this;
	}

	@Deprecated
	@Override
	public ForgeType addParent(ResourceLocation parent)
	{
		return this;
	}

	@Deprecated
	@Override
	public Set<ResourceLocation> getParents()
	{
		return Sets.newHashSet();
	}

	@Deprecated
	@Override
	public ForgeType setParents(Set<ResourceLocation> parents)
	{
		return this;
	}
}

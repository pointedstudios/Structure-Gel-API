package com.legacy.structure_gel.biome_dictionary;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A forge biome dictionary compatible version of {@link BiomeType}.
 * 
 * @author David
 *
 */
@Internal
public class ForgeType extends BiomeType
{
	/**
	 * The forge biome dictionary type held by this instance
	 */
	public final BiomeDictionary.Type type;

	/**
	 * @see #create(net.minecraftforge.common.BiomeDictionary.Type)
	 * @param type
	 */
	private ForgeType(BiomeDictionary.Type type)
	{
		super(new ResourceLocation("forge", type.getName().toLowerCase()), Sets.newHashSet(), Sets.newHashSet());
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
		String biomes = String.join(", ", this.getBiomes().stream().map(ResourceLocation::toString).collect(Collectors.toSet()));
		return String.format("name = %s, biomes = [%s]", this.getRegistryName().toString(), biomes);
	}
}

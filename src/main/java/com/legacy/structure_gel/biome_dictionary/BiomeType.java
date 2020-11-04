package com.legacy.structure_gel.biome_dictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Stores a collection of biomes that should have similar traits for referencing
 * in worldgen. A desert biome would be considered similar to a desert hills
 * biome, so they would make sense to be together.<br>
 * <br>
 * When registering a BiomeType of your own, check the {@link BiomeDictionary}
 * class since handling registry data is a bit different.
 * 
 * @see BiomeDictionary
 * @author David
 *
 */
public class BiomeType implements IForgeRegistryEntry<BiomeType>
{
	/**
	 * The name that this is registered as. Defaults to "structure_gel:empty"
	 */
	private ResourceLocation registryName = BiomeDictionary.EMPTY_NAME;
	/**
	 * The biomes in this instance. Defaults to an empty set.
	 */
	private Set<ResourceLocation> biomes = new HashSet<>();
	/**
	 * When running {@link #getAllBiomes()}, it will return all of the biomes of
	 * this entry and all of the biomes of any parent listed. Defaults to an empty
	 * set.
	 */
	private Set<ResourceLocation> parents = new HashSet<>();

	/**
	 * Ensures none of the values will be set to null just in case.
	 * 
	 * @param name
	 * @param parents
	 * @param biomes
	 */
	public BiomeType(ResourceLocation name, Set<ResourceLocation> parents, Set<ResourceLocation> biomes)
	{
		if (name != null)
			this.registryName = name;
		if (parents != null)
			this.parents = new HashSet<>(parents);
		if (biomes != null)
			this.biomes = new HashSet<>(biomes);
	}

	/**
	 * @see BiomeType#create(ResourceLocation)
	 */
	@Internal
	private BiomeType()
	{

	}

	/**
	 * Start creating the {@link BiomeType}.
	 * 
	 * @param name
	 * @return {@link BiomeType}
	 */
	public static BiomeType create(ResourceLocation name)
	{
		BiomeType biomeType = new BiomeType();
		biomeType.registryName = name;
		return biomeType;
	}

	/**
	 * Start creating the {@link BiomeType}.
	 * 
	 * @see #create(ResourceLocation)
	 * @param name
	 * @return {@link BiomeType}
	 */
	@Internal
	protected static BiomeType create(String name)
	{
		return create(StructureGelMod.locate(name));
	}

	/**
	 * Adds the biomes to this instance
	 * 
	 * @param biomes
	 * @return {@link BiomeType}
	 */
	@SuppressWarnings("unchecked")
	public BiomeType biomes(RegistryKey<Biome>... biomes)
	{
		this.addBiomes(Arrays.asList(biomes).stream().map(r -> r.getLocation()).collect(Collectors.toSet()));
		return this;
	}

	/**
	 * Adds the biomes to this instance
	 * 
	 * @param biomes
	 * @return {@link BiomeType}
	 */
	public BiomeType biomes(Biome... biomes)
	{
		this.addBiomes(Arrays.asList(biomes).stream().map(r -> r.getRegistryName()).collect(Collectors.toSet()));
		return this;
	}

	/**
	 * Adds the biomes to this instance
	 * 
	 * @param biomes
	 * @return {@link BiomeType}
	 */
	public BiomeType biomes(ResourceLocation... biomes)
	{
		this.addBiomes(Arrays.asList(biomes).stream().collect(Collectors.toSet()));
		return this;
	}

	/**
	 * Adds the biomes to this instance for the modid provided.
	 * 
	 * @param modid
	 * @param biomes
	 * @return {@link BiomeType}
	 */
	public BiomeType biomes(String modid, String... biomes)
	{
		Set<ResourceLocation> set = new HashSet<>();
		for (String biome : biomes)
			set.add(new ResourceLocation(modid, biome));
		this.addBiomes(set);
		return this;
	}

	/**
	 * Adds the listed biomes to this instance.
	 * 
	 * @param biomes
	 * @return {@link BiomeType}
	 */
	public BiomeType addBiomes(Set<ResourceLocation> biomes)
	{
		this.getBiomes().addAll(biomes);
		return this;
	}

	/**
	 * Add the biome to this instance.
	 * 
	 * @param biome
	 * @return {@link BiomeType}
	 */
	public BiomeType addBiome(RegistryKey<Biome> biome)
	{
		return addBiome(biome.getLocation());
	}

	/**
	 * Add the biome to this instance.
	 * 
	 * @param biome
	 * @return {@link BiomeType}
	 */
	public BiomeType addBiome(Biome biome)
	{
		return addBiome(biome.getRegistryName());
	}

	/**
	 * Add the biome to this instance.
	 * 
	 * @param biome
	 * @return {@link BiomeType}
	 */
	public BiomeType addBiome(ResourceLocation biome)
	{
		this.getBiomes().add(biome);
		return this;
	}

	/**
	 * Gets the biomes from this instance.
	 * 
	 * @return {@link Set}
	 */
	public Set<ResourceLocation> getBiomes()
	{
		return this.biomes;
	}

	/**
	 * Sets the biomes of this entry, overriding old ones. You probably shouldn't
	 * use this unless you're creating a new instance.
	 * 
	 * @param biomes
	 * @return {@link BiomeType}
	 */
	public BiomeType setBiomes(Set<ResourceLocation> biomes)
	{
		this.biomes = biomes.stream().collect(Collectors.toSet());
		return this;
	}

	/**
	 * Adds the parents to this instance
	 * 
	 * @param parents
	 * @return {@link BiomeType}
	 */
	public BiomeType parents(ResourceLocation... parents)
	{
		this.setParents(new HashSet<>(Arrays.asList(parents)));
		return this;
	}

	/**
	 * Set the parents for this instance.
	 * 
	 * @param parents
	 * @return {@link BiomeType}
	 */
	public BiomeType parents(BiomeType... parents)
	{
		this.parents = Arrays.asList(parents).stream().map(b -> b.getRegistryName()).collect(Collectors.toSet());
		return this;
	}

	/**
	 * Does not allow duplicates.
	 * 
	 * @param parents
	 * @return {@link BiomeType}
	 */
	public BiomeType addParents(Set<ResourceLocation> parents)
	{
		this.getParents().addAll(parents);
		return this;
	}

	/**
	 * Does not allow duplicates.
	 * 
	 * @param parent
	 * @return {@link BiomeType}
	 */
	public BiomeType addParent(ResourceLocation parent)
	{
		this.getParents().add(parent);
		return this;
	}

	/**
	 * @return {@link Set}
	 */
	public Set<ResourceLocation> getParents()
	{
		return this.parents;
	}

	/**
	 * Sets the parents of this entry, overriding old ones. sYou probably shouldn't
	 * use this unless you're creating a new instance.
	 * 
	 * @param parents
	 * @return {@link BiomeType}
	 */
	public BiomeType setParents(Set<ResourceLocation> parents)
	{
		this.parents = new HashSet<>(parents);
		return this;
	}

	/**
	 * Gets the biomes from this instance and it's parents by trying to find their
	 * registry keys.
	 * 
	 * @return {@link Set}
	 */
	public Set<RegistryKey<Biome>> getAllBiomes()
	{
		Set<RegistryKey<Biome>> biomes = this.getBiomes().stream().filter(ForgeRegistries.BIOMES::containsKey).map(r -> RegistryKey.getOrCreateKey(ForgeRegistries.Keys.BIOMES, r)).collect(Collectors.toSet());
		for (ResourceLocation parent : this.getParents())
		{
			if (BiomeDictionary.REGISTRY.containsKey(parent))
				biomes.addAll(BiomeDictionary.REGISTRY.getValue(parent).getAllBiomes());
		}

		return biomes;
	}

	/**
	 * Does this instance or it's parents contain the biome passed.
	 * 
	 * @param key
	 * @return {@link Boolean}
	 */
	public boolean contains(RegistryKey<Biome> key)
	{
		return this.contains(key.getLocation());
	}

	/**
	 * Does this instance or it's parents contain the biome passed.
	 * 
	 * @param biome
	 * @return {@link Boolean}
	 */
	public boolean contains(Biome biome)
	{
		return this.contains(biome.getRegistryName());
	}

	/**
	 * Does this instance or it's parents contain the biome passed.
	 * 
	 * @param key
	 * @return {@link Boolean}
	 */
	public boolean contains(ResourceLocation key)
	{
		return this.getAllBiomes().stream().anyMatch(registryKey -> registryKey.getLocation().equals(key));
	}

	/**
	 * Compares the registry name of the {@link BiomeType} passed in to this.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof BiomeType)
			return ((BiomeType) obj).getRegistryName().equals(this.getRegistryName());
		else
			return false;
	}

	@Override
	public String toString()
	{
		String parents = String.join(", ", this.getParents().stream().map(ResourceLocation::toString).sorted().collect(Collectors.toSet()));
		String biomes = String.join(", ", this.getBiomes().stream().map(ResourceLocation::toString).sorted().collect(Collectors.toSet()));
		return String.format("name = %s, parents = [%s], biomes = [%s]", this.getRegistryName().toString(), parents, biomes);
	}

	/**
	 * Gets the registered name of the {@link BiomeType}.
	 * 
	 * @return {@link ResourceLocation}
	 */
	public ResourceLocation getRegistryName()
	{
		return this.registryName;
	}

	@Override
	public BiomeType setRegistryName(ResourceLocation name)
	{
		this.registryName = name;
		return this;
	}

	@Override
	public Class<BiomeType> getRegistryType()
	{
		return BiomeType.class;
	}
}

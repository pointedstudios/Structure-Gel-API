package com.legacy.structure_gel.biome_dictionary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
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
	 * The name that this is registered as.
	 */
	private ResourceLocation registryName = BiomeDictionary.EMPTY_NAME;
	/**
	 * The biomes in this instance.
	 */
	private Set<ResourceLocation> biomes = new HashSet<>();
	/**
	 * When running {@link #getAllBiomes()}, it will return all of the biomes of
	 * this entry and all of the biomes of any parent listed.
	 */
	private Set<ResourceLocation> parents = new HashSet<>();

	/**
	 * Ensures none of the values will be set to null just in case.
	 * 
	 * @param name
	 * @param parents
	 * @param biomes
	 */
	public BiomeType(ResourceLocation name, Set<ResourceLocation> parents, Set<RegistryKey<Biome>> biomes)
	{
		if (name != null)
			this.registryName = name;
		if (parents != null)
			this.parents = new HashSet<>(parents);
		if (biomes != null)
			this.biomes = biomes.stream().map(r -> r.func_240901_a_()).collect(Collectors.toSet());
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
		this.addBiomes(Arrays.asList(biomes).stream().map(r -> r.func_240901_a_()).collect(Collectors.toSet()));
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
		this.getBiomes().add(biome.func_240901_a_());
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
	 * Sets the biomes of this entry manually.
	 * 
	 * @param biomes
	 * @return {@link BiomeType}
	 */
	public BiomeType setBiomes(Set<RegistryKey<Biome>> biomes)
	{
		this.biomes = biomes.stream().map(r -> r.func_240901_a_()).collect(Collectors.toSet());
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
		Set<RegistryKey<Biome>> biomes = this.getBiomes().stream().filter(ForgeRegistries.BIOMES::containsKey).map(r -> RegistryKey.func_240903_a_(ForgeRegistries.Keys.BIOMES, r)).collect(Collectors.toSet());
		for (ResourceLocation parent : this.getParents())
		{
			if (BiomeDictionary.REGISTRY.containsKey(parent))
				biomes.addAll(BiomeDictionary.REGISTRY.getValue(parent).getAllBiomes());
		}

		return biomes;
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
	 * Sets the parents of this entry manually.
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
	 * Does this {@link BiomeType} or it's parents contain the biome passed.
	 * 
	 * @param biome
	 * @return {@link Boolean}
	 */
	public boolean contains(Biome biome)
	{
		if (BiomeDictionary.getBiomeKey(biome) != null)
			return this.getAllBiomes().contains(BiomeDictionary.getBiomeKey(biome));
		return false;
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
		String parents = "";
		Iterator<ResourceLocation> iterator = this.getParents().iterator();
		while (iterator.hasNext())
		{
			parents = parents + iterator.next().toString();
			if (iterator.hasNext())
				parents = parents + ", ";
		}

		String biomes = "";
		Iterator<ResourceLocation> iterator2 = this.getBiomes().iterator();
		while (iterator2.hasNext())
		{
			biomes = biomes + iterator2.next().toString();
			if (iterator2.hasNext())
				biomes = biomes + ", ";
		}

		return String.format("name = %s, parents = [%s], biomes = [%s]", this.getRegistryName().toString(), parents, biomes);
	}

	@Override
	public Class<BiomeType> getRegistryType()
	{
		return BiomeType.class;
	}
}

package com.legacy.structure_gel.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.data.BiomeDictionaryProvider.BiomeType;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class BiomeDictionary
{
	private static final Map<ResourceLocation, BiomeType> types = new HashMap<>();

	public static final BiomeType OCEAN = get("ocean");
	public static final BiomeType PLAINS = get("plains");
	public static final BiomeType SNOWY_PLAINS = get("snowy_plains");
	public static final BiomeType DESERT = get("desert");
	public static final BiomeType SAVANNA = get("savanna");
	public static final BiomeType MOUNTAIN_SAVANNA = get("mountain_savanna");
	public static final BiomeType FLOWERY = get("flowery");
	public static final BiomeType OAK_FOREST = get("oak_forest");
	public static final BiomeType BIRCH_FOREST = get("birch_forest");
	public static final BiomeType SPRUCE_FOREST = get("spruce_forest");
	public static final BiomeType SNOWY_SPRUCE_FOREST = get("snowy_spruce_forest");
	public static final BiomeType LARGE_SPRUCE_FOREST = get("large_spruce_forest");
	public static final BiomeType JUNGLE = get("jungle");
	public static final BiomeType BAMBOO_JUNGLE = get("bamboo_jungle");
	public static final BiomeType DARK_FOREST = get("dark_forest");
	public static final BiomeType MOUNTAIN = get("mountain");
	public static final BiomeType SWAMP = get("swamp");
	public static final BiomeType BADLANDS = get("badlands");
	public static final BiomeType MUSHROOM = get("mushroom");
	public static final BiomeType RIVER = get("river");
	public static final BiomeType BEACH = get("beach");

	// Temperature
	public static final BiomeType FROZEN = get("frozen");
	public static final BiomeType SNOWY = get("snowy");
	public static final BiomeType COLD = get("cold");
	public static final BiomeType NEUTRAL_TEMP = get("neutral_temp");
	public static final BiomeType WARM = get("warm");
	public static final BiomeType HOT = get("hot");
	public static final BiomeType FIERY = get("fiery");

	// Humidity
	public static final BiomeType HUMID = get("humid");
	public static final BiomeType DRY = get("dry");

	// Dimension
	public static final BiomeType NETHER = get("nether");
	public static final BiomeType END = get("end");
	public static final BiomeType OVERWORLD = get("overworld");

	// Special
	public static final BiomeType MAGICAL = get("magical");
	public static final BiomeType OIL = get("oil");
	public static final BiomeType VOID = get("void");
	public static final BiomeType STRANGE = get("strange");

	@Internal
	private static BiomeType get(String key)
	{
		return get(StructureGelMod.locate(key));
	}

	public static BiomeType get(ResourceLocation key)
	{
		Optional<BiomeType> biomeType = StructureGelMod.biomeDictionary.get(key);
		if (biomeType.isPresent())
		{
			types.put(key, biomeType.get());
			return biomeType.get();
		}
		return BiomeType.EMPTY;
	}

	public static Set<BiomeType> getTypes(RegistryKey<Biome> biome)
	{
		Set<BiomeType> types = new HashSet<>();
		StructureGelMod.biomeDictionary.getRegistry().forEach((name, type) ->
		{
			if (type.getBiomes().contains(biome))
				types.add(type);
		});
		return types;
	}

	public static void reload()
	{
		types.forEach((rl, b) ->
		{
			Optional<BiomeType> registered = StructureGelMod.biomeDictionary.get(rl);
			if (registered.isPresent())
			{
				b.setBiomes(registered.get().getBiomes());
				b.setParents(registered.get().getParents());
			}
		});
	}
}

package com.legacy.structure_gel.structure_gel_compat;

import com.legacy.structure_gel.StructureGelCompat;
import com.legacy.structure_gel.util.Internal;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Sample class for adding Structure Gel biome dictionary support to your mod.
 *
 * @author David
 * @see StructureGelCompat
 */
@Internal
public class BiomeDictionaryCompat
{
	/**
	 * Returns a list containing the required data to register a biome dictionary
	 * entry.<br>
	 * <br>
	 * The {@link org.apache.commons.lang3.tuple.Triple} contains a
	 * {@link ResourceLocation} for the registry name, a {@link Set} of
	 * {@link ResourceLocation}s for parenting biome types that this should inherit
	 * biomes from, and a Set of ResourceLocations for what biomes should be in the
	 * dictionary entry. In that order.<br>
	 * <br>
	 * This will append to an existing entry if one already exists.
	 *
	 * @return {@link List}&lt;{@link Triple}&lt;{@link ResourceLocation},
	 * {@link Set}&lt;{@link ResourceLocation}&gt;,
	 * {@link Set}&lt;{@link ResourceLocation}&gt;&gt;&gt;
	 */
	@Nullable
	public static List<Triple<ResourceLocation, Set<ResourceLocation>, Set<ResourceLocation>>> register(String modID)
	{
		// Example code for how to register the structure_gel:oak_forest tag and a
		// desert biome to the structure_gel:plains biome dictionary tag.

		/*
		List<Triple<ResourceLocation, Set<ResourceLocation>, Set<ResourceLocation>>> list = new ArrayList<>();
		
		list.add(Triple.of(new ResourceLocation("structure_gel", "plains"), Sets.newHashSet(new ResourceLocation("structure_gel", "oak_forest")), Sets.newHashSet(Biomes.DESERT.getLocation())));
		
		return list;
		*/
		return null;
	}
}

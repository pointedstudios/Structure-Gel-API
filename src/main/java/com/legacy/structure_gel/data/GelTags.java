package com.legacy.structure_gel.data;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.structures.processors.RemoveGelStructureProcessor;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;

/**
 * Stores all tags used by the mod.
 * 
 * @author David
 *
 */
public class GelTags
{
	/**
	 * Contains all structure gel blocks. This tag is used to determine which blocks
	 * are turned to air in the {@link RemoveGelStructureProcessor}
	 */
	public static final Tag<Block> GEL = tag("gel");

	/**
	 * 
	 * @param key
	 * @return Tag
	 */
	private static Tag<Block> tag(String key)
	{
		return new BlockTags.Wrapper(StructureGelMod.locate(key));
	}
}

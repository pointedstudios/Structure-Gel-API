package com.legacy.structure_gel.data;

import com.legacy.structure_gel.StructureGelMod;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;

public class GelTags
{
	public static final Tag<Block> GEL = tag("gel");

	private static Tag<Block> tag(String key)
	{
		return new BlockTags.Wrapper(StructureGelMod.locate(key));
	}
}

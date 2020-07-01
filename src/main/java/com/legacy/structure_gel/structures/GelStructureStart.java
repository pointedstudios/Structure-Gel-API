package com.legacy.structure_gel.structures;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public abstract class GelStructureStart<C extends IFeatureConfig> extends StructureStart<C>
{
	public GelStructureStart(Structure<C> structureIn, int chunkX, int chunkZ, MutableBoundingBox boundsIn, int referenceIn, long seed)
	{
		super(structureIn, chunkX, chunkZ, boundsIn, referenceIn, seed);
	}

	/**
	 * Places the structure at a y value between the two values entered such that
	 * the top of it's bounding box is below maxY and the bottom of it's bounding
	 * box is above minY.<br>
	 * <br>
	 * If minY is bigger than maxY, or vis versa, it automatically corrects it.
	 * 
	 * @param minY
	 * @param maxY
	 */
	public void setHeight(int minY, int maxY)
	{
		if (maxY < minY)
			maxY = minY + 1;
		if (minY > maxY)
			minY = maxY - 1;

		if (maxY < this.bounds.maxY)
		{
			int offsetAmount = this.bounds.maxY - maxY;
			this.bounds.offset(0, -offsetAmount, 0);
			this.components.forEach(piece -> piece.offset(0, -offsetAmount, 0));
		}

		if (minY > this.bounds.minY)
		{
			int offsetAmount = minY - this.bounds.minY;
			this.bounds.offset(0, offsetAmount, 0);
			this.components.forEach(piece -> piece.offset(0, offsetAmount, 0));
		}
	}

	/**
	 * Places the structure at the y value set.
	 * 
	 * @param minY
	 * @param maxY
	 */
	public void setHeight(int y)
	{
		int offset = y - this.bounds.minY;
		this.bounds.offset(0, offset, 0);
		this.components.forEach(piece -> piece.offset(0, offset, 0));
	}
}

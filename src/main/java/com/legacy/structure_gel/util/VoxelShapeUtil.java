package com.legacy.structure_gel.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

/**
 * Helper methods for dealing with VoxelShapes
 * 
 * @author David
 *
 */
public class VoxelShapeUtil
{
	/**
	 * Rotates the provided {@link VoxelShape} from one direction to the other.
	 * 
	 * @param shape
	 * @param originalDir
	 * @param newDir
	 * @return {@link VoxelShape}
	 */
	public static VoxelShape rotate(VoxelShape shape, Direction originalDir, Direction newDir)
	{
		VoxelShape[] newShape = new VoxelShape[] { shape };

		for (int rotations = 0; rotations < (newDir.getHorizontalIndex() - originalDir.getHorizontalIndex() + 4) % 4; rotations++)
			newShape[0].forEachBox((x, y, z, a, b, c) -> newShape[0] = VoxelShapes.create(1 - c, y, x, 1 - z, b, a));

		return newShape[0];
	}
}

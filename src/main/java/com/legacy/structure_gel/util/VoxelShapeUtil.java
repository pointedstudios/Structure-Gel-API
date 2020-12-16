package com.legacy.structure_gel.util;

import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

/**
 * Helper methods for dealing with VoxelShapes
 *
 * @author David
 */
public class VoxelShapeUtil
{
	/**
	 * Rotates the provider {@link VoxelShape} to the new direction, assuming it
	 * starts by facing north.
	 *
	 * @param shape
	 * @param newDir
	 * @return {@link VoxelShape}
	 */
	public static VoxelShape rotate(VoxelShape shape, Direction newDir)
	{
		return rotate(shape, Direction.NORTH, newDir);
	}

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
		if (originalDir != newDir)
		{
			VoxelShape[] newShape = new VoxelShape[]{VoxelShapes.empty()};
			shape.forEachBox((x, y, z, a, b, c) -> newShape[0] = VoxelShapes.or(newShape[0], VoxelShapes.create(1 - c, y, x, 1 - z, b, a)));
			return rotate(newShape[0], originalDir.rotateY(), newDir);
		}
		return shape;
	}

	/**
	 * Mirrors the provided {@link VoxelShape} based on the axis of facingProperty.
	 *
	 * @param shape
	 * @param facingProperty
	 * @return {@link VoxelShape}
	 */
	public static VoxelShape mirror(VoxelShape shape, Direction facingProperty)
	{
		return mirror(shape, facingProperty.getAxis());
	}

	/**
	 * Mirrors the provided {@link VoxelShape}. {@link Mirror#FRONT_BACK} for x
	 * axis. {@link Mirror#LEFT_RIGHT} for z axis.
	 *
	 * @param shape
	 * @param mirror
	 * @return {@link VoxelShape}
	 */
	public static VoxelShape mirror(VoxelShape shape, Mirror mirror)
	{
		return mirror(shape, mirror == Mirror.FRONT_BACK ? Axis.X : Axis.Z);
	}

	/**
	 * Mirrors the provided {@link VoxelShape} along the input axis.
	 *
	 * @param shape
	 * @param axis
	 * @return {@link VoxelShape}
	 */
	public static VoxelShape mirror(VoxelShape shape, Axis axis)
	{
		VoxelShape[] newShape = new VoxelShape[]{VoxelShapes.empty()};
		switch (axis)
		{
			case X:
				shape.forEachBox((x, y, z, a, b, c) -> newShape[0] = VoxelShapes.or(newShape[0], VoxelShapes.create(1 - x, y, z, 1 - a, b, c)));
				break;
			case Z:
				shape.forEachBox((x, y, z, a, b, c) -> newShape[0] = VoxelShapes.or(newShape[0], VoxelShapes.create(x, y, 1 - z, a, b, 1 - c)));
				break;
			case Y:
				shape.forEachBox((x, y, z, a, b, c) -> newShape[0] = VoxelShapes.or(newShape[0], VoxelShapes.create(x, 1 - y, z, a, 1 - b, c)));
				break;
		}
		return newShape[0];
	}
}

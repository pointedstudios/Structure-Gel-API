package com.legacy.structure_gel.asm;

import com.legacy.structure_gel.access_helpers.StructureAccessHelper;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.Template;

public class StructureGelHooks
{
	/**
	 * Hooks into
	 * {@link LakesFeature#place(IWorld, net.minecraft.world.gen.ChunkGenerator, java.util.Random, net.minecraft.util.math.BlockPos, net.minecraft.world.gen.feature.BlockStateFeatureConfig)}
	 * to allow for more structures than just villages to prevent lakes from
	 * generating.
	 * 
	 * @see StructureAccessHelper#addLakeProofStructure(Structure)
	 * @param worldIn
	 * @param chunkPos
	 * @return
	 */
	public static boolean lakeCheckForStructures(IWorld worldIn, ChunkPos chunkPos)
	{
		for (Structure<?> structure : StructureAccessHelper.LAKE_STRUCTURES)
			if (!worldIn.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(structure.getStructureName()).isEmpty())
				return false;

		return true;
	}

	/**
	 * Hooks into
	 * {@link Template#processEntityInfos(Template, IWorld, net.minecraft.util.math.BlockPos, net.minecraft.world.gen.feature.template.PlacementSettings, java.util.List)}
	 * Fixes the rotation of entities when loaded from a structure.
	 * 
	 * @param worldIn
	 * @param nbt
	 * @param entityPos
	 * @param mirrorIn
	 * @param rotationIn
	 */
	public static void templateEntityRotationFix(IWorld worldIn, CompoundNBT nbt, Vec3d entityPos, Mirror mirrorIn, Rotation rotationIn)
	{
		Template.loadEntity(worldIn, nbt).ifPresent((entity) ->
		{
			// entity.getMirroredYaw(Mirror) has it's mirroring flipped, so I flip it here
			// to correct that.
			// float yaw = entity.getMirroredYaw(mirrorIn);
			Mirror oppositeMirror = mirrorIn == Mirror.FRONT_BACK ? Mirror.LEFT_RIGHT : (mirrorIn == Mirror.LEFT_RIGHT ? Mirror.FRONT_BACK : Mirror.NONE);
			float yaw = entity.getMirroredYaw(oppositeMirror);

			// The rotation part of this rotates in the wrong direction, so I correct that
			// here.
			// yaw = yaw + (entity.rotationYaw - entity.getRotatedYaw(rotationIn));
			float rotation = 0;
			switch (rotationIn)
			{
			case CLOCKWISE_90:
				rotation = 90;
				break;
			case CLOCKWISE_180:
				rotation = 180;
				break;
			case COUNTERCLOCKWISE_90:
				rotation = -90;
				break;
			default:
				rotation = 0;
				break;
			}
			yaw = yaw + rotation;
			entity.setLocationAndAngles(entityPos.x, entityPos.y, entityPos.z, yaw, entity.rotationPitch);
			worldIn.addEntity(entity);
		});
	}
}

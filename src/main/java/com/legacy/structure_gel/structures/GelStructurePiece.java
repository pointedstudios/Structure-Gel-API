package com.legacy.structure_gel.structures;

import java.util.Random;

import com.legacy.structure_gel.structures.jigsaw.GelJigsawPiece;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class GelStructurePiece extends AbstractVillagePiece
{
	public GelStructurePiece(IStructurePieceType structurePieceType, TemplateManager templateManager, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox bounds)
	{
		super(structurePieceType, templateManager, jigsawPiece, pos, groundLevelDelta, rotation, bounds);
	}

	public GelStructurePiece(TemplateManager templateManager, CompoundNBT nbt, IStructurePieceType structurePieceType)
	{
		super(templateManager, nbt, structurePieceType);
	}

	/**
	 * Modification of addComponentParts to allow for data structure block handling.
	 */
	public boolean addComponentParts(IWorld world, Random rand, MutableBoundingBox bounds, ChunkPos chunkPos)
	{
		if (this.jigsawPiece instanceof GelJigsawPiece)
			return ((GelJigsawPiece) this.jigsawPiece).place(this.templateManager, world, this.pos, this.rotation, bounds, rand, this);
		return this.jigsawPiece.place(this.templateManager, world, this.pos, this.rotation, bounds, rand);
	}

	/**
	 * Runs on every data structure block the same as you would in
	 * {@link TemplateStructurePiece}
	 * 
	 * @param key
	 * @param world
	 * @param pos
	 * @param bounds
	 */
	public abstract void handleDataMarker(String key, IWorld world, BlockPos pos, Random rand, MutableBoundingBox bounds);

	/**
	 * world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3)
	 * 
	 * @param world
	 * @param pos
	 */
	public void setAir(IWorld world, BlockPos pos)
	{
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
	}

	/**
	 * Shorthand method to create an entity with the given pos (offset by 0.5) and
	 * rotation. Rotation is south by default, with the structure's rotation taken
	 * into account.
	 * 
	 * @param entityType
	 * @param worldIn
	 * @param pos
	 * @param rotation
	 * @return Entity
	 */
	public <T extends Entity> T createEntity(EntityType<T> entityType, IWorld worldIn, BlockPos pos, Rotation rotation)
	{
		T entity = entityType.create(worldIn.getWorld());
		entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, rotation.rotate(Direction.SOUTH).getHorizontalAngle(), 0);
		return entity;
	}

	/**
	 * Gives you the name of this structure piece. Useful for cases where you want
	 * extra things to happen when this piece generates.
	 * 
	 * @return ResourceLocation
	 */
	public ResourceLocation getLocation()
	{
		if (this.jigsawPiece instanceof GelJigsawPiece)
			return ((GelJigsawPiece) this.jigsawPiece).getLocation();
		return new ResourceLocation("empty");
	}
}

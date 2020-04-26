package com.legacy.structure_gel.structures;

import java.util.Random;
import java.util.function.Function;

import com.legacy.structure_gel.structures.jigsaw.GelJigsawPiece;
import com.legacy.structure_gel.structures.jigsaw.JigsawAccessHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
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
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * An extension of {@link AbstractVillagePiece} for jigsaw structures that
 * allows for data structure block interaction and contains more functional
 * methods to go along with that. Use this as your piece type if you're doing
 * anything with jigsaw structures, as you'll have more options.
 * 
 * @author David
 *
 */
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
	 * addComponentParts<br>
	 * <br>
	 * Modification of addComponentParts to allow for data structure block handling.
	 */
	@Override
	public boolean func_225577_a_(IWorld world, ChunkGenerator<?> chunkGen, Random rand, MutableBoundingBox bounds, ChunkPos chunkPos)
	{
		if (this.jigsawPiece instanceof GelJigsawPiece)
			return ((GelJigsawPiece) this.jigsawPiece).place(this.templateManager, world, chunkGen, this.pos, this.rotation, bounds, rand, this);
		return this.jigsawPiece.func_225575_a_(this.templateManager, world, chunkGen, this.pos, this.rotation, bounds, rand);
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
	 * Shorthand method to create an entity with the given pos (offset by 0.5) and
	 * rotation. Rotation is south by default, with the structure's rotation taken
	 * into account.
	 * 
	 * @param entityType
	 * @param worldIn
	 * @param pos
	 * @param rotation
	 * @return {@link Entity}
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
	 * @return {@link ResourceLocation}
	 */
	public ResourceLocation getLocation()
	{
		if (this.jigsawPiece instanceof SingleJigsawPiece)
			return JigsawAccessHelper.getSingleJigsawPieceLocation((SingleJigsawPiece) this.jigsawPiece);
		return new ResourceLocation("empty");
	}

	/**
	 * Returns the template manager so you can get data about the structure itself.
	 * 
	 * @return {@link TemplateManager}
	 */
	public TemplateManager getTemplateManager()
	{
		return this.templateManager;
	}

	/**
	 * Gets the size of the structure.
	 * 
	 * @return {@link BlockPos}
	 */
	public BlockPos getSize()
	{
		return this.getTemplateManager().getTemplate(this.getLocation()).getSize();
	}

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
	 * Fills the air and liquid below the lowest part of this structure with
	 * stateIn.
	 * 
	 * @param worldIn
	 * @param stateIn
	 * @param bounds
	 * @param rotation
	 * @param random
	 */
	public void extendDown(IWorld worldIn, BlockState stateIn, MutableBoundingBox bounds, Rotation rotation, Random random)
	{
		this.extendDown(worldIn, (rand) -> stateIn, bounds, rotation, random);
	}

	/**
	 * Fills the air and liquid below the lowest part of this structure with the
	 * BlockState produced from randStateIn.
	 * 
	 * @param worldIn
	 * @param randomBlockState
	 * @param bounds
	 * @param rotation
	 * @param rand
	 */
	public void extendDown(IWorld worldIn, Function<Random, BlockState> randStateIn, MutableBoundingBox bounds, Rotation rotation, Random random)
	{
		int offsetX = rotation == Rotation.CLOCKWISE_180 || this.rotation == Rotation.CLOCKWISE_90 ? -(getSize().getX() - 1) : 0;
		int offsetZ = rotation == Rotation.CLOCKWISE_180 || this.rotation == Rotation.COUNTERCLOCKWISE_90 ? -(getSize().getZ() - 1) : 0;

		for (int x = 0; x < 13; x++)
		{
			for (int z = 0; z < 13; z++)
			{
				if (worldIn.getBlockState(pos.add(x + offsetX, 0, z + offsetZ)).getMaterial() != Material.AIR)
				{
					int offsetY = -1;
					while ((worldIn.isAirBlock(pos.add(x + offsetX, offsetY, z + offsetZ)) || worldIn.getBlockState(pos.add(x + offsetX, offsetY, z + offsetZ)).getMaterial().isLiquid()) && pos.getY() + offsetY > 0)
					{
						worldIn.setBlockState(pos.add(x + offsetX, offsetY, z + offsetZ), randStateIn.apply(random), 2);
						--offsetY;
					}
				}
			}
		}
	}
}

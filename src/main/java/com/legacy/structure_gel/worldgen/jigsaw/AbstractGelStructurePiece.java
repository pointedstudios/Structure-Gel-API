package com.legacy.structure_gel.worldgen.jigsaw;

import java.util.Random;
import java.util.function.Function;

import com.legacy.structure_gel.access_helpers.JigsawAccessHelper;

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
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
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
public abstract class AbstractGelStructurePiece extends AbstractVillagePiece
{
	public AbstractGelStructurePiece(TemplateManager templateManager, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox bounds)
	{
		super(templateManager, jigsawPiece, pos, groundLevelDelta, rotation, bounds);
		this.setStructurePieceType(this.getStructurePieceType());
	}

	public AbstractGelStructurePiece(TemplateManager templateManager, CompoundNBT nbt)
	{
		super(templateManager, nbt);
		this.setStructurePieceType(this.getStructurePieceType());
	}

	/**
	 * Runs on every data structure block the same as you would in
	 * {@link TemplateStructurePiece}
	 * 
	 * @param key
	 * @param pos
	 * @param world
	 * @param bounds
	 */
	public abstract void handleDataMarker(String key, BlockPos pos, IServerWorld world, Random rand, MutableBoundingBox bounds);

	/**
	 * Returns the {@link IStructurePieceType} for this piece.
	 * 
	 * @return IStructurePieceType
	 */
	public abstract IStructurePieceType getStructurePieceType();

	/**
	 * Sets the {@link IStructurePieceType} for this piece.
	 * 
	 * @param structurePieceType
	 */
	public void setStructurePieceType(IStructurePieceType structurePieceType)
	{
		this.structurePieceType = structurePieceType;
	}

	/**
	 * addComponentParts<br>
	 * <br>
	 * Modification of addComponentParts to allow for data structure block handling.
	 */
	@Override
	public boolean func_237001_a_(ISeedReader seedReader, StructureManager structureManager, ChunkGenerator chunkGen, Random rand, MutableBoundingBox bounds, BlockPos pos, boolean isLegacy)
	{
		if (this.jigsawPiece instanceof GelJigsawPiece)
			return ((GelJigsawPiece) this.jigsawPiece).place(this.templateManager, seedReader, structureManager, chunkGen, this.pos, pos, this.rotation, bounds, rand, isLegacy, this);
		return this.jigsawPiece.func_230378_a_(this.templateManager, seedReader, structureManager, chunkGen, this.pos, pos, this.rotation, bounds, rand, isLegacy);
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
	 * @return {@link Entity}
	 */
	public <T extends Entity> T createEntity(EntityType<T> entityType, IServerWorld worldIn, BlockPos pos, Rotation rotation)
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
	 * @param randStateIn
	 * @param bounds
	 * @param rotation
	 * @param random
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

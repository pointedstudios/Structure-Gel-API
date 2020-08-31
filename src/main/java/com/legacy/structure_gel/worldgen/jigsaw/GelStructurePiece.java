package com.legacy.structure_gel.worldgen.jigsaw;

import java.util.Random;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * The default implementation of {@link AbstractGelStructurePiece}.
 * 
 * @author David
 */
@Internal
public final class GelStructurePiece extends AbstractGelStructurePiece
{
	public GelStructurePiece(TemplateManager templateManager, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox bounds)
	{
		super(templateManager, jigsawPiece, pos, groundLevelDelta, rotation, bounds);
	}

	public GelStructurePiece(TemplateManager templateManager, CompoundNBT nbt)
	{
		super(templateManager, nbt);
	}

	@Override
	public IStructurePieceType getStructurePieceType()
	{
		return StructureGelMod.StructurePieceTypes.GEL_JIGSAW;
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
	@Override
	public void handleDataMarker(String key, BlockPos pos, IServerWorld world, Random rand, MutableBoundingBox bounds)
	{

	}
}

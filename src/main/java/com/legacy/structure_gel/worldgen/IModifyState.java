package com.legacy.structure_gel.worldgen;

import java.util.Random;

import javax.annotation.Nullable;

import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.worldgen.jigsaw.AbstractGelStructurePiece;
import com.legacy.structure_gel.worldgen.structure.GelTemplateStructurePiece;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;

/**
 * Used to modify blocks placed by structures.
 * 
 * @see AbstractGelStructurePiece
 * @see GelTemplateStructurePiece
 * @author David
 *
 */
@Internal
public interface IModifyState
{
	/**
	 * Modifies the state passed in based on the structure's rules. This method is
	 * called after processors are applied.<br>
	 * <br>
	 * Return null to prevent placement.
	 * 
	 * @param world
	 * @param rand
	 * @param pos
	 * @param originalState
	 * @return {@link BlockState}
	 */
	@Nullable
	BlockState modifyState(IServerWorld world, Random rand, BlockPos pos, BlockState originalState);
}

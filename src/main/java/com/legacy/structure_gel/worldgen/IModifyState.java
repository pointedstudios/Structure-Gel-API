package com.legacy.structure_gel.worldgen;

import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.worldgen.jigsaw.AbstractGelStructurePiece;
import com.legacy.structure_gel.worldgen.structure.GelTemplateStructurePiece;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Used to modify blocks placed by structures.
 *
 * @author David
 * @see AbstractGelStructurePiece
 * @see GelTemplateStructurePiece
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

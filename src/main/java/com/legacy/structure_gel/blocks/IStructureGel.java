package com.legacy.structure_gel.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.legacy.structure_gel.StructureGelMod;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Includes a few methods to hook into {@link StructureGelBlock}'s behavior
 * 
 * @author David
 *
 */
public interface IStructureGel
{
	/**
	 * Determines how the structure gel should act.
	 * 
	 * @author David
	 *
	 */
	public static enum Behavior
	{
		/**
		 * Spreads the gel along the cardinal directions, only replacing
		 * {@link Blocks#AIR}. All gels use this basic behavior, using the others to
		 * modify it.
		 */
		DEFAULT(),
		/**
		 * Only spreads the gel when there isn't max skylight or there are blocks
		 * blocking the sky. Great for rooms with open walls.
		 */
		PHOTOSENSITIVE(),
		/**
		 * Spreads the gel diagonally to allow it to go through corners created by vines
		 * and other details in a build that other gel cant get around. Be careful as
		 * this may go in unintended places.
		 */
		DIAGONAL_SPREAD(),
		/**
		 * Spreads the gel with a set distance based on how many you're holding. Maxes
		 * out at 50.
		 */
		DYNAMIC_SPREAD_DIST();

		public final String translation;

		Behavior()
		{
			this.translation = String.format("info.%s.%s", StructureGelMod.MODID, this.name().toLowerCase());
		}
		
		Behavior(String translation)
		{
			this.translation = translation;
		}
	}

	/**
	 * Called before the gel spreads. Return false to cancel the default spread
	 * mechanic and do your own.
	 * 
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param random
	 * @return boolean
	 */
	default boolean spreadHookPre(BlockState state, World worldIn, BlockPos pos, Random random)
	{
		return true;
	};

	/**
	 * Called before the gel begins to remove itself. Return false to cancel the
	 * default removal mechanic and do your own.
	 * 
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param random
	 * @return boolean
	 */
	default boolean removalHookPre(BlockState state, World worldIn, BlockPos pos, Random random)
	{
		return true;
	};

	/**
	 * Called after the gel finishes spreading to append additional actions
	 * 
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param random
	 */
	default void spreadHookPost(BlockState state, World worldIn, BlockPos pos, Random random)
	{
	};

	/**
	 * Called after the gel finishes it's steps for chain removal
	 * 
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param random
	 */
	default void removalHookPost(BlockState state, World worldIn, BlockPos pos, Random random)
	{
	};

	/**
	 * Called when the gel checks if it can spread to a given location. Return false
	 * to prevent the gel from spreading to spreadPos.
	 * 
	 * @param worldIn
	 * @param spreadPos : where the gel is trying to spread
	 * @param count : the {@link StructureGelBlock#COUNT} property to place as
	 * @return boolean
	 */
	default boolean checkPlacementHook(World worldIn, BlockPos spreadPos, int count)
	{
		return true;
	}

	/**
	 * Called when placed by hand to get what state this should place as. Return
	 * null for default placement behavior.
	 * 
	 * @param context
	 * @return {@link BlockState}
	 */
	@Nullable
	default BlockState onHandPlaceHook(BlockItemUseContext context)
	{
		return null;
	}
}

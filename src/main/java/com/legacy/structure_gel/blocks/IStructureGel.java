package com.legacy.structure_gel.blocks;

import com.legacy.structure_gel.StructureGelMod;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

/**
 * Includes a few methods to hook into {@link StructureGelBlock}'s behavior
 *
 * @author David
 */
public interface IStructureGel
{
	/**
	 * Extend off of this to make your own behaviors.
	 *
	 * @author David
	 */
	interface IBehavior
	{
		String getTranslation();
	}

	/**
	 * Determines how the structure gel should act and provides translations for the
	 * item.
	 *
	 * @author David
	 */
	enum Behavior implements IBehavior
	{
		/**
		 * Spreads the gel along the cardinal directions, only replacing air. All gels
		 * use this basic behavior, using the others to modify it.
		 */
		DEFAULT(),
		/**
		 * Only spreads the gel in positions not exposed to the sky. Great for rooms
		 * with open walls.
		 */
		PHOTOSENSITIVE(),
		/**
		 * Spreads itself along the cardinal and diagonal directions. Useful to get
		 * around vines and other weird corners.
		 */
		DIAGONAL_SPREAD(),
		/**
		 * Spreads itself along the plane that it was placed facing. Must extend
		 * {@link AxisStructureGelBlock}.
		 */
		AXIS_SPREAD(),
		/**
		 * Spreads the gel with a set distance based on how many you're holding. Maxes
		 * out at 50.
		 */
		DYNAMIC_SPREAD_DIST();

		/**
		 * Translation string for the item to display in it's lore field.
		 */
		private final String translation;

		/**
		 * Automatically generates a translation as "info.structure_gel.(name)"
		 *
		 * @see Behavior
		 */
		Behavior()
		{
			this.translation = String.format("info.%s.%s", StructureGelMod.MODID, this.name().toLowerCase(Locale.ENGLISH));
		}

		@Override
		public String getTranslation()
		{
			return this.translation;
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
	 * @return {@link Boolean}
	 */
	default boolean spreadHookPre(BlockState state, World worldIn, BlockPos pos, Random random)
	{
		return true;
	}

	/**
	 * Called before the gel begins to remove itself. Return false to cancel the
	 * default removal mechanic and do your own.
	 *
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param random
	 * @return {@link Boolean}
	 */
	default boolean removalHookPre(BlockState state, World worldIn, BlockPos pos, Random random)
	{
		return true;
	}

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
	}

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
	}

	/**
	 * Called when the gel checks if it can spread to a given location. Return false
	 * to prevent the gel from spreading to spreadPos.
	 *
	 * @param worldIn
	 * @param spreadPos : where the gel is trying to spread
	 * @param count     : the {@link StructureGelBlock#COUNT} property to place as
	 * @return {@link Boolean}
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

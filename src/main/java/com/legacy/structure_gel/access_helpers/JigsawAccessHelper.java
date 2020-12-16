package com.legacy.structure_gel.access_helpers;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.util.GelCollectors;
import com.legacy.structure_gel.worldgen.jigsaw.JigsawPoolBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.jigsaw.*;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Contains methods to access normally privated values in the jigsaw piece or
 * registry. Also allows for you to add to/remove from existing JigsawPatterns,
 * allowing you to add new houses to villages, change pillager outpost designs,
 * etc.
 *
 * @author David
 */
public class JigsawAccessHelper
{
	/**
	 * The {@link Structure#field_236384_t_} list allows for villages and pillager
	 * outposts to generate terrain underneath them so they don't float. The area
	 * that gets generated is based on the structure that starts the generation. In
	 * villages, this would be the town center.
	 *
	 * @param structures
	 */
	public static void addIllagerStructures(Structure<?>... structures)
	{
		Structure.field_236384_t_ = GelCollectors.addToList(Structure.field_236384_t_, Arrays.asList(structures));
	}

	/**
	 * Clears the input registered jigsaw pool.
	 *
	 * @param pool
	 */
	public static void clearPool(ResourceLocation pool)
	{
		if (getJigsawPattern(pool).isPresent())
			getJigsawPattern(pool).get().jigsawPieces.clear();
		else
			StructureGelMod.LOGGER.warn(String.format("Could not clear the pool %s as it does not exist.", pool));
	}

	/**
	 * Adds the input jigsaw pieces to the pool passed in. Make sure the pool you're
	 * adding to has been initialized.<br>
	 * <br>
	 * Note: Make sure the structure's jigsaw blocks have the proper connections.
	 *
	 * @param pool
	 * @param pieces
	 * @see JigsawPoolBuilder
	 */
	public static void addToPool(ResourceLocation pool, List<Pair<JigsawPiece, Integer>> pieces)
	{
		if (getJigsawPattern(pool).isPresent())
		{
			getJigsawPattern(pool).ifPresent(jigsawPattern ->
			{
				for (Pair<JigsawPiece, Integer> pair : pieces)
					for (int i = 0; i < pair.getSecond(); i++)
						jigsawPattern.jigsawPieces.add(pair.getFirst());
			});
		}
		else
		{
			StructureGelMod.LOGGER.warn(String.format("Could not add to %s because it has not been created yet.", pool));
			return;
		}
	}

	/**
	 * Removes the input jigsaw piece from the pool passed in. Make sure the pool
	 * you're removing from has been initialized.
	 *
	 * @param pool
	 * @param pieceName
	 */
	public static void removeFromPool(ResourceLocation pool, ResourceLocation pieceName)
	{
		if (getJigsawPattern(pool).isPresent())
		{
			getJigsawPattern(pool).get().jigsawPieces.removeIf(piece ->
			{
				if (piece instanceof SingleJigsawPiece)
				{
					return getSingleJigsawPieceLocation((SingleJigsawPiece) piece).equals(pieceName);
				}
				else if (piece instanceof ListJigsawPiece)
				{
					return JigsawAccessHelper.removeFromListJigsaw((ListJigsawPiece) piece, pieceName);
				}
				else
					return false;
			});
		}
		else
		{
			StructureGelMod.LOGGER.warn(String.format("Could not remove from %s because it has not been created yet.", pool));
		}
	}

	/**
	 * Removes the input feature from the pool passed in. Make sure the pool you're
	 * removing from has been initialized.
	 *
	 * @param pool
	 * @param feature
	 */
	public static void removeFromPool(ResourceLocation pool, Feature<?> feature)
	{
		if (getJigsawPattern(pool).isPresent())
		{
			getJigsawPattern(pool).get().jigsawPieces.removeIf(piece ->
			{
				if (piece instanceof FeatureJigsawPiece)
				{
					return ((FeatureJigsawPiece) piece).configuredFeature.get().feature == feature;
				}
				else if (piece instanceof ListJigsawPiece)
				{
					return JigsawAccessHelper.removeFromListJigsaw((ListJigsawPiece) piece, feature);
				}
				else
					return false;
			});
		}
		else
		{
			StructureGelMod.LOGGER.warn(String.format("Could not remove from %s because it has not been created yet.", pool));
		}
	}

	/**
	 * Iterates through the ListJigsawPiece and removes all instances of pieceName
	 * from it. Mainly for internal use.
	 *
	 * @param listJigsawPiece
	 * @param pieceName
	 * @return {@link Boolean}
	 * @see JigsawAccessHelper#removeFromPool(ResourceLocation, ResourceLocation)
	 */
	public static boolean removeFromListJigsaw(ListJigsawPiece listJigsawPiece, ResourceLocation pieceName)
	{
		listJigsawPiece.elements.removeIf(piece ->
		{
			if (piece instanceof SingleJigsawPiece)
				return getSingleJigsawPieceLocation((SingleJigsawPiece) piece).equals(pieceName);
			else if (piece instanceof ListJigsawPiece)
			{
				JigsawAccessHelper.removeFromListJigsaw((ListJigsawPiece) piece, pieceName);
				return ((ListJigsawPiece) piece).elements.isEmpty();
			}
			return false;
		});

		return listJigsawPiece.elements.isEmpty();
	}

	/**
	 * Iterates through the ListJigsawPiece and removes all instances of pieceName
	 * from it. Mainly for internal use.
	 *
	 * @param listJigsawPiece
	 * @param feature
	 * @return {@link Boolean}
	 * @see JigsawAccessHelper#removeFromPool(ResourceLocation, Feature)
	 */
	public static boolean removeFromListJigsaw(ListJigsawPiece listJigsawPiece, Feature<?> feature)
	{
		listJigsawPiece.elements.removeIf(piece ->
		{
			if (piece instanceof FeatureJigsawPiece)
				return ((FeatureJigsawPiece) piece).configuredFeature.get().feature == feature;
			else if (piece instanceof ListJigsawPiece)
			{
				JigsawAccessHelper.removeFromListJigsaw((ListJigsawPiece) piece, feature);
				return ((ListJigsawPiece) piece).elements.isEmpty();
			}
			return false;
		});

		return listJigsawPiece.elements.isEmpty();
	}

	/**
	 * Gives the name of this piece.
	 *
	 * @param piece
	 * @return {@link ResourceLocation}
	 */
	public static ResourceLocation getSingleJigsawPieceLocation(SingleJigsawPiece piece)
	{
		return piece.field_236839_c_.left().get();
	}

	/**
	 * Returns the pieces stored in this piece.
	 *
	 * @param piece
	 * @return {@link List}
	 */
	public static List<JigsawPiece> getListJigsawPiecePieces(ListJigsawPiece piece)
	{
		return piece.elements;
	}

	/**
	 * Returns the feature stored in this piece.
	 *
	 * @param piece
	 * @return {@link ConfiguredFeature}
	 */
	public static ConfiguredFeature<?, ?> getFeatureJigsawPieceFeatures(FeatureJigsawPiece piece)
	{
		return piece.configuredFeature.get();
	}

	/**
	 * Accessor for the jigsaw pattern registry. Mainly for mapped convenience.
	 *
	 * @param location
	 * @return {@link Optional}
	 */
	public static Optional<JigsawPattern> getJigsawPattern(ResourceLocation location)
	{
		return WorldGenRegistries.JIGSAW_POOL.getOptional(location);
	}
}

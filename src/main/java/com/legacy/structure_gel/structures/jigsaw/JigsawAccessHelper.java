package com.legacy.structure_gel.structures.jigsaw;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.legacy.structure_gel.StructureGelMod;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.jigsaw.FeatureJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.ListJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.Structure;

/**
 * Contains methods to access normally privated values in the jigsaw piece or
 * registry. Also allows for you to add to/remove from existing JigsawPatterns,
 * allowing you to add new houses to villages, change pillager outpost designs,
 * etc.
 * 
 * @author David
 *
 */
public class JigsawAccessHelper
{
	/**
	 * The {@link Feature#ILLAGER_STRUCTURES} list allows for villages and pillager
	 * outposts to generate terrain underneath them so they don't float. The area
	 * that gets generated is based on the structure that starts the generation. In
	 * villages, this would be the town center.
	 * 
	 * @param structures
	 */
	public static void addIllagerStructures(Structure<?>... structures)
	{
		Feature.ILLAGER_STRUCTURES = Streams.concat(Feature.ILLAGER_STRUCTURES.stream(), Arrays.asList(structures).stream()).collect(ImmutableList.toImmutableList());
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
		if (JigsawManager.REGISTRY.get(pool) == JigsawPattern.INVALID)
		{
			StructureGelMod.LOGGER.warn(String.format("Could not add to %s because it has not been created yet.", pool));
			return;
		}
		for (Pair<JigsawPiece, Integer> pair : pieces)
		{
			for (Integer integer = 0; integer < pair.getSecond(); integer = integer + 1)
			{
				JigsawManager.REGISTRY.get(pool).jigsawPieces.add(pair.getFirst());
			}
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
		if (JigsawManager.REGISTRY.get(pool) == JigsawPattern.INVALID)
		{
			StructureGelMod.LOGGER.warn(String.format("Could not remove from %s because it has not been created yet.", pool));
			return;
		}
		JigsawManager.REGISTRY.get(pool).jigsawPieces.removeIf(piece ->
		{
			if (piece instanceof SingleJigsawPiece)
			{
				return ((SingleJigsawPiece) piece).location.equals(pieceName);
			}
			else if (piece instanceof ListJigsawPiece)
			{
				return JigsawAccessHelper.removeFromListJigsaw((ListJigsawPiece) piece, pieceName);
			}
			else
				return false;
		});
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
		if (JigsawManager.REGISTRY.get(pool) == JigsawPattern.INVALID)
		{
			StructureGelMod.LOGGER.warn(String.format("Could not remove from %s because it has not been created yet.", pool));
			return;
		}
		JigsawManager.REGISTRY.get(pool).jigsawPieces.removeIf(piece ->
		{
			if (piece instanceof FeatureJigsawPiece)
			{
				return ((FeatureJigsawPiece) piece).configuredFeature.feature == feature;
			}
			else if (piece instanceof ListJigsawPiece)
			{
				return JigsawAccessHelper.removeFromListJigsaw((ListJigsawPiece) piece, feature);
			}
			else
				return false;
		});
	}

	/**
	 * Iterates through the ListJigsawPiece and removes all instances of pieceName
	 * from it. Mainly for internal use.
	 * 
	 * @param listJigsawPiece
	 * @param pieceName
	 * @return boolean
	 * @see JigsawAccessHelper#removeFromPool(ResourceLocation, ResourceLocation)
	 */
	public static boolean removeFromListJigsaw(ListJigsawPiece listJigsawPiece, ResourceLocation pieceName)
	{
		listJigsawPiece.elements.removeIf(piece ->
		{
			if (piece instanceof SingleJigsawPiece)
				return ((SingleJigsawPiece) piece).location.equals(pieceName);
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
	 * @return boolean
	 * @see JigsawAccessHelper#removeFromPool(ResourceLocation, Feature)
	 */
	public static boolean removeFromListJigsaw(ListJigsawPiece listJigsawPiece, Feature<?> feature)
	{
		listJigsawPiece.elements.removeIf(piece ->
		{
			if (piece instanceof FeatureJigsawPiece)
				return ((FeatureJigsawPiece) piece).configuredFeature.feature == feature;
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
	 * Gives access to the registry that stores all jigsaw patterns.
	 * 
	 * @return {@link Map}
	 */
	public static Map<ResourceLocation, JigsawPattern> getJigsawPatternRegistry()
	{
		return JigsawManager.REGISTRY.registry;
	}

	/**
	 * Gives the name of this piece.
	 * 
	 * @param piece
	 * @return {@link ResourceLocation}
	 */
	public static ResourceLocation getSingleJigsawPieceLocation(SingleJigsawPiece piece)
	{
		return piece.location;
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
	public static ConfiguredFeature<?> getFeatureJigsawPieceFeatures(FeatureJigsawPiece piece)
	{
		return piece.configuredFeature;
	}
}

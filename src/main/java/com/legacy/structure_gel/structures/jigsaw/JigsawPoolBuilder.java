package com.legacy.structure_gel.structures.jigsaw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.StructureProcessor;

/**
 * Assists in the creation of jigsaw pools, allowing multiple pools to be
 * created using the same settings.
 * 
 * @author David
 *
 */
public class JigsawPoolBuilder
{
	private final JigsawRegistryHelper jigsawRegistryHelper;
	private Map<ResourceLocation, Integer> names = ImmutableMap.of();

	private List<StructureProcessor> processors = ImmutableList.of();
	private boolean maintainWater = true;
	private JigsawPattern.PlacementBehaviour placementBehavior = JigsawPattern.PlacementBehaviour.RIGID;

	/**
	 * @see JigsawPoolBuilder
	 * @see JigsawRegistryHelper#builder()
	 * @param jigsawRegistryHelper
	 */
	public JigsawPoolBuilder(JigsawRegistryHelper jigsawRegistryHelper)
	{
		this.jigsawRegistryHelper = jigsawRegistryHelper;
	}

	/**
	 * Set a list of names that the builder uses as structure ResourceLocations with
	 * the weights in the map.
	 * 
	 * @param nameMap : Names are converted to {@link ResourceLocation} using
	 *            {@link JigsawRegistryHelper#locatePiece(String)} from the
	 *            {@link #jigsawRegistryHelper}. Piece weights are set in the map.
	 * @return JigsawPoolBuilder
	 */
	public JigsawPoolBuilder names(Map<String, Integer> nameMap)
	{
		Map<ResourceLocation, Integer> tempNames = new HashMap<>();
		nameMap.forEach((s, i) ->
		{
			tempNames.put(jigsawRegistryHelper.locatePiece(s), i);
		});
		this.names = tempNames;
		return this;
	}

	/**
	 * Set a list of names that the builder uses as structure ResourceLocations with
	 * equal weights.
	 * 
	 * @param names : Names are converted to {@link ResourceLocation} using
	 *            {@link JigsawRegistryHelper#locatePiece(String)} from the
	 *            {@link #jigsawRegistryHelper}. All pieces have equal weight.
	 * @return JigsawPoolBuilder
	 */
	public JigsawPoolBuilder names(String... names)
	{
		Map<ResourceLocation, Integer> tempNames = new HashMap<>();
		for (String s : names)
			tempNames.put(jigsawRegistryHelper.locatePiece(s), 1);
		this.names = tempNames;
		return this;
	}

	/**
	 * Structure processors that all pieces in this builder will use.
	 * 
	 * @param processors : empty by default
	 * @return JigsawPoolBuilder
	 */
	public JigsawPoolBuilder processors(StructureProcessor... processors)
	{
		this.processors = Arrays.asList(processors).stream().collect(ImmutableList.toImmutableList());
		return this;
	}

	/**
	 * Determines if waterloggable blocks should become waterlogged when placed in
	 * water.
	 * 
	 * @param maintainWater : default = false
	 * @return JigsawPoolBuilder
	 */
	public JigsawPoolBuilder maintainWater(boolean maintainWater)
	{
		this.maintainWater = maintainWater;
		return this;
	}

	/**
	 * Sets how the structures should place.
	 * 
	 * @param placementBehavior : default = RIGID
	 * @return JigsawPoolBuilder
	 */
	public JigsawPoolBuilder placementBehavior(JigsawPattern.PlacementBehaviour placementBehavior)
	{
		this.placementBehavior = placementBehavior;
		return this;
	}

	/**
	 * Generates a pool of pieces using the weights established in {@link #names}
	 * and other settings established such as {@link #maintainWater(boolean)}.
	 * 
	 * @return List
	 */
	public List<Pair<JigsawPiece, Integer>> build()
	{
		List<Pair<JigsawPiece, Integer>> jigsawList = new ArrayList<>();
		this.names.forEach((rl, i) ->
		{
			jigsawList.add(Pair.of(new GelJigsawPiece(rl, this.processors, this.placementBehavior).setMaintainWater(this.maintainWater), i));
		});
		return jigsawList.stream().collect(ImmutableList.toImmutableList());
	}

	/**
	 * Creates a simple pool of pieces based on the values input with the default
	 * settings.
	 * 
	 * @param pieceMap : A map containing the pieces and their weights.
	 * @return List
	 */
	public static List<Pair<JigsawPiece, Integer>> build(Map<JigsawPiece, Integer> pieceMap)
	{
		List<Pair<JigsawPiece, Integer>> jigsawList = new ArrayList<>();
		pieceMap.forEach((jp, i) ->
		{
			jigsawList.add(Pair.of(jp, i));
		});
		return jigsawList.stream().collect(ImmutableList.toImmutableList());
	}

	/**
	 * Creats a pool of pieces with equal chances with the default settings.
	 * 
	 * @param pieces
	 * @return List
	 */
	public static List<Pair<JigsawPiece, Integer>> build(JigsawPiece... pieces)
	{
		List<Pair<JigsawPiece, Integer>> pairs = new ArrayList<>();
		for (JigsawPiece p : pieces)
			pairs.add(Pair.of(p, 1));

		return pairs;
	}

	/**
	 * Combines multiple lists generated from {@link #build()}. Intended to be used
	 * in cases where one pool needs to have pieces with different settings.
	 * 
	 * @param lists
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public static List<Pair<JigsawPiece, Integer>> collect(List<Pair<JigsawPiece, Integer>>... lists)
	{
		List<Pair<JigsawPiece, Integer>> pairs = new ArrayList<>();
		for (List<Pair<JigsawPiece, Integer>> l : lists)
			pairs = Streams.concat(pairs.stream(), l.stream()).collect(ImmutableList.toImmutableList());
		return pairs;
	}
}
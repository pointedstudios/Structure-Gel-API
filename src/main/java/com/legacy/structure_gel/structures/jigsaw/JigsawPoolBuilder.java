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
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.StructureProcessor;

public class JigsawPoolBuilder
{
	private Map<ResourceLocation, Integer> names = ImmutableMap.of();
	private List<StructureProcessor> processors = ImmutableList.of();
	private boolean maintainWater = true;

	private JigsawPoolBuilder(Map<ResourceLocation, Integer> names)
	{
		this.names = names;
	}

	public static JigsawPoolBuilder names(Map<String, Integer> nameMap)
	{
		Map<ResourceLocation, Integer> tempNames = new HashMap<>();
		nameMap.forEach((s, i) ->
		{
			tempNames.put(JigsawRegistryHelper.locatePiece(s), i);
		});
		return new JigsawPoolBuilder(tempNames);
	}

	public static JigsawPoolBuilder names(String... nameArray)
	{
		Map<ResourceLocation, Integer> tempNames = new HashMap<>();
		for (String s : nameArray)
			tempNames.put(JigsawRegistryHelper.locatePiece(s), 1);
		return new JigsawPoolBuilder(tempNames);
	}

	public JigsawPoolBuilder processors(StructureProcessor... processors)
	{
		this.processors = Arrays.asList(processors).stream().collect(ImmutableList.toImmutableList());
		return this;
	}

	public JigsawPoolBuilder maintainWater(boolean maintainWater)
	{
		this.maintainWater = maintainWater;
		return this;
	}

	public List<Pair<JigsawPiece, Integer>> build()
	{
		List<Pair<JigsawPiece, Integer>> jigsawList = new ArrayList<>();
		this.names.forEach((rl, i) ->
		{
			jigsawList.add(Pair.of(new GelJigsawPiece(rl, this.processors).setMaintainWater(this.maintainWater), i));
		});
		return jigsawList.stream().collect(ImmutableList.toImmutableList());
	}

	/**
	 * Creates a list of Pair<JigsawPiece, Integer> based on the values input.
	 * 
	 * @param o = Should be input as "piece, chance, piece, chance, piece, chance,
	 *            etc" where "piece" = JigsawPiece and "chance" = Integer.
	 * @return
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
	 * Creats a list of Pair<JigsawPiece, Integer> with equal chances.
	 * 
	 * @param pieces
	 * @return
	 */
	public static List<Pair<JigsawPiece, Integer>> build(JigsawPiece... pieces)
	{
		List<Pair<JigsawPiece, Integer>> pairs = new ArrayList<>();
		for (JigsawPiece p : pieces)
			pairs.add(Pair.of(p, 1));

		return pairs;
	}

	@SafeVarargs
	public static <P extends Pair<JigsawPiece, Integer>> List<P> collect(List<P>... lists)
	{
		List<P> pairs = new ArrayList<>();
		for (List<P> l : lists)
			pairs = Streams.concat(pairs.stream(), l.stream()).collect(ImmutableList.toImmutableList());
		return pairs;
	}
}
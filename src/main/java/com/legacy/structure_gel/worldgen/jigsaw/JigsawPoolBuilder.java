package com.legacy.structure_gel.worldgen.jigsaw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.legacy.structure_gel.util.GelCollectors;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;

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
	private int weight = 1;
	private StructureProcessorList processors = ProcessorLists.field_244101_a;
	private boolean maintainWater = true;
	private JigsawPattern.PlacementBehaviour placementBehavior = JigsawPattern.PlacementBehaviour.RIGID;

	/**
	 * @see JigsawPoolBuilder
	 * @see JigsawRegistryHelper#builder()
	 * 
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
	 * @return {@link JigsawPoolBuilder}
	 * @see GelCollectors#mapOf(Class, Class, Object...)
	 * @see ImmutableMap#of()
	 */
	public JigsawPoolBuilder names(Map<String, Integer> nameMap)
	{
		Map<ResourceLocation, Integer> tempNames = new HashMap<>();
		nameMap.forEach((s, i) -> tempNames.put(jigsawRegistryHelper.locatePiece(s), i));
		return namesR(tempNames);
	}

	/**
	 * Set a list of names that the builder uses as structure ResourceLocations with
	 * the weights in the map.
	 * 
	 * @param nameMap : Names are left as is with no conversion
	 * @return {@link JigsawPoolBuilder}
	 * @see GelCollectors#mapOf(Class, Class, Object...)
	 * @see ImmutableMap#of()
	 */
	public JigsawPoolBuilder namesR(Map<ResourceLocation, Integer> nameMap)
	{
		this.names = nameMap;
		return this;
	}

	/**
	 * Set a list of names that the builder uses as structure ResourceLocations with
	 * equal weights.
	 * 
	 * @param names : Names are converted to {@link ResourceLocation} using
	 *            {@link JigsawRegistryHelper#locatePiece(String)} from the
	 *            {@link #jigsawRegistryHelper}. Piece weights are set in the map.
	 * @return {@link JigsawPoolBuilder}
	 */
	public JigsawPoolBuilder names(Collection<String> names)
	{
		Map<ResourceLocation, Integer> tempNames = new HashMap<>();
		for (String s : names)
			tempNames.put(jigsawRegistryHelper.locatePiece(s), this.weight);
		return namesR(tempNames);
	}

	/**
	 * Set a list of names that the builder uses as structure ResourceLocations with
	 * equal weights.
	 * 
	 * @param names : Names are left as is with no conversion
	 * @return {@link JigsawPoolBuilder}
	 */
	public JigsawPoolBuilder namesR(Collection<ResourceLocation> names)
	{
		Map<ResourceLocation, Integer> tempNames = new HashMap<>();
		for (ResourceLocation rl : names)
			tempNames.put(rl, this.weight);
		return namesR(tempNames);
	}

	/**
	 * Set a list of names that the builder uses as structure ResourceLocations with
	 * equal weights.
	 * 
	 * @param names : Names are converted to {@link ResourceLocation} using
	 *            {@link JigsawRegistryHelper#locatePiece(String)} from the
	 *            {@link #jigsawRegistryHelper}. All pieces have equal weight.
	 * @return {@link JigsawPoolBuilder}
	 */
	public JigsawPoolBuilder names(String... names)
	{
		return this.names(Arrays.asList(names));
	}

	/**
	 * Set a list of names that the builder uses as structure ResourceLocations with
	 * equal weights.
	 * 
	 * @param names : Names are left as is with no conversion
	 * @return {@link JigsawPoolBuilder}
	 */
	public JigsawPoolBuilder namesR(ResourceLocation... names)
	{
		return this.namesR(Arrays.asList(names));
	}

	/**
	 * Sets the weight of all pieces to be the value passed in. For efficiency, set
	 * the weight before the names.
	 * 
	 * @param weight
	 * @return {@link JigsawPoolBuilder}
	 */
	public JigsawPoolBuilder weight(int weight)
	{
		this.weight = weight;
		return this.namesR(this.names.keySet());
	}

	/**
	 * Structure processors that all pieces in this builder will use.
	 * 
	 * @param processors : empty by default
	 * @return {@link JigsawPoolBuilder}
	 */
	public JigsawPoolBuilder processors(StructureProcessorList processors)
	{
		this.processors = processors;
		return this;
	}

	/**
	 * Determines if waterloggable blocks should become waterlogged when placed in
	 * water.
	 * 
	 * @param maintainWater : default = true
	 * @return {@link JigsawPoolBuilder}
	 */
	public JigsawPoolBuilder maintainWater(boolean maintainWater)
	{
		this.maintainWater = maintainWater;
		return this;
	}

	/**
	 * Determines placement for the structure. It's recommeneded that you set this
	 * when you register the structure pool in {@link JigsawRegistryHelper}.
	 * 
	 * @param placementBehavior : default = RIGID
	 * @return {@link JigsawPoolBuilder}
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
	 * @return {@link List}
	 */
	public List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> build()
	{
		List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> jigsawList = new ArrayList<>();
		this.names.forEach((rl, i) -> jigsawList.add(Pair.of(createGelPiece(rl, this.processors, this.maintainWater, false), i)));

		return jigsawList.stream().collect(ImmutableList.toImmutableList());
	}

	/**
	 * Creates a simple pool of pieces based on the values input with the default
	 * settings.
	 * 
	 * @param pieceMap : A map containing the pieces and their weights.
	 * @return {@link List}
	 */
	public static List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> build(Map<JigsawPiece, Integer> pieceMap)
	{
		List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> jigsawList = new ArrayList<>();
		pieceMap.forEach((jp, i) -> jigsawList.add(Pair.of((placement) -> jp, i)));

		return jigsawList.stream().collect(ImmutableList.toImmutableList());
	}

	/**
	 * Creats a pool of pieces with equal chances with the default settings.
	 * 
	 * @param pieces
	 * @return {@link List}
	 */
	public static List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> build(JigsawPiece... pieces)
	{
		List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> pairs = new ArrayList<>();
		for (JigsawPiece p : pieces)
			pairs.add(Pair.of((placement) -> p, 1));

		return pairs;
	}

	/**
	 * Combines the JigsawPoolBuilders together after building them. This is used in
	 * cases where multiple structures exist in the same pool using different
	 * settings, processors, etc.
	 * 
	 * @param builders
	 * @return {@link List}
	 */
	public static List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> collect(JigsawPoolBuilder... builders)
	{
		return collect(Arrays.asList(builders));
	}

	/**
	 * Combines the JigsawPoolBuilders together after building them. This is used in
	 * cases where multiple structures exist in the same pool using different
	 * settings, processors, etc.
	 * 
	 * @param builders
	 * @return {@link List}
	 */
	public static List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> collect(List<JigsawPoolBuilder> builders)
	{
		List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> pairs = new ArrayList<>();
		for (JigsawPoolBuilder builder : builders)
			pairs = Streams.concat(pairs.stream(), builder.build().stream()).collect(ImmutableList.toImmutableList());
		return pairs;
	}

	/**
	 * Creates a copy of this builder. Used in cases where similar settings are used
	 * across multiple builders.
	 * 
	 * @return {@link JigsawPoolBuilder}
	 */
	public JigsawPoolBuilder clone()
	{
		return new JigsawPoolBuilder(this.jigsawRegistryHelper).weight(this.weight).namesR(this.names).maintainWater(this.maintainWater).processors(this.processors).placementBehavior(this.placementBehavior);
	}

	/**
	 * Creates a {@link GelJigsawPiece} for internal use.
	 * 
	 * @param name
	 * @param processors
	 * @param maintainWater
	 * @param ignoreEntities
	 * @return {@link Function}
	 */
	public static Function<JigsawPattern.PlacementBehaviour, GelJigsawPiece> createGelPiece(ResourceLocation name, StructureProcessorList processors, boolean maintainWater, boolean ignoreEntities)
	{
		return (placement) -> new GelJigsawPiece(Either.left(name), () -> processors, placement, maintainWater, ignoreEntities);
	}
}
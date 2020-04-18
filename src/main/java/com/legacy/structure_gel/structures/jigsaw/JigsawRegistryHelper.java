package com.legacy.structure_gel.structures.jigsaw;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

public class JigsawRegistryHelper
{
	private static String modid = "minecraft";
	private static String prefix = "";

	/**
	 * Registers a jigsaw pool in JigsawManager.REGISTRY.
	 * 
	 * @param name = The pool name. Uses JigsawRegistryHelper.prefix.
	 * @param defaultPool = The default pool if it fails I think. Not sure. Uses
	 *            JigsawRegistryHelper.prefix. "minecraft:empty" by default.
	 * @param options = The structurs that this pool can generate.
	 * @param placement = How should the structure place. Rigid by default.
	 */
	public static void register(String name, String defaultPool, List<Pair<JigsawPiece, Integer>> options, JigsawPattern.PlacementBehaviour placement)
	{
		JigsawManager.REGISTRY.register(new JigsawPattern(locatePiece(name), locatePiece(defaultPool), options, placement));
	}

	public static void register(ResourceLocation name, ResourceLocation defaultPool, List<Pair<JigsawPiece, Integer>> options, JigsawPattern.PlacementBehaviour placement)
	{
		JigsawManager.REGISTRY.register(new JigsawPattern(name, defaultPool, options, placement));
	}

	public static void register(String name, String defaultPool, List<Pair<JigsawPiece, Integer>> options)
	{
		register(name, defaultPool, options, JigsawPattern.PlacementBehaviour.RIGID);
	}

	public static void register(String name, List<Pair<JigsawPiece, Integer>> options, JigsawPattern.PlacementBehaviour placement)
	{
		register(locatePiece(name), new ResourceLocation("empty"), options, placement);
	}

	public static void register(String name, List<Pair<JigsawPiece, Integer>> options)
	{
		register(name, options, JigsawPattern.PlacementBehaviour.RIGID);
	}

	public static void setPrefix(String prefix)
	{
		JigsawRegistryHelper.prefix = prefix;
	}
	
	public static void setModId(String modid)
	{
		JigsawRegistryHelper.modid = modid;
	}
	
	public static ResourceLocation locatePiece(String modid, String key)
	{
		return new ResourceLocation(modid, prefix + key);
	}
	
	public static ResourceLocation locatePiece(String key)
	{
		return new ResourceLocation(prefix + key);
	}
}
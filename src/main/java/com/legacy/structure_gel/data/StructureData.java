package com.legacy.structure_gel.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.legacy.structure_gel.structures.jigsaw.JigsawPoolBuilder;
import com.legacy.structure_gel.structures.jigsaw.JigsawRegistryHelper;
import com.legacy.structure_gel.util.ConfigTemplates;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessor;

public class StructureData
{
	public final int seed, spacing, offset, minY, maxY;
	public final double probability;
	public final ResourceLocation registryName, startPool;
	public final List<Biome> biomes;

	public StructureData(ResourceLocation name, ResourceLocation startPool, int seed, int spacing, int offset, double probability, List<Biome> biomes, int minY, int maxY)
	{
		this.registryName = name;
		this.startPool = startPool;
		this.seed = seed;
		this.spacing = spacing;
		this.offset = offset;
		this.probability = probability;
		this.biomes = biomes;
		this.minY = minY;
		this.maxY = maxY;
	}

	public int getSeed()
	{
		return this.seed;
	}

	public double getProbability()
	{
		return this.probability;
	}

	public int getSpacing()
	{
		return this.spacing;
	}

	public int getOffset()
	{
		return this.offset;
	}

	public static StructureData parse(JsonObject json, String path) throws JsonSyntaxException
	{
		if (!JSONUtils.isString(json, "name"))
			throwJson("string", "name", path);
		if (!JSONUtils.isString(json, "start_pool"))
			throwJson("string", "start_pool", path);

		ResourceLocation name = new ResourceLocation(JSONUtils.getString(json, "name"));
		ResourceLocation startPool = new ResourceLocation(JSONUtils.getString(json, "start_pool"));

		JsonObject properties = JSONUtils.getJsonObject(json, "properties", new JsonObject());
		int seed = JSONUtils.getInt(properties, "seed", 1461684410);
		int spacing = JSONUtils.getInt(properties, "spacing", 12);
		int offset = JSONUtils.getInt(properties, "offset", 5);
		double probability = JSONUtils.getFloat(properties, "probability", 1.0F);
		List<Biome> biomes = ConfigTemplates.BiomeStructureConfig.parseBiomes(JSONUtils.getString(properties, "biomes", ""));
		JsonObject placement = JSONUtils.getJsonObject(properties, "placement", new JsonObject());
		int minY = placement.has("min_y") ? JSONUtils.getInt(placement, "min_y") : -1;
		int maxY = placement.has("max_y") ? JSONUtils.getInt(placement, "max_y") : -1;

		JSONUtils.getJsonArray(json, "pools", new JsonArray()).forEach(j ->
		{
			if (j.isJsonObject())
				parsePool(j.getAsJsonObject(), path);
		});

		return new StructureData(name, startPool, seed, spacing, offset, probability, biomes, minY, maxY);
	}

	private static void parsePool(JsonObject json, String path) throws JsonSyntaxException
	{
		if (!JSONUtils.isString(json, "name"))
			throwJson("string", "name", path);
		if (!JSONUtils.isJsonArray(json, "pieces"))
			throwJson("array", "pieces", path);

		ResourceLocation name = new ResourceLocation(JSONUtils.getString(json, "name"));
		ResourceLocation defaultPool = new ResourceLocation(JSONUtils.getString(json, "default_pool", "minecraft:empty"));
		PlacementBehaviour poolPlacement = PlacementBehaviour.getBehaviour(JSONUtils.getString(json, "placement", PlacementBehaviour.RIGID.getName()));

		JigsawRegistryHelper registry = new JigsawRegistryHelper("");
		List<JigsawPoolBuilder> builders = new ArrayList<>();
		JSONUtils.getJsonArray(json, "pieces").forEach(j ->
		{
			if (j.isJsonObject())
			{
				JsonObject jsonObj = j.getAsJsonObject();

				if (!JSONUtils.isString(jsonObj, "piece"))
					throwJson("string", "piece", path);

				ResourceLocation pieceName = new ResourceLocation(JSONUtils.getString(jsonObj, "piece"));
				int weight = JSONUtils.getInt(jsonObj, "weight", 1);
				boolean maintainWater = JSONUtils.getBoolean(jsonObj, "maintain_water", true);
				PlacementBehaviour piecePlacement = PlacementBehaviour.getBehaviour(JSONUtils.getString(jsonObj, "placement", PlacementBehaviour.RIGID.getName()));
				List<StructureProcessor> processors = parseProcessors(jsonObj, path);

				builders.add(registry.builder().weight(weight).namesR(pieceName).maintainWater(maintainWater).placementBehavior(piecePlacement).processors(processors));

			}
		});

		registry.register(name, defaultPool, JigsawPoolBuilder.collect(builders), poolPlacement);
	}

	private static List<StructureProcessor> parseProcessors(JsonObject json, String path) throws JsonSyntaxException
	{
		List<StructureProcessor> processors = new ArrayList<>();
		JSONUtils.getJsonArray(json, "processors", new JsonArray()).forEach(j ->
		{
			if (j.isJsonObject())
			{
				JsonObject jsonObj = j.getAsJsonObject();

				if (!JSONUtils.isString(jsonObj, "name"))
					throwJson("string", "name", path);
				if (!JSONUtils.hasField(jsonObj, "data"))
					throwJson("json object", "data", path);

				ResourceLocation name = new ResourceLocation(JSONUtils.getString(jsonObj, "name"));
				Optional<IStructureProcessorType<?>> optional = Registry.STRUCTURE_PROCESSOR.getValue(name);
				if (optional.isPresent())
				{
					DataResult<StructureProcessor> data = optional.get().codec().parse(new Dynamic<>(JsonOps.INSTANCE, JSONUtils.getJsonObject(jsonObj, "data"))).map(sj -> null);
					processors.add(data.get().left().get());
				}
				else
					throw new IllegalArgumentException(String.format("%s is not a registered structure processor type.", name));
			}
		});

		return processors;
	}

	private static void throwJson(String type, String fieldName, String path)
	{
		throw new JsonSyntaxException(String.format("Expected to find the %s \"%s\" in \"%s\" but it was not present.", type, fieldName, path));
	}
}

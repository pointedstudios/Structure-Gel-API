package com.legacy.structure_gel.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.data.BiomeDictionaryProvider.BiomeType;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class BiomeDictionaryManager extends JsonReloadListener
{
	private static final Gson GSON_INSTANCE = (new GsonBuilder()).create();
	private Map<ResourceLocation, BiomeType> registry = new HashMap<>();

	public BiomeDictionaryManager()
	{
		super(GSON_INSTANCE, "biome_dictionary");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> object, IResourceManager resourceManager, IProfiler profiler)
	{
		Map<ResourceLocation, BiomeType> newRegistry = new HashMap<>();

		object.forEach((name, json) ->
		{
			try
			{
				IResource resource = resourceManager.getResource(getPreparedPath(name));
				newRegistry.put(name, BiomeType.deserialize(json.getAsJsonObject(), name));
			}
			catch (Exception ex)
			{
				StructureGelMod.LOGGER.error("Couldn't parse biome dictionary entry {}", name.toString());
			}
		});

		this.registry = newRegistry;
		BiomeDictionary.reload();
		this.registry.entrySet().forEach(System.out::println);
	}

	public Map<ResourceLocation, BiomeType> getRegistry()
	{
		return this.registry;
	}

	public Optional<BiomeType> get(ResourceLocation name)
	{
		return Optional.of(registry.get(name));
	}
}

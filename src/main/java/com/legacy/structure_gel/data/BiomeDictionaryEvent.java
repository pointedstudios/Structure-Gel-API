package com.legacy.structure_gel.data;

import com.legacy.structure_gel.data.BiomeDictionary.BiomeType;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Called when it's time to register biome dictionary entries.<br>
 * <br>
 * {@link Bus#MOD}
 * 
 * @author David
 *
 */
public class BiomeDictionaryEvent extends Event
{
	public BiomeDictionaryEvent()
	{

	}

	/**
	 * Registers the passed biome type and returns it.
	 * 
	 * @param type
	 * @return {@link BiomeType}
	 */
	public BiomeType register(BiomeType type)
	{
		return BiomeDictionary.register(type);
	}
}

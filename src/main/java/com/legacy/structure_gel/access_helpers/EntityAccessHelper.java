package com.legacy.structure_gel.access_helpers;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Contains helper methods to interact with entities.
 *
 * @author David
 */
public class EntityAccessHelper
{
	/**
	 * Gets the death loot table of the entity.
	 *
	 * @param entity
	 * @return {@link ResourceLocation}
	 */
	public static ResourceLocation getDeathLootTable(MobEntity entity)
	{
		return entity.deathLootTable;
	}

	/**
	 * Sets the loot table for the entity passed.
	 *
	 * @param entity
	 * @param lootTable
	 */
	public static void setDeathLootTable(MobEntity entity, ResourceLocation lootTable)
	{
		entity.deathLootTable = lootTable;
	}
}

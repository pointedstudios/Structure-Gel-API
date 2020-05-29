package com.legacy.structure_gel.access_helpers;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

public class EntityAccessHelper
{
	public static ResourceLocation getDeathLootTable(MobEntity entity)
	{
		return entity.deathLootTable;
	}
	
	public static void setDeathLootTable(MobEntity entity, ResourceLocation lootTable)
	{
		entity.deathLootTable = lootTable;
	}
}

package com.legacy.structure_gel.access_helpers;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.WeightedSpawnerEntity;

public class SpawnerAccessHelper
{
	public static void setMinSpawnDelay(MobSpawnerTileEntity tile, short minSpawnelay)
	{
		modifyNBT(tile, nbt -> nbt.putShort("MinSpawnDelay", minSpawnelay));
	}

	public static void setMaxSpawnDelay(MobSpawnerTileEntity tile, short maxSpawnDelay)
	{
		modifyNBT(tile, nbt -> nbt.putShort("MaxSpawnDelay", maxSpawnDelay));
	}

	public static void setSpawnCount(MobSpawnerTileEntity tile, short spawnCount)
	{
		modifyNBT(tile, nbt -> nbt.putShort("SpawnCount", spawnCount));
	}

	public static void setMaxNearbyEntities(MobSpawnerTileEntity tile, short maxNearbyEntities)
	{
		modifyNBT(tile, nbt -> nbt.putShort("MaxNearbyEntities", maxNearbyEntities));
	}

	public static void setRequiredPlayerRange(MobSpawnerTileEntity tile, short requiredPlayerRange)
	{
		modifyNBT(tile, nbt -> nbt.putShort("RequiredPlayerRange", requiredPlayerRange));
	}

	public static void setSpawnRange(MobSpawnerTileEntity tile, short spawnRange)
	{
		modifyNBT(tile, nbt -> nbt.putShort("SpawnRange", spawnRange));
	}

	/**
	 * @see SpawnerAccessHelper#setSpawnPotentials(MobSpawnerTileEntity, List)
	 * @param tile
	 * @param spawnerEntities
	 */
	public static void setSpawnPotentials(MobSpawnerTileEntity tile, WeightedSpawnerEntity... spawnerEntities)
	{
		setSpawnPotentials(tile, Lists.newArrayList(spawnerEntities));
	}

	/**
	 * Takes the input {@link WeightedSpawnerEntity} list and puts it into the mob
	 * spawner's data. Nbt flags for entities can be written as follows:<br>
	 * <br>
	 * WeightedSpawnerEntity x = new WeightedSpawnerEntity();<br>
	 * x.getNbt().putString("id", "minecraft:skeleton");<br>
	 * x.getNbt().putBoolean("Glowing", true);<br>
	 * 
	 * @param tile
	 * @param spawnerEntities
	 */
	public static void setSpawnPotentials(MobSpawnerTileEntity tile, List<WeightedSpawnerEntity> spawnerEntities)
	{
		modifyNBT(tile, nbt ->
		{
			if (!spawnerEntities.isEmpty())
			{
				nbt.put("SpawnData", spawnerEntities.get(0).getNbt());
				ListNBT listNbt = new ListNBT();
				spawnerEntities.forEach(wse -> listNbt.add(wse.toCompoundTag()));
				nbt.put("SpawnPotentials", listNbt);
			}
		});
	}

	public static WeightedSpawnerEntity createSpawnerEntity(EntityType<?> entity)
	{
		return createSpawnerEntity(1, entity);
	}

	public static WeightedSpawnerEntity createSpawnerEntity(int weight, EntityType<?> entity)
	{
		return createSpawnerEntity(weight, entity, new CompoundNBT());
	}

	public static WeightedSpawnerEntity createSpawnerEntity(int weight, EntityType<?> entity, CompoundNBT entityNBT)
	{
		CompoundNBT nbt = new CompoundNBT();
		entityNBT.putString("id", entity.getRegistryName().toString());
		nbt.put("Entity", entityNBT);
		return new WeightedSpawnerEntity(weight, entityNBT);
	}

	public static void modifyNBT(MobSpawnerTileEntity tile, Consumer<CompoundNBT> nbtConsumer)
	{
		CompoundNBT nbt = new CompoundNBT();
		tile.getSpawnerBaseLogic().write(nbt);
		nbtConsumer.accept(nbt);
		tile.getSpawnerBaseLogic().read(nbt);
	}
}

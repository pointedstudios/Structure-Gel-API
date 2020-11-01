package com.legacy.structure_gel.access_helpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.WeightedSpawnerEntity;

/**
 * Contains helper methods for modifying the various fields within a mob spawner
 * through it's NBT.
 * 
 * @author David
 *
 */
public class SpawnerAccessHelper
{
	/**
	 * The minimum time a spawner should wait before spawning a mob.
	 * 
	 * @param tile
	 * @param minSpawnelay
	 */
	public static void setMinSpawnDelay(MobSpawnerTileEntity tile, int minSpawnelay)
	{
		modifySpawnerNBT(tile, nbt -> nbt.putShort("MinSpawnDelay", (short) minSpawnelay));
	}

	/**
	 * The maximum time a spawner should wait before spawning a mob.
	 * 
	 * @param tile
	 * @param maxSpawnDelay
	 */
	public static void setMaxSpawnDelay(MobSpawnerTileEntity tile, int maxSpawnDelay)
	{
		modifySpawnerNBT(tile, nbt -> nbt.putShort("MaxSpawnDelay", (short) maxSpawnDelay));
	}

	/**
	 * The maximum amount of mobs that can spawn from this spawner at a time.
	 * 
	 * @param tile
	 * @param spawnCount
	 */
	public static void setSpawnCount(MobSpawnerTileEntity tile, int spawnCount)
	{
		modifySpawnerNBT(tile, nbt -> nbt.putShort("SpawnCount", (short) spawnCount));
	}

	/**
	 * How many entities can be around the spawner before it stops spawning mobs.
	 * 
	 * @param tile
	 * @param maxNearbyEntities
	 */
	public static void setMaxNearbyEntities(MobSpawnerTileEntity tile, int maxNearbyEntities)
	{
		modifySpawnerNBT(tile, nbt -> nbt.putShort("MaxNearbyEntities", (short) maxNearbyEntities));
	}

	/**
	 * How far the player can be from the spawner for it to spawn mobs.
	 * 
	 * @param tile
	 * @param requiredPlayerRange
	 */
	public static void setRequiredPlayerRange(MobSpawnerTileEntity tile, int requiredPlayerRange)
	{
		modifySpawnerNBT(tile, nbt -> nbt.putShort("RequiredPlayerRange", (short) requiredPlayerRange));
	}

	/**
	 * The horizontal area a spawner will search to try placing mobs. The vertical
	 * area is between the y level below the spawner and the y level above it (3
	 * blocks high).
	 * 
	 * @param tile
	 * @param spawnRange
	 */
	public static void setSpawnRange(MobSpawnerTileEntity tile, int spawnRange)
	{
		modifySpawnerNBT(tile, nbt -> nbt.putShort("SpawnRange", (short) spawnRange));
	}

	/**
	 * Sets the passed {@link EntityType} as the mob that the spawner should spawn.
	 * 
	 * @param tile
	 * @param entityType
	 */
	public static void setSpawnPotentials(MobSpawnerTileEntity tile, EntityType<?> entityType)
	{
		setSpawnPotentials(tile, createSpawnerEntity(entityType));
	}

	/**
	 * @see SpawnerAccessHelper#setSpawnPotentials(MobSpawnerTileEntity, Collection)
	 * @param tile
	 * @param spawnerEntities
	 */
	public static void setSpawnPotentials(MobSpawnerTileEntity tile, WeightedSpawnerEntity... spawnerEntities)
	{
		setSpawnPotentials(tile, Arrays.asList(spawnerEntities));
	}

	/**
	 * Takes the input {@link WeightedSpawnerEntity} list and puts it into the mob
	 * spawner's data. Nbt flags for entities can be written as follows:<br>
	 * <br>
	 * {@code WeightedSpawnerEntity x = new WeightedSpawnerEntity();}<br>
	 * {@code x.getNbt().putString("id", "minecraft:skeleton");}<br>
	 * {@code x.getNbt().putBoolean("Glowing", true);}<br>
	 * <br>
	 * You can also use {@link #createSpawnerEntity(int, EntityType, CompoundNBT)}
	 * to generate one.
	 * 
	 * @param tile
	 * @param spawnerEntities
	 */
	public static void setSpawnPotentials(MobSpawnerTileEntity tile, Collection<WeightedSpawnerEntity> spawnerEntities)
	{
		modifySpawnerNBT(tile, nbt ->
		{
			if (!spawnerEntities.isEmpty())
			{
				nbt.put("SpawnData", spawnerEntities.stream().findAny().get().getNbt());
				ListNBT listNbt = new ListNBT();
				spawnerEntities.forEach(wse -> listNbt.add(wse.toCompoundTag()));
				nbt.put("SpawnPotentials", listNbt);
			}
		});
	}

	/**
	 * Creates a {@link WeightedSpawnerEntity} with the entity passed.
	 * 
	 * @param entity
	 * @return {@link WeightedSpawnerEntity}
	 */
	public static WeightedSpawnerEntity createSpawnerEntity(EntityType<?> entity)
	{
		return createSpawnerEntity(1, entity);
	}

	/**
	 * Creates a {@link WeightedSpawnerEntity} with the weight and entity passed.
	 * 
	 * @param weight
	 * @param entity
	 * @return {@link WeightedSpawnerEntity}
	 */
	public static WeightedSpawnerEntity createSpawnerEntity(int weight, EntityType<?> entity)
	{
		return createSpawnerEntity(weight, entity, new CompoundNBT());
	}

	/**
	 * Creates a {@link WeightedSpawnerEntity} with the wieght, entity, and entity
	 * nbt passed.
	 * 
	 * @param weight
	 * @param entity
	 * @param entityNBT
	 * @return {@link WeightedSpawnerEntity}
	 */
	public static WeightedSpawnerEntity createSpawnerEntity(int weight, EntityType<?> entity, CompoundNBT entityNBT)
	{
		CompoundNBT nbt = new CompoundNBT();
		entityNBT.putString("id", entity.getRegistryName().toString());
		nbt.put("Entity", entityNBT);
		return new WeightedSpawnerEntity(weight, entityNBT);
	}

	/**
	 * Applies nbtConsumer to the nbt of the spawner tile entity.
	 * 
	 * @param tile
	 * @param nbtConsumer
	 */
	public static void modifySpawnerNBT(MobSpawnerTileEntity tile, Consumer<CompoundNBT> nbtConsumer)
	{
		CompoundNBT nbt = new CompoundNBT();
		tile.getSpawnerBaseLogic().write(nbt);
		nbtConsumer.accept(nbt);
		tile.getSpawnerBaseLogic().read(nbt);
	}
}

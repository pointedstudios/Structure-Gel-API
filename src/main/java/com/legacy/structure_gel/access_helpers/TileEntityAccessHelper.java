package com.legacy.structure_gel.access_helpers;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedSpawnerEntity;

/**
 * Contains a handful of methods to interface with tile entities, namely
 * spawners at the moment.
 * 
 * @author David
 *
 */
public class TileEntityAccessHelper
{
	public static void modifyNBT(TileEntity tile, Consumer<CompoundNBT> nbtConsumer)
	{
		CompoundNBT nbt = new CompoundNBT();
		tile.write(nbt);
		nbtConsumer.accept(nbt);
		tile.read(tile.getBlockState(), nbt);
	}
	
	/**
	 * @see TileEntityAccessHelper#setSpawnerSpawns(MobSpawnerTileEntity, List)
	 * @deprecated See {@link SpawnerAccessHelper} TODO remove in 1.17
	 * @param tile
	 * @param spawnerEntities
	 */
	@Deprecated
	public static void setSpawnerSpawns(MobSpawnerTileEntity tile, WeightedSpawnerEntity... spawnerEntities)
	{
		SpawnerAccessHelper.setSpawnPotentials(tile, spawnerEntities);
	}

	/**
	 * Takes the input {@link WeightedSpawnerEntity} list and puts it into the mob
	 * spawner's data. Nbt flags for entities can be written as follows:<br>
	 * <br>
	 * WeightedSpawnerEntity x = new WeightedSpawnerEntity();<br>
	 * x.getNbt().putString("id", "minecraft:skeleton");<br>
	 * x.getNbt().putBoolean("Glowing", true);<br>
	 * 
	 * @deprecated See {@link SpawnerAccessHelper} TODO remove in 1.17
	 * @param tile
	 * @param spawnerEntities
	 */
	@Deprecated
	public static void setSpawnerSpawns(MobSpawnerTileEntity tile, List<WeightedSpawnerEntity> spawnerEntities)
	{
		SpawnerAccessHelper.setSpawnPotentials(tile, spawnerEntities);
	}
}

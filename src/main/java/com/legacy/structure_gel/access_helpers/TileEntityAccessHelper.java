package com.legacy.structure_gel.access_helpers;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
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
	/**
	 * @see TileEntityAccessHelper#setSpawnerSpawns(MobSpawnerTileEntity, List)
	 * @param tile
	 * @param spawnerEntities
	 */
	public static void setSpawnerSpawns(MobSpawnerTileEntity tile, WeightedSpawnerEntity... spawnerEntities)
	{
		TileEntityAccessHelper.setSpawnerSpawns(tile, Lists.newArrayList(spawnerEntities));
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
	public static void setSpawnerSpawns(MobSpawnerTileEntity tile, List<WeightedSpawnerEntity> spawnerEntities)
	{
		CompoundNBT compound = new CompoundNBT();
		tile.getSpawnerBaseLogic().write(compound);

		if (!spawnerEntities.isEmpty())
		{
			compound.put("SpawnData", spawnerEntities.get(0).getNbt());

			ListNBT listNbt = new ListNBT();
			spawnerEntities.forEach(wse -> listNbt.add(wse.toCompoundTag()));
			compound.put("SpawnPotentials", listNbt);

		}
		tile.getSpawnerBaseLogic().read(compound);
	}
}

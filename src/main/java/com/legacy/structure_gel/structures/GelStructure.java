package com.legacy.structure_gel.structures;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

/**
 * An extension of {@link Structure} that allows for more precise tweaking and
 * removes the weird grid pattern that can occur when using
 * {@link ScatteredStructure}.
 * 
 * @author David
 *
 * @param <C>
 */
public abstract class GelStructure<C extends IFeatureConfig> extends Structure<C>
{
	public GelStructure(Function<Dynamic<?>, ? extends C> configFactoryIn)
	{
		super(configFactoryIn);
		MinecraftForge.EVENT_BUS.addListener(this::potentialSpawnsEvent);
	}

	/**
	 * Checks to see if this structure can generate in the given chunk using a grid
	 * with custom spacing and offsets.
	 * 
	 * @see #getSpacing()
	 * @see #getOffset()
	 * 
	 * @param chunkGen
	 * @param rand
	 * @param chunkPosX
	 * @param chunkPosZ
	 * @return boolean
	 */
	public boolean canStartInChunk(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ, Biome biomeIn)
	{
		int spacing = this.getSpacing();
		int gridX = (chunkPosX / spacing) * spacing;
		int gridZ = (chunkPosZ / spacing) * spacing;

		((SharedSeedRandom) rand).setLargeFeatureSeedWithSalt(chunkGen.getSeed(), gridX, gridZ, this.getSeed());
		int spacingOffset = this.getOffset();
		int offsetX = rand.nextInt(spacingOffset * 2 + 1) - spacingOffset;
		int offsetZ = rand.nextInt(spacingOffset * 2 + 1) - spacingOffset;

		int gridOffsetX = gridX + offsetX;
		int gridOffsetZ = gridZ + offsetZ;

		return chunkPosX == gridOffsetX && chunkPosZ == gridOffsetZ && chunkGen.hasStructure(biomeIn, this);
	}
	
	/**
	 * hasStartAt<br>
	 * <br>
	 * Runs {@link #canStartInChunk(ChunkGenerator, Random, int, int)} to see if the
	 * chunk is a valid chunk to generate this structure in, and then checks
	 * {@link #getProbability()} to see if it will succeed in generating.
	 */
	@Override
	public boolean func_225558_a_(BiomeManager biomeManagerIn, ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ, Biome biomeIn)
	{
		if (canStartInChunk(chunkGen, rand, chunkPosX, chunkPosZ, biomeIn))
		{
			((SharedSeedRandom) rand).setLargeFeatureSeedWithSalt(chunkGen.getSeed(), chunkPosX, chunkPosZ, this.getSeed());
			return rand.nextDouble() < getProbability();
		}
		else
			return false;
	}

	/**
	 * Every structure should have a different seed to prevent them from overlapping
	 * as best as possible, especially when the same chances are used. This seed
	 * should be constant.
	 * 
	 * @return int
	 */
	public abstract int getSeed();

	/**
	 * This is the probability of the structure generating in a given chunk,
	 * expressed as a percent.
	 * 
	 * @return double
	 */
	public abstract double getProbability();

	/**
	 * When checking if a structure can be placed in a given chunk, this is called
	 * to determine how far apart each structure should be from eachother. A value
	 * of 4 will space structures out 4 chunks apart.<br>
	 * <br>
	 * This number should not be negative or 0. 0 will crash. Don't do it.
	 * 
	 * @return int
	 */
	public abstract int getSpacing();

	/**
	 * When checking if a structure can be placed in a given chunk, this value is
	 * used along with {@link #getSpacing()} to give the structure an offset as to
	 * not make the grid apparent. It's recommended that this value is no larger
	 * than half of what {@link #getSpacing()} returns to prevent structure
	 * overlap.<br>
	 * <br>
	 * This number should not be negative.
	 * 
	 * @return int
	 */
	public abstract int getOffset();

	@Override
	public String getStructureName()
	{
		return this.getRegistryName().toString();
	}

	/**
	 * Return a list of hostile mobs to change spawn behavior.
	 */
	public List<Biome.SpawnListEntry> getSpawnList()
	{
		return Collections.emptyList();
	}

	/**
	 * Return a list of passive mobs to change spawn behavior.
	 */
	public List<Biome.SpawnListEntry> getCreatureSpawnList()
	{
		return Collections.emptyList();
	}

	/**
	 * Automatically registered to the event bus. Use {@link #getSpawnList()} or
	 * {@link #getCreatureSpawnList()} to set the mob spawns that should occur.
	 * 
	 * @param event
	 */
	public void potentialSpawnsEvent(WorldEvent.PotentialSpawns event)
	{
		if (this.isPositionInStructure(event.getWorld(), event.getPos()))
		{
			if (event.getType() == EntityClassification.MONSTER && !this.getSpawnList().isEmpty())
			{
				event.getList().clear();
				event.getList().addAll(this.getSpawnList());
			}
			if (event.getType() == EntityClassification.CREATURE && !this.getCreatureSpawnList().isEmpty())
			{
				event.getList().clear();
				event.getList().addAll(this.getCreatureSpawnList());
			}
		}
	}
}
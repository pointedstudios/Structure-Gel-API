package com.legacy.structure_gel.structures;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;

/**
 * An extension of {@link Structure} that allows for more precise tweaking and
 * removes the weird grid pattern that can occur when using
 * {@link ScatteredStructure}.
 * 
 * @author David
 *
 * @param <C>
 */
public abstract class ProbabilityStructure<C extends IFeatureConfig> extends Structure<C>
{
	public ProbabilityStructure(Function<Dynamic<?>, ? extends C> configFactoryIn)
	{
		super(configFactoryIn);
	}

	/**
	 * Checks to see if this structure can generate in the given chunk using a grid
	 * with custom spacing and offsets.
	 * 
	 * @see #getSpacing()
	 * @see #getSpacingOffset()
	 * 
	 * @param chunkGen
	 * @param rand
	 * @param chunkPosX
	 * @param chunkPosZ
	 * @return boolean
	 */
	public boolean canStartInChunk(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ)
	{
		int spacing = this.getSpacing();
		int gridX = (chunkPosX / spacing) * spacing;
		int gridZ = (chunkPosZ / spacing) * spacing;

		((SharedSeedRandom) rand).setLargeFeatureSeedWithSalt(chunkGen.getSeed(), gridX, gridZ, this.getSeed());
		int spacingOffset = this.getSpacingOffset();
		int offsetX = rand.nextInt(spacingOffset * 2 + 1) - spacingOffset;
		int offsetZ = rand.nextInt(spacingOffset * 2 + 1) - spacingOffset;

		int gridOffsetX = gridX + offsetX;
		int gridOffsetZ = gridZ + offsetZ;

		return chunkPosX == gridOffsetX && chunkPosZ == gridOffsetZ && chunkGen.hasStructure(chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX << 4) + 9, 0, (chunkPosZ << 4) + 9)), this);
	}

	/**
	 * Runs {@link #canStartInChunk(ChunkGenerator, Random, int, int)} to see if the
	 * chunk is a valid chunk to generate this structure in, and then checks
	 * {@link #getChance()} to see if it will succeed in generating.
	 */
	@Override
	public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ)
	{
		if (canStartInChunk(chunkGen, rand, chunkPosX, chunkPosZ))
		{
			((SharedSeedRandom) rand).setLargeFeatureSeedWithSalt(chunkGen.getSeed(), chunkPosX, chunkPosZ, this.getSeed());
			return rand.nextDouble() < getChance();
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
	public abstract double getChance();

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
	public abstract int getSpacingOffset();
}

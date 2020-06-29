package com.legacy.structure_gel.structures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.legacy.structure_gel.access_helpers.StructureAccessHelper;
import com.mojang.serialization.Codec;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

/**
 * An extension of {@link Structure} that allows for more precise tweaking and
 * handles structure spacing.
 * 
 * @author David
 *
 * @param <C>
 */
public abstract class GelStructure<C extends IFeatureConfig> extends Structure<C>
{
	public final Map<EntityClassification, List<SpawnListEntry>> SPAWNS = new HashMap<>();

	public GelStructure(Codec<C> codec)
	{
		super(codec);
		MinecraftForge.EVENT_BUS.addListener(this::potentialSpawnsEvent);
		this.setLakeProof(true);
	}

	/**
	 * Determines if lakes shuold generate inside of this structure or not. This is
	 * automatically set to true when you create the structure.
	 * 
	 * @param lakeProof
	 * @return
	 */
	public GelStructure<C> setLakeProof(boolean lakeProof)
	{
		if (lakeProof)
			StructureAccessHelper.addLakeProofStructure(this);
		else
			StructureAccessHelper.removeLakeProofStructure(this);
		return this;
	}

	public GelStructure<C> setSpawnList(EntityClassification classification, List<SpawnListEntry> spawns)
	{
		this.SPAWNS.put(classification, spawns);
		return this;
	}

	/**
	 * Checks to see if this structure can generate in the given chunk using a grid
	 * with custom spacing and offsets.
	 * 
	 * @see #getSpacing()
	 * @see #getOffset()
	 */
	// getChunkPosForPos
	@Override
	public ChunkPos func_236392_a_(StructureSeparationSettings settings, long seed, SharedSeedRandom sharedSeedRand, int x, int z)
	{
		int spacing = this.getSpacing();
		int gridX = ((x / spacing) * spacing);
		int gridZ = ((z / spacing) * spacing);

		int offset = this.getOffset() + 1;
		sharedSeedRand.setLargeFeatureSeedWithSalt(seed, gridX, gridZ, this.getSeed());
		int offsetX = sharedSeedRand.nextInt(offset);
		int offsetZ = sharedSeedRand.nextInt(offset);

		int gridOffsetX = gridX + offsetX;
		int gridOffsetZ = gridZ + offsetZ;

		return new ChunkPos(gridOffsetX, gridOffsetZ);
	}

	// canGenerate
	@Override
	protected boolean func_230363_a_(ChunkGenerator chunkGen, BiomeProvider biomeProvider, long seed, SharedSeedRandom sharedSeedRand, int chunkPosX, int chunkPosZ, Biome biomeIn, ChunkPos chunkPos, C config)
	{
		sharedSeedRand.setLargeFeatureSeedWithSalt(seed, chunkPosX, chunkPosZ, this.getSeed());
		return sharedSeedRand.nextDouble() < getProbability();
	}

	// findNearest
	@Override
	public BlockPos func_236388_a_(IWorldReader worldIn, StructureManager structureManager, BlockPos startPos, int searchRadius, boolean skipExistingChunks, long seed, StructureSeparationSettings settings)
	{
		int spacing = this.getSpacing();
		int chunkX = startPos.getX() >> 4;
		int chunkZ = startPos.getZ() >> 4;
		int i = 0;

		for (SharedSeedRandom rand = new SharedSeedRandom(); i <= searchRadius; ++i)
		{
			for (int xOffset = -i; xOffset <= i; ++xOffset)
			{
				boolean flag = xOffset == -i || xOffset == i;

				for (int zOffset = -i; zOffset <= i; ++zOffset)
				{
					boolean flag1 = zOffset == -i || zOffset == i;
					if (flag || flag1)
					{
						int newChunkX = chunkX + spacing * xOffset;
						int newChunkZ = chunkZ + spacing * zOffset;
						ChunkPos chunkpos = this.func_236392_a_(settings, seed, rand, newChunkX, newChunkZ);
						IChunk chunk = worldIn.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_STARTS);
						StructureStart<?> start = structureManager.func_235013_a_(SectionPos.from(chunk.getPos(), 0), this, chunk);
						if (start != null && start.isValid())
						{
							if (skipExistingChunks && start.isRefCountBelowMax())
							{
								start.incrementRefCount();
								return start.getPos();
							}

							if (!skipExistingChunks)
							{
								return start.getPos();
							}
						}

						if (i == 0)
						{
							break;
						}
					}
				}

				if (i == 0)
				{
					break;
				}
			}
		}

		return null;
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
	 * Deprecated: Use {@link #getSpawns(EntityClassification)} and
	 * {@link #setSpawnList(EntityClassification, List)}.
	 * <p>
	 * Return a list of hostile mobs to change spawn behavior. Return null to do
	 * nothing.
	 */
	@Nullable
	@Deprecated
	@Override
	public List<Biome.SpawnListEntry> getSpawnList()
	{
		return this.SPAWNS.get(EntityClassification.MONSTER);
	}

	/**
	 * Deprecated: Use {@link #getSpawns(EntityClassification)} and
	 * {@link #setSpawnList(EntityClassification, List)}.
	 * <p>
	 * Return a list of passive mobs to change spawn behavior. Return null to do
	 * nothing.
	 */
	@Nullable
	@Deprecated
	@Override
	public List<Biome.SpawnListEntry> getCreatureSpawnList()
	{
		return this.SPAWNS.get(EntityClassification.CREATURE);
	}

	/**
	 * Returns a list of mobs to spawn based on the classification put in. Any
	 * classification not set in {@link #SPAWNS} will return null, and be ignored.
	 * 
	 * @param classification
	 * @return
	 */
	@Nullable
	public List<SpawnListEntry> getSpawns(EntityClassification classification)
	{
		switch (classification)
		{
		case MONSTER:
			return getSpawnList();
		case CREATURE:
			return getCreatureSpawnList();
		default:
			return this.SPAWNS.get(classification);
		}
	}

	/**
	 * Automatically registered to the event bus. Uses
	 * {@link #getSpawns(EntityClassification)} to get what mobs should spawn.
	 * 
	 * @param event
	 */
	public void potentialSpawnsEvent(WorldEvent.PotentialSpawns event)
	{
		if (event.getWorld() instanceof ServerWorld && ((ServerWorld) event.getWorld()).func_241112_a_().func_235010_a_(event.getPos(), false, this).isValid())
		{
			if (this.getSpawns(event.getType()) != null)
			{
				event.getList().clear();
				event.getList().addAll(this.getSpawns(event.getType()));
			}
		}
	}

	/**
	 * What stage of generation your structure should be generated during.
	 */
	public GenerationStage.Decoration func_236396_f_()
	{
		return GenerationStage.Decoration.SURFACE_STRUCTURES;
	}
}

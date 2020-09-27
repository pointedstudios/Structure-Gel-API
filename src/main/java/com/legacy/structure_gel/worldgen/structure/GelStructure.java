package com.legacy.structure_gel.worldgen.structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.access_helpers.StructureAccessHelper;
import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.worldgen.jigsaw.GelJigsawStructure;
import com.mojang.serialization.Codec;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

/**
 * An extension of {@link Structure} that allows for more precise tweaking and
 * handles structure spacing. For jigsaw related structures, use
 * {@link GelJigsawStructure} or it's children.
 * 
 * @author David
 *
 * @param <C>
 */
public abstract class GelStructure<C extends IFeatureConfig> extends Structure<C>
{
	public final Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawns = new HashMap<>();
	public Integer seed = null;

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
	 * @return {@link GelStructure}
	 */
	public GelStructure<C> setLakeProof(boolean lakeProof)
	{
		if (lakeProof)
			StructureAccessHelper.addLakeProofStructure(this);
		else
			StructureAccessHelper.removeLakeProofStructure(this);
		return this;
	}

	/**
	 * Sets the spawns that can exist in this structure for the passed mob
	 * classification. Set to an empty list to prevent spawns, and leave as null to
	 * change nothing.
	 * 
	 * @param classification
	 * @param spawns
	 * @return {@link GelStructure}
	 */
	public GelStructure<C> setSpawnList(EntityClassification classification, List<MobSpawnInfo.Spawners> spawns)
	{
		this.spawns.put(classification, spawns);
		return this;
	}

	/**
	 * Gets a list of {@link DimensionSettings} to tell your structure where to
	 * generate. Can be set using configs. You should only need to modify this if a
	 * dimension that this structure generates in does not use a vanilla noise
	 * setting.
	 * 
	 * @return {@link List}
	 */
	public List<DimensionSettings> getNoiseSettingsToGenerateIn()
	{
		return ImmutableList.of(DimensionSettings.field_242734_c, DimensionSettings.field_242735_d, DimensionSettings.field_242736_e, DimensionSettings.field_242737_f, DimensionSettings.field_242738_g, DimensionSettings.field_242739_h).stream().map(WorldGenRegistries.NOISE_SETTINGS::getValueForKey).collect(ImmutableList.toImmutableList());
	}

	/**
	 * What stage of generation your structure should be generated during. Surface
	 * structures by default.
	 * 
	 * @return {@link Decoration}
	 */
	@Override
	public GenerationStage.Decoration func_236396_f_()
	{
		return GenerationStage.Decoration.SURFACE_STRUCTURES;
	}

	/**
	 * Checks to see if this structure can generate in the given chunk using a grid
	 * with custom spacing and offsets.
	 * 
	 * @see #getSpacing()
	 * @see #getOffset()
	 * @return {@link ChunkPos}
	 */
	// getChunkPosForPos
	@Internal
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
	@Internal
	@Override
	protected boolean func_230363_a_(ChunkGenerator chunkGen, BiomeProvider biomeProvider, long seed, SharedSeedRandom sharedSeedRand, int chunkPosX, int chunkPosZ, Biome biomeIn, ChunkPos chunkPos, C config)
	{
		sharedSeedRand.setLargeFeatureSeedWithSalt(seed, chunkPosX, chunkPosZ, this.getSeed());
		return sharedSeedRand.nextDouble() < getProbability();
	}

	// findNearest
	@Internal
	@Override
	public BlockPos func_236388_a_(IWorldReader worldIn, StructureManager structureManager, BlockPos startPos, int searchRadius, boolean skipExistingChunks, long seed, StructureSeparationSettings settings)
	{
		return super.func_236388_a_(worldIn, structureManager, startPos, searchRadius, skipExistingChunks, seed, this.getSeparationSettings());
	}

	/**
	 * Every structure should have a different seed to prevent them from overlapping
	 * as best as possible, especially when the same chances are used. This seed
	 * should be constant.<br>
	 * <br>
	 * Returns the hash code of this instance's registry name by default.
	 * 
	 * @return {@link Integer}
	 */
	public int getSeed()
	{
		if (this.seed == null)
		{
			if (this.getRegistryName() == null)
			{
				this.seed = 0;
				StructureGelMod.LOGGER.error(String.format("The structure %s does not have a registry name. Seed defaulted to 0. This should be avoided.", this.getClass().getName()));
			}
			else
				this.seed = this.getRegistryName().toString().hashCode();
		}
		return this.seed;
	}

	/**
	 * This is the probability of the structure generating in a given chunk,
	 * expressed as a percent.
	 * 
	 * @return {@link Double}
	 */
	public abstract double getProbability();

	/**
	 * When checking if a structure can be placed in a given chunk, this is called
	 * to determine how far apart each structure should be from eachother. A value
	 * of 4 will space structures out 4 chunks apart.<br>
	 * <br>
	 * This number should not be negative or 0. 0 will crash. Don't do it.
	 * 
	 * @return {@link Integer}
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
	 * @return {@link Integer}
	 */
	public abstract int getOffset();

	/**
	 * Gets the registry name of the structure.
	 * 
	 * @return {@link String}
	 */
	@Override
	public String getStructureName()
	{
		return this.getRegistryName().toString();
	}

	/**
	 * Return a list of hostile mobs to change spawn behavior. Return null to do
	 * nothing.
	 * 
	 * @deprecated Use {@link #getSpawns(EntityClassification)} and
	 *             {@link #setSpawnList(EntityClassification, List)}.
	 * @return {@link List}
	 */
	@Nullable
	@Deprecated
	@Override
	public List<MobSpawnInfo.Spawners> getSpawnList()
	{
		return this.spawns.get(EntityClassification.MONSTER);
	}

	/**
	 * Return a list of passive mobs to change spawn behavior. Return null to do
	 * nothing.
	 * 
	 * @deprecated Use {@link #getSpawns(EntityClassification)} and
	 *             {@link #setSpawnList(EntityClassification, List)}.
	 * @return {@link List}
	 */
	@Nullable
	@Deprecated
	@Override
	public List<MobSpawnInfo.Spawners> getCreatureSpawnList()
	{
		return this.spawns.get(EntityClassification.CREATURE);
	}

	/**
	 * Returns a list of mobs to spawn based on the classification put in. Any
	 * classification not set in {@link #spawns} will return null, and be ignored.
	 * 
	 * @param classification
	 * @return {@link List}
	 */
	@Nullable
	public List<MobSpawnInfo.Spawners> getSpawns(EntityClassification classification)
	{
		switch (classification)
		{
		case MONSTER:
			return getSpawnList();
		case CREATURE:
			return getCreatureSpawnList();
		default:
			return this.spawns.get(classification);
		}
	}

	/**
	 * Automatically registered to the event bus. Uses
	 * {@link #getSpawns(EntityClassification)} to get what mobs should spawn.
	 * 
	 * @param event
	 */
	@Internal
	public void potentialSpawnsEvent(WorldEvent.PotentialSpawns event)
	{
		if (this.getSpawns(event.getType()) != null)
		{
			if (event.getWorld() instanceof ServerWorld && ((ServerWorld) event.getWorld()).func_241112_a_().func_235010_a_(event.getPos(), false, this).isValid())
			{
				event.getList().clear();
				event.getList().addAll(this.getSpawns(event.getType()));
			}
		}
	}

	/**
	 * Gets a {@link StructureSeparationSettings} based on the API values.
	 * 
	 * @return {@link StructureSeparationSettings}
	 */
	@Internal
	public StructureSeparationSettings getSeparationSettings()
	{
		return new StructureSeparationSettings(this.getSpacing(), this.getOffset(), this.getSeed());
	}
}

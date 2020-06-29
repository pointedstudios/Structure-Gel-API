package com.legacy.structure_gel.data;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.structures.GelStructure;
import com.legacy.structure_gel.structures.GelStructurePiece;
import com.legacy.structure_gel.structures.GelStructureStart;

import net.minecraft.entity.EntityClassification;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class JsonStructure extends GelStructure<NoFeatureConfig>
{
	public final StructureData data;

	public JsonStructure(StructureData data)
	{
		super(NoFeatureConfig.field_236558_a_);
		this.data = data;
		this.SPAWNS.putAll(data.spawns);
	}

	@Override
	public int getSeed()
	{
		return this.data.getSeed();
	}

	@Override
	public double getProbability()
	{
		return this.data.getProbability();
	}

	@Override
	public int getSpacing()
	{
		return this.data.getSpacing();
	}

	@Override
	public int getOffset()
	{
		return this.data.getOffset();
	}

	@Override
	@Nullable
	public List<SpawnListEntry> getSpawns(EntityClassification classification)
	{
		return this.SPAWNS.get(classification);
	}
	
	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory()
	{
		return JsonStart::new;
	}

	public static class JsonStart extends GelStructureStart<NoFeatureConfig>
	{
		public JsonStart(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox boundsIn, int referenceIn, long seed)
		{
			super(structureIn, chunkX, chunkZ, boundsIn, referenceIn, seed);
		}

		@Override
		public void func_230364_a_(ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config)
		{
			StructureData data = ((JsonStructure) this.getStructure()).data;
			
			BlockPos pos = new BlockPos(chunkX * 16 + rand.nextInt(15), 90, chunkZ * 16 + rand.nextInt(15));
			JsonPieces.assemble(generator, templateManagerIn, pos, this.components, this.rand, data.startPool, 7);
			this.recalculateStructureSize();

			if (data.minY > -1 && data.maxY > -1 && data.minY < data.maxY)
				this.setHeight(data.minY + rand.nextInt(data.maxY - data.minY));
		}
	}

	public static class JsonPieces
	{
		public static void assemble(ChunkGenerator chunkGen, TemplateManager template, BlockPos pos, List<StructurePiece> pieces, SharedSeedRandom seed, ResourceLocation start, int size)
		{
			JigsawManager.func_236823_a_(start, size, JsonPieces.Piece::new, chunkGen, template, pos, pieces, seed, true, true);
		}

		public static class Piece extends GelStructurePiece
		{
			public Piece(TemplateManager template, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox boundingBox)
			{
				super(StructureGelMod.StructureRegistry.JSON_PIECE, template, jigsawPiece, pos, groundLevelDelta, rotation, boundingBox);
			}

			public Piece(TemplateManager template, CompoundNBT nbt)
			{
				super(template, nbt, StructureGelMod.StructureRegistry.JSON_PIECE);
			}

			@Override
			public void handleDataMarker(String key, IWorld world, BlockPos pos, Random rand, MutableBoundingBox bounds)
			{
			}
		}
	}
}

package com.legacy.structure_gel.data;

import java.util.List;
import java.util.Random;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.structures.GelStructure;
import com.legacy.structure_gel.structures.GelStructurePiece;
import com.legacy.structure_gel.structures.GelStructureStart;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
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
		super(NoFeatureConfig::deserialize);
		this.data = data;
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
	public int getSize()
	{
		return this.data.getChunkSize();
	}

	@Override
	public IStartFactory getStartFactory()
	{
		return JsonStart::new;
	}

	public static class JsonStart extends GelStructureStart
	{
		public JsonStart(Structure<?> structureIn, int chunkX, int chunkZ, MutableBoundingBox boundsIn, int referenceIn, long seed)
		{
			super(structureIn, chunkX, chunkZ, boundsIn, referenceIn, seed);
		}

		@Override
		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn)
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
		public static void assemble(ChunkGenerator<?> chunkGen, TemplateManager template, BlockPos pos, List<StructurePiece> pieces, SharedSeedRandom seed, ResourceLocation start, int size)
		{
			JigsawManager.addPieces(start, size, JsonPieces.Piece::new, chunkGen, template, pos, pieces, seed);
		}

		public static class Piece extends GelStructurePiece
		{
			public Piece(TemplateManager template, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox boundingBox)
			{
				super(StructureGelMod.FeatureRegistry.JSON_PIECE, template, jigsawPiece, pos, groundLevelDelta, rotation, boundingBox);
			}

			public Piece(TemplateManager template, CompoundNBT nbt)
			{
				super(template, nbt, StructureGelMod.FeatureRegistry.JSON_PIECE);
			}

			@Override
			public void handleDataMarker(String key, IWorld world, BlockPos pos, Random rand, MutableBoundingBox bounds)
			{
			}
		}
	}
}
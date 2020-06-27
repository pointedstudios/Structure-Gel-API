package com.legacy.structure_gel.test;

import com.legacy.structure_gel.structures.GelConfigStructure;
import com.legacy.structure_gel.structures.GelStructure;
import com.legacy.structure_gel.structures.GelStructureStart;
import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;
import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class TowerStructure extends GelStructure<NoFeatureConfig>
{
	public TowerStructure(Codec<NoFeatureConfig> codec)
	{
		super(codec);
	}

	@Override
	public int getSeed()
	{
		return 155166;
	}
	
	@Override
	public double getProbability()
	{
		return 1;
	}

	@Override
	public int getSpacing()
	{
		return 2;
	}

	@Override
	public int getOffset()
	{
		return 0;
	}
	
	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory()
	{
		return TowerStructure.Start::new;
	}

	public static class Start extends GelStructureStart<NoFeatureConfig>
	{

		public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox boundsIn, int referenceIn, long seed)
		{
			super(structureIn, chunkX, chunkZ, boundsIn, referenceIn, seed);
		}

		@Override
		public void func_230364_a_(ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config)
		{
			BlockPos pos = new BlockPos(chunkX * 16 + this.rand.nextInt(5), 90, chunkZ * 16 + this.rand.nextInt(5));
			TowerPieces.assemble(generator, templateManagerIn, pos, this.components, this.rand);
			this.recalculateStructureSize();
		}
	}
}

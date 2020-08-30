package com.legacy.structure_gel.worldgen.jigsaw;

import com.legacy.structure_gel.worldgen.structure.GelStructure;
import com.mojang.serialization.Codec;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class GelJigsawStructure extends GelStructure<VillageConfig>
{
	public final int deltaY;
	public final boolean flag1;
	public final boolean flag2;

	public GelJigsawStructure(Codec<VillageConfig> codec, int deltaY, boolean flag1, boolean flag2)
	{
		super(codec);
		this.deltaY = deltaY;
		this.flag1 = flag1;
		this.flag2 = flag2;
	}

	/**
	 * Override this with a declaration of your own extension of
	 * {@link GelStructurePiece} to use data markers.
	 * 
	 * @return IPieceFactory
	 */
	public IPieceFactory getPieceType()
	{
		return GelStructurePiece::new;
	}

	public AbstractVillagePiece getPiece(TemplateManager templateManager, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox bounds)
	{
		return getPieceType().create(templateManager, jigsawPiece, pos, groundLevelDelta, rotation, bounds);
	}

	public Structure.IStartFactory<VillageConfig> getStartFactory()
	{
		return (structure, chunkX, chunkZ, bounds, references, seed) -> new GelJigsawStructure.Start(this, chunkX, chunkZ, bounds, references, seed);
	}

	public static class Start extends StructureStart<VillageConfig>
	{
		private final GelJigsawStructure jigsawStructure;

		public Start(GelJigsawStructure jigsawStructure, int chunkX, int chunkZ, MutableBoundingBox bounds, int references, long seed)
		{
			super(jigsawStructure, chunkX, chunkZ, bounds, references, seed);
			this.jigsawStructure = jigsawStructure;
		}

		public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGen, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, VillageConfig config)
		{
			BlockPos blockpos = new BlockPos(chunkX * 16, this.jigsawStructure.deltaY, chunkZ * 16);
			JigsawPatternRegistry.func_244093_a();
			JigsawManager.func_242837_a(dynamicRegistries, config, this.jigsawStructure::getPiece, chunkGen, templateManager, blockpos, this.components, this.rand, this.jigsawStructure.flag1, this.jigsawStructure.flag2);
			this.recalculateStructureSize();
		}
	}
}

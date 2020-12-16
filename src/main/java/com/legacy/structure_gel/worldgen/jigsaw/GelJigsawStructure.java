package com.legacy.structure_gel.worldgen.jigsaw;

import com.legacy.structure_gel.util.Internal;
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
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

/**
 * An extension of {@link GelStructure} to use with jigsaw structures.
 *
 * @author David
 */
public abstract class GelJigsawStructure extends GelStructure<VillageConfig>
{
	/**
	 * If {@link GelJigsawStructure#placesOnSurface} is set to false, this behaves
	 * as the y value that your structure places at. If true, your structure will
	 * place on the surface with this as a vertical offset.
	 */
	public final int deltaY;
	/**
	 * Unsure what this is used for at the time of writing this. Set to true in
	 * villages and false in bastions.
	 */
	public final boolean flag1;
	/**
	 * Determines if your structure should place on the surface or at a specific y
	 * value. Set to true for villages and false for bastions.
	 */
	public final boolean placesOnSurface;

	public GelJigsawStructure(Codec<VillageConfig> codec, int deltaY, boolean flag1, boolean placesOnSurface)
	{
		super(codec);
		this.deltaY = deltaY;
		this.flag1 = flag1;
		this.placesOnSurface = placesOnSurface;
	}

	/**
	 * Override this with a declaration of your own extension of
	 * {@link GelStructurePiece} to use data markers.
	 *
	 * @return {@link IPieceFactory}
	 */
	public IPieceFactory getPieceType()
	{
		return GelStructurePiece::new;
	}

	/**
	 * @param templateManager
	 * @param jigsawPiece
	 * @param pos
	 * @param groundLevelDelta
	 * @param rotation
	 * @param bounds
	 * @return {@link AbstractVillagePiece}
	 */
	@Internal
	public AbstractVillagePiece getPiece(TemplateManager templateManager, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox bounds)
	{
		return getPieceType().create(templateManager, jigsawPiece, pos, groundLevelDelta, rotation, bounds);
	}

	/**
	 * Called in the start factory so you won't need to make your own for basic
	 * things.
	 *
	 * @param start
	 * @param dynamicRegistries
	 * @param chunkGen
	 * @param templateManager
	 * @param chunkX
	 * @param chunkZ
	 * @param biome
	 * @param config
	 */
	public void handleStartFactory(Start start, DynamicRegistries dynamicRegistries, ChunkGenerator chunkGen, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, VillageConfig config)
	{
		BlockPos blockpos = new BlockPos(chunkX * 16, start.jigsawStructure.deltaY, chunkZ * 16);
		JigsawManager.func_242837_a(dynamicRegistries, config, start.jigsawStructure::getPiece, chunkGen, templateManager, blockpos, start.getComponents(), start.getRand(), start.jigsawStructure.flag1, start.jigsawStructure.placesOnSurface);
		start.recalculateStructureSize();
	}

	/**
	 * @return {@link IStartFactory}
	 */
	@Internal
	public IStartFactory<VillageConfig> getStartFactory()
	{
		return (structure, chunkX, chunkZ, bounds, references, seed) -> new GelJigsawStructure.Start(this, chunkX, chunkZ, bounds, references, seed);
	}

	public static class Start extends StructureStart<VillageConfig>
	{
		public final GelJigsawStructure jigsawStructure;

		public Start(GelJigsawStructure jigsawStructure, int chunkX, int chunkZ, MutableBoundingBox bounds, int references, long seed)
		{
			super(jigsawStructure, chunkX, chunkZ, bounds, references, seed);
			this.jigsawStructure = jigsawStructure;
		}

		public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGen, TemplateManager templateManager, int chunkX, int chunkZ, Biome biome, VillageConfig config)
		{
			jigsawStructure.handleStartFactory(this, dynamicRegistries, chunkGen, templateManager, chunkX, chunkZ, biome, config);
		}

		public List<StructurePiece> getComponents()
		{
			return this.components;
		}

		public Random getRand()
		{
			return this.rand;
		}

		public MutableBoundingBox getBounds()
		{
			return this.bounds;
		}

		@Override
		public void recalculateStructureSize()
		{
			super.recalculateStructureSize();
		}
	}
}

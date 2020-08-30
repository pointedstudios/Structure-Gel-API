package com.legacy.dungeons_plus.features;

import java.util.Random;

import com.legacy.dungeons_plus.DungeonsPlus;
import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;
import com.legacy.structure_gel.worldgen.jigsaw.AbstractGelStructurePiece;
import com.legacy.structure_gel.worldgen.jigsaw.GelConfigJigsawStructure;
import com.legacy.structure_gel.worldgen.jigsaw.GelJigsawStructure;
import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.registries.ForgeRegistries;

public class EndRuinsStructure extends GelConfigJigsawStructure
{
	public EndRuinsStructure(Codec<VillageConfig> codec, StructureConfig config)
	{
		super(codec, config, 0, true, true);
	}

	@Override
	public int getSeed()
	{
		return 843152;
	}

	@Override
	public IPieceFactory getPieceType()
	{
		return Piece::new;
	}

	@Override
	public IStartFactory<VillageConfig> getStartFactory()
	{
		return (structure, chunkX, chunkZ, bounds, references, seed) -> new EndRuinsStructure.Start(this, chunkX, chunkZ, bounds, references, seed);
	}

	public static class Start extends GelJigsawStructure.Start
	{

		public Start(GelJigsawStructure jigsawStructure, int chunkX, int chunkZ, MutableBoundingBox bounds, int references, long seed)
		{
			super(jigsawStructure, chunkX, chunkZ, bounds, references, seed);
		}

		@Override
		public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biome, VillageConfig config)
		{
			int x = (chunkX * 16) + 7;
			int z = (chunkZ * 16) + 7;
			int y = generator.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG);
			if (y >= 60)
			{
				super.func_230364_a_(dynamicRegistries, generator, templateManagerIn, chunkX, chunkZ, biome, config);
				this.components.removeIf(c -> c.getBoundingBox().minY < 5);
				this.recalculateStructureSize();
			}
		}
	}

	public static class Piece extends AbstractGelStructurePiece
	{
		public Piece(TemplateManager template, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox boundingBox)
		{
			super(template, jigsawPiece, pos, groundLevelDelta, rotation, boundingBox);
		}

		public Piece(TemplateManager template, CompoundNBT nbt)
		{
			super(template, nbt);
		}

		@Override
		public IStructurePieceType getStructurePieceType()
		{
			return DungeonsPlus.Structures.END_RUINS.getPieceType();
		}

		/**
		 * Places end stone underneath the structure in case it generates with overhang,
		 * and obsidian under the pylons just in case.
		 */
		@Override
		public boolean func_237001_a_(ISeedReader seedReader, StructureManager structureManager, ChunkGenerator chunkGen, Random rand, MutableBoundingBox bounds, BlockPos pos, boolean isLegacy)
		{
			if (super.func_237001_a_(seedReader, structureManager, chunkGen, rand, bounds, pos, isLegacy))
			{
				if (this.getLocation().toString().contains("end_ruins/tower/base_"))
					this.extendDown(seedReader, Blocks.END_STONE.getDefaultState(), bounds, this.rotation, rand);
				if (this.getLocation().toString().contains("end_ruins/pylon/"))
					this.extendDown(seedReader, Blocks.OBSIDIAN.getDefaultState(), bounds, this.rotation, rand);
				return true;
			}
			return false;
		}

		@Override
		public void handleDataMarker(String key, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox bounds)
		{
			if (key.contains("spawner"))
			{
				this.setAir(worldIn, pos);
				String[] data = key.split("-");
				EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(data[1]));

				if (entityType == EntityType.PHANTOM || entityType == EntityType.ENDERMAN)
				{
					worldIn.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 3);
					if (worldIn.getTileEntity(pos) instanceof MobSpawnerTileEntity)
					{
						MobSpawnerTileEntity tile = (MobSpawnerTileEntity) worldIn.getTileEntity(pos);
						tile.getSpawnerBaseLogic().setEntityType(entityType);
					}
				}
				else
				{
					worldIn.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), 3);
				}
			}
		}
	}
}
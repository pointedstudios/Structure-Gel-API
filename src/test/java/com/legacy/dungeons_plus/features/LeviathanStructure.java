package com.legacy.dungeons_plus.features;

import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.legacy.dungeons_plus.DungeonsPlus;
import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;
import com.legacy.structure_gel.worldgen.jigsaw.AbstractGelStructurePiece;
import com.legacy.structure_gel.worldgen.jigsaw.GelConfigJigsawStructure;
import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class LeviathanStructure extends GelConfigJigsawStructure
{
	public LeviathanStructure(Codec<VillageConfig> codec, StructureConfig config)
	{
		super(codec, config, -1, true, true);
		this.setSpawnList(EntityClassification.MONSTER, ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.HUSK, 1, 4, 4)));
	}

	@Override
	public int getSeed()
	{
		return 719643;
	}
	
	@Override
	public IPieceFactory getPieceType()
	{
		return Piece::new;
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
			return DungeonsPlus.Structures.LEVIATHAN.getPieceType();
		}

		@Override
		public void handleDataMarker(String key, BlockPos pos, IServerWorld world, Random rand, MutableBoundingBox bounds)
		{
			if (key.contains("chest"))
			{
				this.setAir(world, pos);
				String[] data = key.split("-");

				world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.byName(data[1])).rotate(world, pos, this.rotation), 3);
				if (world.getTileEntity(pos) instanceof ChestTileEntity)
					((ChestTileEntity) world.getTileEntity(pos)).setLootTable(LootTables.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
			}
			if (key.equals("spawner"))
			{
				this.setAir(world, pos);

				world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 3);
				if (world.getTileEntity(pos) instanceof MobSpawnerTileEntity)
				{
					((MobSpawnerTileEntity) world.getTileEntity(pos)).getSpawnerBaseLogic().setEntityType(EntityType.HUSK);
				}
			}
		}
	}
}

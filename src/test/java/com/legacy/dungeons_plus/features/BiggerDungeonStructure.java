package com.legacy.dungeons_plus.features;

import java.util.Random;

import com.legacy.dungeons_plus.DPLoot;
import com.legacy.dungeons_plus.DungeonsPlus;
import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;
import com.legacy.structure_gel.worldgen.jigsaw.AbstractGelStructurePiece;
import com.legacy.structure_gel.worldgen.jigsaw.GelConfigJigsawStructure;
import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager.IPieceFactory;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.registries.ForgeRegistries;

public class BiggerDungeonStructure extends GelConfigJigsawStructure
{
	public BiggerDungeonStructure(Codec<VillageConfig> codec, StructureConfig config)
	{
		super(codec, config, 0, true, true);
	}

	@Override
	public int getSeed()
	{
		return 973181;
	}

	@Override
	public IPieceFactory getPieceType()
	{
		return Piece::new;
	}

	public static final class Piece extends AbstractGelStructurePiece
	{
		public Piece(TemplateManager templateManager, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox bounds)
		{
			super(templateManager, jigsawPiece, pos, groundLevelDelta, rotation, bounds);
		}

		public Piece(TemplateManager templateManager, CompoundNBT nbt)
		{
			super(templateManager, nbt);
		}

		@Override
		public IStructurePieceType getStructurePieceType()
		{
			return DungeonsPlus.Structures.BIGGER_DUNGEON.getPieceType();
		}
		
		@Override
		public void handleDataMarker(String key, BlockPos pos, IServerWorld world, Random rand, MutableBoundingBox bounds)
		{
			if (key.contains("chest"))
			{
				this.setAir(world, pos);
				String[] data = key.split("-");

				/**
				 * Generates the chests with a 50% chance, or if they are a map chest.
				 */
				if (rand.nextBoolean() || data[0].contains("map"))
				{
					world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.byName(data[1])).rotate(world, pos, this.rotation), 3);
					if (world.getTileEntity(pos) instanceof ChestTileEntity)
						if (data[0].contains("huskmap"))
							((ChestTileEntity) world.getTileEntity(pos)).setLootTable(DPLoot.DUNGEON_LOOT_HUSK, rand.nextLong());
						else if (data[0].contains("straymap"))
							((ChestTileEntity) world.getTileEntity(pos)).setLootTable(DPLoot.DUNGEON_LOOT_STRAY, rand.nextLong());
						else
							((ChestTileEntity) world.getTileEntity(pos)).setLootTable(LootTables.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
				}
			}
			if (key.contains("spawner"))
			{
				this.setAir(world, pos);
				String[] data = key.split("-");

				world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 3);
				if (world.getTileEntity(pos) instanceof MobSpawnerTileEntity)
					((MobSpawnerTileEntity) world.getTileEntity(pos)).getSpawnerBaseLogic().setEntityType(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(data[1])));

			}
		}
	}
}

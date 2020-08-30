package com.legacy.dungeons_plus.features;

import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.legacy.dungeons_plus.DPLoot;
import com.legacy.dungeons_plus.DungeonsPlus;
import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;
import com.legacy.structure_gel.worldgen.jigsaw.AbstractGelStructurePiece;
import com.legacy.structure_gel.worldgen.jigsaw.GelConfigJigsawStructure;
import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.ChestType;
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

public class TowerStructure extends GelConfigJigsawStructure
{
	public TowerStructure(Codec<VillageConfig> codec, StructureConfig config)
	{
		super(codec, config, 0, true, true);
	}

	@Override
	public int getSeed()
	{
		return 155166;
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
			return DungeonsPlus.Structures.TOWER.getPieceType();
		}
		
		@Override
		public void handleDataMarker(String key, BlockPos pos, IServerWorld world, Random rand, MutableBoundingBox bounds)
		{
			/**
			 * Using flag 2 because I don't want block updates for this. If the chest
			 * updates, double chests might not connect.
			 */
			if (key.contains("chest"))
			{
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				String[] data = key.split("-");

				Direction facing = Direction.byName(data[1]);
				ChestType chestType = data[2].equals(ChestType.LEFT.getString()) ? ChestType.LEFT : (data[2].equals(ChestType.RIGHT.getString()) ? ChestType.RIGHT : ChestType.SINGLE);

				world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, facing).with(ChestBlock.TYPE, chestType).rotate(world, pos, this.rotation), 3);
				if (world.getTileEntity(pos) instanceof ChestTileEntity)
				{
					if (data[0].contains("map"))
						((ChestTileEntity) world.getTileEntity(pos)).setLootTable(DPLoot.TOWER_LOOT_MAP, rand.nextLong());
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
			/**
			 * Creating entities is a little simpler with the createEntity method. Doing
			 * this will automatically create the entity and set it's position and rotation
			 * based on the structure.
			 * 
			 * Entities are spawned facing south by default with the rotation argument being
			 * the rotation of the structure to offset them. Do Rotation.add to the rotation
			 * value passed in to rotate it according to how yours needs to be facing.
			 */
			if (key.equals("armor_stand"))
			{
				setAir(world, pos);

				ArmorStandEntity entity = createEntity(EntityType.ARMOR_STAND, world, pos, this.rotation);
				entity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));

				for (Item item : ImmutableList.of(Items.GOLDEN_HELMET, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS))
					if (rand.nextFloat() < 0.25)
						entity.setItemStackToSlot(MobEntity.getSlotForItemStack(new ItemStack(item)), new ItemStack(item));

				world.addEntity(entity);
			}
		}
	}
}

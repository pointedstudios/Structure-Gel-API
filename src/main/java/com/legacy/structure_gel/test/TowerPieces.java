package com.legacy.structure_gel.test;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.structures.GelStructurePiece;
import com.legacy.structure_gel.structures.jigsaw.JigsawPoolBuilder;
import com.legacy.structure_gel.structures.jigsaw.JigsawRegistryHelper;
import com.legacy.structure_gel.structures.processors.RandomBlockSwapProcessor;

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
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.RandomBlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleStructureProcessor;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.registries.ForgeRegistries;

public class TowerPieces
{
	public static void assemble(ChunkGenerator chunkGen, TemplateManager template, BlockPos pos, List<StructurePiece> pieces, SharedSeedRandom seed)
	{
		JigsawManager.func_236823_a_(new ResourceLocation("dungeons_plus", "tower/root"), 7, TowerPieces.Piece::new, chunkGen, template, pos, pieces, seed, true, true);
	}

	public static void init()
	{
	}

	static
	{
		/**
		 * This is a processor from Structure Gel API for making a single block swap. It
		 * exists as a shorthand way of writing the processor below it for a simple
		 * swap.
		 */
		RandomBlockSwapProcessor cobbleToMossy = new RandomBlockSwapProcessor(Blocks.COBBLESTONE, 0.2F, Blocks.MOSSY_COBBLESTONE.getDefaultState());
		RandomBlockSwapProcessor goldDecay = new RandomBlockSwapProcessor(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR.getDefaultState());

		//@formatter:off
		RuleStructureProcessor brickDecay = new RuleStructureProcessor(ImmutableList.of(
				new RuleEntry(new RandomBlockMatchRuleTest(Blocks.STONE_BRICKS, 0.15F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
				new RuleEntry(new RandomBlockMatchRuleTest(Blocks.STONE_BRICKS, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_STONE_BRICKS.getDefaultState())));
		//@formatter:on

		/**
		 * This registry is used in combination with JigsawPoolBuilders. The prefix
		 * ("tower/") is optional, but allows you to cut down some typing since your
		 * resource locations may need to start with it.
		 * 
		 * If you need to change the prefix, you can use .setPrefix on the registry to
		 * create a clone of it.
		 * 
		 * In the future, any string input using this registry will become a
		 * ResourceLocation with this format. modid:prefixtext ->
		 * dungeons_plus:tower/text
		 */
		JigsawRegistryHelper registry = new JigsawRegistryHelper("dungeons_plus", "tower/");

		/**
		 * The JigsawRegistryHelper.register method takes a string for the name of the
		 * pool. This name takes the set modid and prefix into account. In this case,
		 * it'll end up being "structure_gel_demo:tower/root". This is the target pool
		 * in your jigsaw blocks. You could also just use a ResourceLocation directly
		 * instead of letting it fill in the repeated mod id and prefix.
		 *
		 * The second value is the JigsawPoolBuilder's built List<Pair<JigsawPiece,
		 * Integer>>. The "names" method tells the pool what options it has for
		 * structures to generate. These all take the mod id and prefix into account
		 * just like before. You can use ResourceLocations to handle the mod id and
		 * prefix yourself. You can also input a Map<String, Integer> to set weights.
		 *
		 * ...names(ImmutableMap.of("root", 1, "root_2", 9)) would generate "root_2" 90%
		 * of the time.
		 * 
		 * build() takes everything in the builder and compiles it into the List that
		 * the registry needs. Any settings like processors will take effect for all
		 * pieces passed in. This is done in the next line.
		 */
		registry.register("root", registry.builder().names("root").build());

		/**
		 * Here we have an example of using more than one structure in the names()
		 * method. Since they are simply written in a list, they will all have the same
		 * weight.
		 * 
		 * This registry is told to not maintain water and to use the structure
		 * processors (cobbleToMossy and brickDecay) defined above.
		 */
		registry.register("floor", registry.builder().names("floor_spider", "floor_zombie", "floor_skeleton").maintainWater(false).processors(cobbleToMossy, brickDecay).build());

		registry.register("floor_vex", registry.builder().names("floor_vex").maintainWater(false).processors(cobbleToMossy, brickDecay).build());

		/**
		 * Just another way to get the JigsawPoolBuilder if you like doing things like
		 * this.
		 */
		registry.register("base", new JigsawPoolBuilder(registry).names("base").maintainWater(false).processors(cobbleToMossy, brickDecay).build());

		/**
		 * Creating a JigsawPoolBuilder instance beforehand with shared settings that'll
		 * be used in the "top" pool.
		 */
		JigsawPoolBuilder topBuilder = registry.builder().maintainWater(false).processors(cobbleToMossy);

		/**
		 * In this case, I'm using the JigsawPoolBuilder.collect to merge two different
		 * pool builders into one. Since both "top_full" and "top_decay" will use the
		 * same basic settings, but with "top_decay" having a gold decay processor, I
		 * use a copy of the JigsawPoolBuilder instance created beforehand. With these
		 * copies, I can adjust the settings for each builder while keeping commmon
		 * settings the same.
		 * 
		 * It's worth noting that processors() functions as an append, meaning that the
		 * "top_decay" structure will have both cobbleToMossy and goldDecay.
		 * 
		 * The weights of each structure set in names() are preserved, so set them as
		 * you normally would.
		 */
		registry.register("top", JigsawPoolBuilder.collect(topBuilder.clone().names(ImmutableMap.of("top_full", 1)), topBuilder.clone().names(ImmutableMap.of("top_decay", 4)).processors(goldDecay)));

	}

	public static class Piece extends GelStructurePiece
	{
		public Piece(TemplateManager template, JigsawPiece jigsawPiece, BlockPos pos, int groundLevelDelta, Rotation rotation, MutableBoundingBox boundingBox)
		{
			super(StructureGelMod.StructureRegistry.testPiece, template, jigsawPiece, pos, groundLevelDelta, rotation, boundingBox);
		}

		public Piece(TemplateManager template, CompoundNBT nbt)
		{
			super(template, nbt, StructureGelMod.StructureRegistry.testPiece);
		}

		/**
		 * When placing a tile entity in place of the structure block, you first need to
		 * set the space to air. If you don't, the chest you place won't be a chest tile
		 * entity. setAir is a simple shortcut to do this.
		 * 
		 * The value I enter into the structure block is formatted with arguments
		 * separated by "-". So in these cases, I use formats like "chest-west-left" and
		 * "spawner-minecraft:vex". Using String.split, I can issolate these values.
		 */
		@Override
		public void handleDataMarker(String key, IWorld worldIn, BlockPos pos, Random rand, MutableBoundingBox bounds)
		{
			if (key.contains("chest"))
			{
				/**
				 * Using flag 2 because I don't want block updates for this. If the chest
				 * updates, double chests might not connect.
				 */
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				String[] data = key.split("-");

				Direction facing = Direction.byName(data[1]);
				ChestType chestType = data[2].equals(ChestType.LEFT.func_176610_l()) ? ChestType.LEFT : (data[2].equals(ChestType.RIGHT.func_176610_l()) ? ChestType.RIGHT : ChestType.SINGLE);

				worldIn.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, facing).with(ChestBlock.TYPE, chestType).rotate(this.rotation), 3);
				if (worldIn.getTileEntity(pos) instanceof ChestTileEntity)
				{
					((ChestTileEntity) worldIn.getTileEntity(pos)).setLootTable(LootTables.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
				}
			}
			if (key.contains("spawner"))
			{
				this.setAir(worldIn, pos);
				String[] data = key.split("-");

				worldIn.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 3);
				if (worldIn.getTileEntity(pos) instanceof MobSpawnerTileEntity)
					((MobSpawnerTileEntity) worldIn.getTileEntity(pos)).getSpawnerBaseLogic().setEntityType(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(data[1])));

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
				setAir(worldIn, pos);

				ArmorStandEntity entity = createEntity(EntityType.ARMOR_STAND, worldIn, pos, this.rotation);
				entity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));

				for (Item item : ImmutableList.of(Items.GOLDEN_HELMET, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS))
					if (rand.nextFloat() < 0.25)
						entity.setItemStackToSlot(MobEntity.getSlotForItemStack(new ItemStack(item)), new ItemStack(item));

				worldIn.addEntity(entity);
			}
		}
	}
}

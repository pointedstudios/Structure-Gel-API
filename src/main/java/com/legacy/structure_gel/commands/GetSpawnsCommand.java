package com.legacy.structure_gel.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;

public class GetSpawnsCommand
{
	public static LiteralArgumentBuilder<CommandSource> get()
	{
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("getspawns").executes(context -> getSpawns(context, EntityClassification.values()));

		for (EntityClassification classification : EntityClassification.values())
			command.then(Commands.literal(classification.getName()).executes(context -> getSpawns(context, classification)));

		return command;
	}

	private static int getSpawns(CommandContext<CommandSource> context, EntityClassification... classifications)
	{
		Map<EntityClassification, List<MobSpawnInfo.Spawners>> map = new LinkedHashMap<>();
		for (EntityClassification classification : classifications)
		{
			List<MobSpawnInfo.Spawners> list = getSpawnList(classification, context);
			if (!list.isEmpty())
				map.put(classification, list);
		}
		ServerPlayerEntity player = ((ServerPlayerEntity) context.getSource().getEntity());
		if (!map.isEmpty())
		{
			player.sendMessage(new StringTextComponent("--Spawn Data--"), Util.DUMMY_UUID);
			map.forEach((classification, list) -> printSpawns(classification, list, context));
		}
		else if (context.getSource().getEntity() instanceof ServerPlayerEntity)
			player.sendMessage(new StringTextComponent("No spawn data."), Util.DUMMY_UUID);

		return 1;
	}

	private static List<MobSpawnInfo.Spawners> getSpawnList(EntityClassification classification, CommandContext<CommandSource> context)
	{
		ServerWorld world = context.getSource().getWorld();
		BlockPos pos = new BlockPos(context.getSource().getPos());
		Biome biome = world.getBiome(pos);
		ChunkGenerator chunkGen = world.getChunkProvider().generator;
		StructureManager manager = world.func_241112_a_();
		List<MobSpawnInfo.Spawners> list = chunkGen.func_230353_a_(biome, manager, classification, pos);
		return net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(world, classification, pos, list);
	}

	private static void printSpawns(EntityClassification classification, List<MobSpawnInfo.Spawners> spawns, CommandContext<CommandSource> context)
	{
		if (context.getSource().getEntity() instanceof ServerPlayerEntity)
		{
			if (!spawns.isEmpty())
			{
				ServerPlayerEntity player = ((ServerPlayerEntity) context.getSource().getEntity());
				player.sendMessage(new StringTextComponent("[" + classification.getName() + "]").mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
				spawns.forEach(spawn -> player.sendMessage(new StringTextComponent(String.format(" - %s, weight:%d, min:%d, max:%d", spawn.type.getRegistryName(), spawn.itemWeight, spawn.minCount, spawn.maxCount)), Util.DUMMY_UUID));
			}
		}
	}
}

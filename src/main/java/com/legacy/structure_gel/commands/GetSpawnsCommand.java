package com.legacy.structure_gel.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;

public class GetSpawnsCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("getspawns").requires(source -> source.hasPermissionLevel(2)).executes(context -> getSpawns(context));

		for (EntityClassification classification : EntityClassification.values())
			command.then(Commands.literal(classification.getName()).executes(context -> getSpawns(classification, context)));

		dispatcher.register(command);
	}

	private static int getSpawns(CommandContext<CommandSource> context)
	{
		Map<EntityClassification, List<SpawnListEntry>> map = new LinkedHashMap<>();
		for (EntityClassification classification : EntityClassification.values())
		{
			ServerWorld world = context.getSource().getWorld();
			BlockPos pos = new BlockPos(context.getSource().getPos());
			Biome biome = world.getBiome(pos);
			ChunkGenerator chunkGen = world.getChunkProvider().generator;
			StructureManager manager = world.func_241112_a_();
			List<SpawnListEntry> list = chunkGen.func_230353_a_(biome, manager, classification, pos);
			list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(world, classification, pos, list);
			if (!list.isEmpty())
				map.put(classification, list);
		}
		if (!map.isEmpty())
			map.forEach((classification, list) -> printSpawns(classification, list, context));
		else if (context.getSource().getEntity() instanceof ServerPlayerEntity)
			((ServerPlayerEntity) context.getSource().getEntity()).sendMessage(new StringTextComponent("No spawn data for any type."), Util.field_240973_b_);

		return 1;
	}

	private static int getSpawns(EntityClassification classification, CommandContext<CommandSource> context)
	{
		ServerWorld world = context.getSource().getWorld();
		BlockPos pos = new BlockPos(context.getSource().getPos());
		Biome biome = world.getBiome(pos);
		ChunkGenerator chunkGen = world.getChunkProvider().generator;
		StructureManager manager = world.func_241112_a_();
		List<SpawnListEntry> list = chunkGen.func_230353_a_(biome, manager, classification, pos);
		list = net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(world, classification, pos, list);
		if (!list.isEmpty())
			printSpawns(classification, list, context);
		else if (context.getSource().getEntity() instanceof ServerPlayerEntity)
			((ServerPlayerEntity) context.getSource().getEntity()).sendMessage(new StringTextComponent("No spawn data for " + classification.getName() + "."), Util.field_240973_b_);
		return 1;
	}

	private static void printSpawns(EntityClassification classification, List<SpawnListEntry> spawns, CommandContext<CommandSource> context)
	{
		if (context.getSource().getEntity() instanceof ServerPlayerEntity)
		{
			if (!spawns.isEmpty())
			{
				ServerPlayerEntity player = ((ServerPlayerEntity) context.getSource().getEntity());
				player.sendMessage(new StringTextComponent("[" + classification.getName() + "]").func_240699_a_(TextFormatting.GREEN), Util.field_240973_b_);
				spawns.forEach(spawn -> player.sendMessage(new StringTextComponent(String.format(" - %s, weight:%d, min:%d, max:%d", spawn.entityType.getRegistryName(), spawn.itemWeight, spawn.minGroupCount, spawn.maxGroupCount)), Util.field_240973_b_));
			}
		}
	}
}

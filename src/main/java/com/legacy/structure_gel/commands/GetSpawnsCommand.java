package com.legacy.structure_gel.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		if (!map.isEmpty())
		{
			context.getSource().sendFeedback(new StringTextComponent("--Spawn Data--"), true);
			map.entrySet().stream().sorted(new ClassificationComparator<>()).forEach(e -> printSpawns(e.getKey(), e.getValue(), context));
		}
		context.getSource().sendFeedback(new StringTextComponent("No spawn data."), true);

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
		return ForgeEventFactory.getPotentialSpawns(world, classification, pos, list);
	}

	private static void printSpawns(EntityClassification classification, List<MobSpawnInfo.Spawners> spawns, CommandContext<CommandSource> context)
	{
		if (!spawns.isEmpty())
		{
			context.getSource().sendFeedback(new StringTextComponent("[" + classification.getName() + "]").mergeStyle(TextFormatting.GREEN), true);
			spawns.stream().sorted(new SpawnInfoComparator<>()).forEach(spawn -> context.getSource().sendFeedback(new StringTextComponent(String.format(" - %s, weight:%d, min:%d, max:%d", spawn.type.getRegistryName(), spawn.itemWeight, spawn.minCount, spawn.maxCount)), true));
		}
	}

	private static class SpawnInfoComparator<T extends MobSpawnInfo.Spawners> implements Comparator<T>
	{
		@Override
		public int compare(T o1, T o2)
		{
			return o1.type.getRegistryName().toString().compareTo(o2.type.getRegistryName().toString());
		}
	}

	private static class ClassificationComparator<T extends Map.Entry<EntityClassification, List<MobSpawnInfo.Spawners>>> implements Comparator<T>
	{
		@Override
		public int compare(T o1, T o2)
		{
			return o1.getKey().getName().compareTo(o2.getKey().getName());
		}
	}
}

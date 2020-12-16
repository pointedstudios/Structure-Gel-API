package com.legacy.structure_gel.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class StructureGelCommand
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("structure_gel");

		command.then(SaveStructuresCommand.get());
		command.then(BiomeDictCommand.get());
		command.then(GetSpawnsCommand.get());

		dispatcher.register(command);
	}
}

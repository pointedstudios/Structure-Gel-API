package com.legacy.structure_gel.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.legacy.structure_gel.StructureGelMod;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;

public class SaveStructuresCommand
{
	public static LiteralArgumentBuilder<CommandSource> get()
	{
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("savestructures").requires(source -> source.hasPermissionLevel(2));

		command.then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).executes(context -> saveStructures(context, new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to"))))));

		return command;
	}

	private static int saveStructures(CommandContext<CommandSource> context, MutableBoundingBox area)
	{
		ServerWorld world = context.getSource().getWorld();
		List<String> savedStructures = new ArrayList<>();
		Map<BlockPos, String> duplicates = new HashMap<>();
		for (BlockPos pos : BlockPos.getAllInBoxMutable(area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ))
		{
			if (world.getTileEntity(pos) instanceof StructureBlockTileEntity)
			{
				StructureBlockTileEntity tile = (StructureBlockTileEntity) world.getTileEntity(pos);
				if (tile.hasName() && tile.save())
				{
					if (savedStructures.contains(tile.getName()))
						duplicates.put(new BlockPos(pos), tile.getName());
					savedStructures.add(tile.getName());
				}
			}
		}
		if (savedStructures.size() > 0)
		{
			if (context.getSource().getEntity() instanceof ServerPlayerEntity)
			{
				ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
				player.sendMessage(new StringTextComponent("[Saved " + savedStructures.size() + " Structures]").mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
				if (savedStructures.size() <= 50)
					savedStructures.forEach(structure -> player.sendMessage(new StringTextComponent(" - " + structure), Util.DUMMY_UUID));
				else
				{
					player.sendMessage(new StringTextComponent(" - Too many structures to print. Check the console."), Util.DUMMY_UUID);
					StructureGelMod.LOGGER.info("Saved structures:");
					savedStructures.forEach(StructureGelMod.LOGGER::info);
				}
				if (duplicates.size() > 0)
				{
					player.sendMessage(new StringTextComponent("Warning: Found " + duplicates.size() + " structures with a duplicate name. Click to teleport.").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
					player.sendMessage(new StringTextComponent("[Duplicate Structures]").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
					if (duplicates.size() <= 50)
						duplicates.forEach((pos, structure) -> player.sendMessage(new StringTextComponent(String.format("%s at (%d, %d, %d)", structure, pos.getX(), pos.getY(), pos.getZ())).modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))), Util.DUMMY_UUID));
					else
					{
						player.sendMessage(new StringTextComponent(" - Too many duplicates to print. Check the console."), Util.DUMMY_UUID);
						StructureGelMod.LOGGER.info("Duplicate structures:");
						duplicates.forEach((pos, structure) -> StructureGelMod.LOGGER.info(String.format("%s /tp @s %d %d %d", structure, pos.getX(), pos.getY(), pos.getZ())));
					}
				}
			}
			return savedStructures.size();
		}
		ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
		player.sendMessage(new StringTextComponent("No structures were saved."), Util.DUMMY_UUID);
		return 0;
	}
}
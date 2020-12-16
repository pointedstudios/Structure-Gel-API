package com.legacy.structure_gel.commands;

import com.legacy.structure_gel.StructureGelMod;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			context.getSource().sendFeedback(new StringTextComponent("[Saved " + savedStructures.size() + " Structures]").mergeStyle(TextFormatting.GREEN), true);
			if (savedStructures.size() <= 50)
				savedStructures.stream().sorted().forEach(structure -> context.getSource().sendFeedback(new StringTextComponent(" - " + structure), true));
			else
			{
				context.getSource().sendFeedback(new StringTextComponent(" - Too many structures to print. Check the console."), true);
				StructureGelMod.LOGGER.info("Saved structures:");
				savedStructures.stream().sorted().forEach(StructureGelMod.LOGGER::info);
			}
			if (duplicates.size() > 0)
			{
				context.getSource().sendFeedback(new StringTextComponent("Warning: Found " + duplicates.size() + " structures with a duplicate name. Click to teleport.").mergeStyle(TextFormatting.RED), true);
				context.getSource().sendFeedback(new StringTextComponent("[Duplicate Structures]").mergeStyle(TextFormatting.RED), true);
				if (duplicates.size() <= 50)
					duplicates.forEach((pos, structure) -> context.getSource().sendFeedback(new StringTextComponent(String.format("%s at (%d, %d, %d)", structure, pos.getX(), pos.getY(), pos.getZ())).modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))), true));
				else
				{
					context.getSource().sendFeedback(new StringTextComponent(" - Too many duplicates to print. Check the console."), true);
					StructureGelMod.LOGGER.info("Duplicate structures:");
					duplicates.forEach((pos, structure) -> StructureGelMod.LOGGER.info(String.format("%s /tp @s %d %d %d", structure, pos.getX(), pos.getY(), pos.getZ())));
				}
			}
			return savedStructures.size();
		}

		context.getSource().sendFeedback(new StringTextComponent("No structures were saved."), true);
		return 0;
	}
}

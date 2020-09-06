package com.legacy.structure_gel.commands;

import java.util.Optional;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.biome_dictionary.BiomeDictionary;
import com.legacy.structure_gel.util.RegistryHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeDictCommand
{
	public static final SuggestionProvider<CommandSource> BIOME_DICTIONARY_ENTRIES = SuggestionProviders.register(StructureGelMod.locate("biome_dictionary_entries"), (context, builder) ->
	{
		return ISuggestionProvider.suggestIterable(BiomeDictionary.REGISTRY.getKeys(), builder);
	});

	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("biomedict");

		command.then(Commands.literal("gettypes").executes(BiomeDictCommand::getTypes).then(Commands.argument("biome", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.field_239574_d_).executes(context -> getTypes(context, context.getArgument("biome", ResourceLocation.class)))));
		command.then(Commands.literal("getbiomes").then(Commands.argument("dictionaryentry", ResourceLocationArgument.resourceLocation()).suggests(BIOME_DICTIONARY_ENTRIES).executes(context -> getBiomes(context, context.getArgument("dictionaryentry", ResourceLocation.class)))));

		dispatcher.register(command);
	}

	private static int getTypes(CommandContext<CommandSource> context)
	{
		World world = context.getSource().getWorld();
		Optional<RegistryKey<Biome>> biome = RegistryHelper.getKey(world, Registry.BIOME_KEY, world.getBiome(new BlockPos(context.getSource().getPos())));
		if (biome.isPresent())
		{
			return getTypes(context, biome.get().func_240901_a_());
		}
		return 0;
	}
	
	private static int getTypes(CommandContext<CommandSource> context, ResourceLocation key)
	{
		if (context.getSource().getEntity() instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
			player.sendMessage(new StringTextComponent("[" + key.toString() + "]").mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
			BiomeDictionary.getAllTypes(RegistryKey.func_240903_a_(Registry.BIOME_KEY, key)).forEach(t -> player.sendMessage(new StringTextComponent(" - " + t.getRegistryName().toString()), Util.DUMMY_UUID));
		}
		return 1;
	}

	private static int getBiomes(CommandContext<CommandSource> context, ResourceLocation key)
	{
		if (context.getSource().getEntity() instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntity();
			player.sendMessage(new StringTextComponent("[" + key.toString() + "]").mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
			BiomeDictionary.get(key).getAllBiomes().forEach(b -> player.sendMessage(new StringTextComponent(" - " + b.func_240901_a_().toString()), Util.DUMMY_UUID));
		}
		return 1;
	}
}

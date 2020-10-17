package com.legacy.structure_gel.commands;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.biome_dictionary.BiomeDictionary;
import com.legacy.structure_gel.util.RegistryHelper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
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

	public static LiteralArgumentBuilder<CommandSource> get()
	{
		LiteralArgumentBuilder<CommandSource> command = Commands.literal("biomedict");

		command.then(Commands.literal("gettypes").executes(BiomeDictCommand::getTypes).then(Commands.argument("biome", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.field_239574_d_).executes(context -> getTypes(context, context.getArgument("biome", ResourceLocation.class)))));
		command.then(Commands.literal("getbiomes").then(Commands.argument("dictionaryentry", ResourceLocationArgument.resourceLocation()).suggests(BIOME_DICTIONARY_ENTRIES).executes(context -> getBiomes(context, context.getArgument("dictionaryentry", ResourceLocation.class)))));

		return command;
	}

	private static int getTypes(CommandContext<CommandSource> context)
	{
		World world = context.getSource().getWorld();
		Optional<RegistryKey<Biome>> biome = RegistryHelper.getKey(world, Registry.BIOME_KEY, world.getBiome(new BlockPos(context.getSource().getPos())));
		if (biome.isPresent())
			return getTypes(context, biome.get().getLocation());

		return 0;
	}

	private static int getTypes(CommandContext<CommandSource> context, ResourceLocation key)
	{
		context.getSource().sendFeedback(new StringTextComponent("[" + key.toString() + "]").mergeStyle(TextFormatting.GREEN), true);
		List<String> types = BiomeDictionary.getAllTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, key)).stream().map(b -> b.getRegistryName().toString()).sorted().collect(Collectors.toList());
		if (types.isEmpty())
			context.getSource().sendFeedback(new StringTextComponent(key.toString() + " has no registered types."), true);
		else
			types.forEach(t -> context.getSource().sendFeedback(new StringTextComponent(" - " + t), true));

		return 1;
	}

	private static int getBiomes(CommandContext<CommandSource> context, ResourceLocation key)
	{
		context.getSource().sendFeedback(new StringTextComponent("[" + key.toString() + "]").mergeStyle(TextFormatting.GREEN), true);
		List<String> biomes = BiomeDictionary.get(key).getAllBiomes().stream().map(rk -> rk.getLocation().toString()).sorted().collect(Collectors.toList());
		if (biomes.isEmpty())
			context.getSource().sendFeedback(new StringTextComponent(key.toString() + " has no registered biomes."), true);
		else
			biomes.forEach(b -> context.getSource().sendFeedback(new StringTextComponent(" - " + b), true));

		return 1;
	}
}

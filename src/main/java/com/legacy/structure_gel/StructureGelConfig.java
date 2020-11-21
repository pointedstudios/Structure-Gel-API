package com.legacy.structure_gel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.legacy.structure_gel.util.Internal;

import net.minecraftforge.common.ForgeConfigSpec;

@Internal
public class StructureGelConfig
{
	public static final Common COMMON;
	public static final Client CLIENT;
	protected static final ForgeConfigSpec COMMON_SPEC;
	protected static final ForgeConfigSpec CLIENT_SPEC;
	static
	{
		Pair<Common, ForgeConfigSpec> specPairCommon = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPairCommon.getRight();
		COMMON = specPairCommon.getLeft();

		Pair<Client, ForgeConfigSpec> specPairClient = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = specPairClient.getRight();
		CLIENT = specPairClient.getLeft();
	}

	public static class Common
	{
		private final ForgeConfigSpec.BooleanValue extraLakeProofing;
		private final ForgeConfigSpec.ConfigValue<String> ignoredMods;
		private final ForgeConfigSpec.BooleanValue guessBiomeDict;
		private final ForgeConfigSpec.BooleanValue exceedFillLimit;

		//public final com.legacy.structure_gel.util.ConfigTemplates.StructureConfig structureConfig;

		public Common(ForgeConfigSpec.Builder builder)
		{
			this.extraLakeProofing = builder.comment("Adds more vanilla structures to the list of structures that lakes cannot generate inside of. Only villages when set to false. Requires reload.").define("features.extra_lake_proofing", true);
			this.ignoredMods = builder.comment("A list of mod IDs that will be ignored when the biome dictionary attempts to register unregistered biomes. Mod IDs must be comma separated (\"biomesoplenty, byg, blue_skies\").").define("biome_dictionary.ignored_mods", "");
			this.guessBiomeDict = builder.comment("Determines if the biome dictionary make assumptions for unregistered biomes. Setting this to false can fix issues where a structure generates in the wrong dimension.").define("biome_dictionary.make_best_guess", true);
			this.exceedFillLimit = builder.comment("When true, removes the size limit from the fill and clone commands.").define("commands.exceed_fill_clone_limit", true);

			//this.structureConfig = new com.legacy.structure_gel.util.ConfigTemplates.StructureConfig(builder, "test_structure", 1.0, 20, 0).biomes(true, "#structure_gel:end");
		}

		public boolean getExtraLakeProofing()
		{
			return this.extraLakeProofing.get();
		}

		public boolean shouldGuessBiomeDict()
		{
			return this.guessBiomeDict.get();
		}

		public List<String> getIgnoredMods()
		{
			return Arrays.stream(ignoredMods.get().split(",")).map(String::trim).collect(Collectors.toList());
		}

		public boolean shouldExceedFillLimit()
		{
			return this.exceedFillLimit.get();
		}
	}

	public static class Client
	{
		private final ForgeConfigSpec.BooleanValue skipExperimentalBackupScreen;
		private final ForgeConfigSpec.BooleanValue fixSpawners;
		private final ForgeConfigSpec.BooleanValue showStructureBlockInfo;

		public Client(ForgeConfigSpec.Builder builder)
		{
			this.skipExperimentalBackupScreen = builder.comment("Skips the screen that tells you that a world uses experimental settings.").define("gui.skip_experimental_backup_screen", true);
			this.fixSpawners = builder.comment("Fixes spawners causing fps lag from a ClassCastException with some entities.").define("entity.fix_spawners", true);
			this.showStructureBlockInfo = builder.comment("Displays info on top of Structure Blocks, similarly to pre-1.13 versions.").define("gui.show_structure_block_info", true);
		}

		public boolean skipExperimentalScreen()
		{
			return this.skipExperimentalBackupScreen.get();
		}

		public boolean fixSpawners()
		{
			return this.fixSpawners.get();
		}

		public boolean showStructureBlockInfo()
		{
			return this.showStructureBlockInfo.get();
		}
	}
}

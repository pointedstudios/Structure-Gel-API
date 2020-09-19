package com.legacy.structure_gel;

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
		public final com.legacy.structure_gel.util.ConfigTemplates.StructureConfig structureConfig;
		
		public Common(ForgeConfigSpec.Builder builder)
		{
			this.extraLakeProofing = builder.comment("Adds more vanilla structures to the list of structures that lakes cannot generate inside of. Only villages when set to false. Requires reload.").define("features.extra_lake_proofing", true);
			this.structureConfig = new com.legacy.structure_gel.util.ConfigTemplates.StructureConfig(builder, "test_structure", 1.0, 20, 0).biomes(true, "#structure_gel:end");
		}
		
		public boolean getExtraLakeProofing()
		{
			return this.extraLakeProofing.get();
		}
	}

	public static class Client
	{
		private final ForgeConfigSpec.BooleanValue skipExperimentalBackupScreen;

		public Client(ForgeConfigSpec.Builder builder)
		{
			this.skipExperimentalBackupScreen = builder.comment("Skips the screen that tells you that a world uses experimental settings.").define("gui.skip_experimental_backup_screen", true);
		}

		public boolean skipExperimentalScreen()
		{
			return this.skipExperimentalBackupScreen.get();
		}
	}
}

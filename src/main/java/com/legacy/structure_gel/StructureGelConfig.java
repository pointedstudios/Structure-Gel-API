package com.legacy.structure_gel;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class StructureGelConfig
{
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	static
	{
		Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Common
	{
		public final ForgeConfigSpec.BooleanValue extraLakeProofing, debugMode;

		public Common(ForgeConfigSpec.Builder builder)
		{
			this.extraLakeProofing = builder.comment("Adds more vanilla structures to the list of structures that lakes cannot generate inside of. Only villages when set to false. Requires reload.").define("features.extra_lake_proofing", true);
			this.debugMode = builder.comment("Outputs debug text to the console and gives some items special debugging utilities.").define("dev.debug_mode", false);
		}
	}
	
	public static boolean getExtraLakeProofing()
	{
		return COMMON.extraLakeProofing.get();
	}
	
	public static boolean isDebugMode()
	{
		return COMMON.debugMode.get();
	}
}

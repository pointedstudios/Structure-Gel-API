package com.legacy.structure_gel;

import org.apache.commons.lang3.tuple.Pair;

import com.legacy.structure_gel.util.Internal;

import net.minecraftforge.common.ForgeConfigSpec;

@Internal
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
		private final ForgeConfigSpec.BooleanValue extraLakeProofing;

		public Common(ForgeConfigSpec.Builder builder)
		{
			this.extraLakeProofing = builder.comment("Adds more vanilla structures to the list of structures that lakes cannot generate inside of. Only villages when set to false. Requires reload.").define("features.extra_lake_proofing", true);
		}

		public boolean getExtraLakeProofing()
		{
			return this.extraLakeProofing.get();
		}
	}
}

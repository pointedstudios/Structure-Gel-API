package com.legacy.structure_gel;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.legacy.structure_gel.util.ConfigTemplates;

import net.minecraft.entity.EntityClassification;
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
		public final ForgeConfigSpec.BooleanValue extraLakeProofing;

		public Common(ForgeConfigSpec.Builder builder)
		{
			this.extraLakeProofing = builder.comment("Adds more vanilla structures to the list of structures that lakes cannot generate inside of. Only villages when set to false. Requires reload.").define("features.extra_lake_proofing", true);
			new ConfigTemplates.StructureConfigBuilder(builder, "test").probability(1.0).spacing(4).offset(2).biomes(true, "plains, forest").spawns(ImmutableMap.of(EntityClassification.MONSTER, "[minecraft:zombie, 1, 2, 3]", EntityClassification.AMBIENT, "[minecraft:bat, 3, 4, 5]"));
			new ConfigTemplates.StructureConfigBuilder(builder, "example").spacing(5);
		}

		public boolean getExtraLakeProofing()
		{
			return this.extraLakeProofing.get();
		}
	}
}

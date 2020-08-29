package com.legacy.structure_gel.core.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.legacy.structure_gel.StructureGelConfig;
import com.legacy.structure_gel.access_helpers.StructureAccessHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;

@Mixin(LakesFeature.class)
public class LakesFeatureMixin
{
	@Inject(method = "func_241855_a", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/BlockPos;down(I)Lnet/minecraft/util/math/BlockPos;"), cancellable = true)
	private void checkForStructures(ISeedReader seedReader, ChunkGenerator chunkGen, Random random, BlockPos pos, BlockStateFeatureConfig config, CallbackInfoReturnable<Boolean> callback)
	{
		if (StructureGelConfig.COMMON.getExtraLakeProofing())
		{
			for (Structure<?> structure : StructureAccessHelper.LAKE_STRUCTURES)
			{
				if (seedReader.func_241827_a(SectionPos.from(pos), structure).findAny().isPresent())
				{
					callback.setReturnValue(false);
					break;
				}
			}
		}
	}
}
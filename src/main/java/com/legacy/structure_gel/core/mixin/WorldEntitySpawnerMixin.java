package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

@Mixin(WorldEntitySpawner.class)
public class WorldEntitySpawnerMixin
{
	// Fixes a forge bug with mob spawns using events.
	@Inject(at = @At("TAIL"), method = "func_234976_a_(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/world/gen/feature/structure/StructureManager;Lnet/minecraft/world/gen/ChunkGenerator;Lnet/minecraft/entity/EntityClassification;Lnet/minecraft/world/biome/MobSpawnInfo$Spawners;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
	private static void isInDefaultSpawns(ServerWorld world, StructureManager structureManager, ChunkGenerator chunkGen, EntityClassification classification, MobSpawnInfo.Spawners spawner, BlockPos pos, CallbackInfoReturnable<Boolean> callback)
	{
		callback.setReturnValue(true);
	}
}

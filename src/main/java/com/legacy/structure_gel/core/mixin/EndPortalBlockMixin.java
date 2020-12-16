package com.legacy.structure_gel.core.mixin;

import com.legacy.structure_gel.util.capability.GelEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin
{
	@Inject(at = @At("HEAD"), method = "onEntityCollision(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V")
	private void onCollideHook(BlockState state, World worldIn, BlockPos pos, Entity entityIn, CallbackInfo callback)
	{
		GelEntity.setPortalClient(entityIn, null);
	}
}

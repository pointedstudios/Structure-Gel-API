package com.legacy.structure_gel.core.mixin;

import com.legacy.structure_gel.StructureGelConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.CompoundNBT;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO remove in 1.17
public class SpawnerFixMixins
{
	@Mixin(EndermanEntity.class)
	public static class Enderman
	{
		@Inject(at = @At(value = "FIELD", target = "net/minecraft/entity/monster/EndermanEntity.world:Lnet/minecraft/world/World;", opcode = Opcodes.GETFIELD), method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V", cancellable = true)
		private void readAdditional(CompoundNBT compound, CallbackInfo callback)
		{
			if (((Entity) (Object) this).world.isRemote && StructureGelConfig.CLIENT.fixSpawners())
				callback.cancel();
		}
	}

	@Mixin(ZombifiedPiglinEntity.class)
	public static class ZPiglin
	{
		@Inject(at = @At(value = "FIELD", target = "net/minecraft/entity/monster/ZombifiedPiglinEntity.world:Lnet/minecraft/world/World;", opcode = Opcodes.GETFIELD), method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V", cancellable = true)
		private void readAdditional(CompoundNBT compound, CallbackInfo callback)
		{
			if (((Entity) (Object) this).world.isRemote && StructureGelConfig.CLIENT.fixSpawners())
				callback.cancel();
		}
	}

	@Mixin(BeeEntity.class)
	public static class Bee
	{
		@Inject(at = @At(value = "FIELD", target = "net/minecraft/entity/passive/BeeEntity.world:Lnet/minecraft/world/World;", opcode = Opcodes.GETFIELD), method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V", cancellable = true)
		private void readAdditional(CompoundNBT compound, CallbackInfo callback)
		{
			if (((Entity) (Object) this).world.isRemote && StructureGelConfig.CLIENT.fixSpawners())
				callback.cancel();
		}
	}

	@Mixin(IronGolemEntity.class)
	public static class IronGolem
	{
		@Inject(at = @At(value = "FIELD", target = "net/minecraft/entity/passive/IronGolemEntity.world:Lnet/minecraft/world/World;", opcode = Opcodes.GETFIELD), method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V", cancellable = true)
		private void readAdditional(CompoundNBT compound, CallbackInfo callback)
		{
			if (((Entity) (Object) this).world.isRemote && StructureGelConfig.CLIENT.fixSpawners())
				callback.cancel();
		}
	}

	@Mixin(PolarBearEntity.class)
	public static class PolarBear
	{
		@Inject(at = @At(value = "FIELD", target = "net/minecraft/entity/passive/PolarBearEntity.world:Lnet/minecraft/world/World;", opcode = Opcodes.GETFIELD), method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V", cancellable = true)
		private void readAdditional(CompoundNBT compound, CallbackInfo callback)
		{
			if (((Entity) (Object) this).world.isRemote && StructureGelConfig.CLIENT.fixSpawners())
				callback.cancel();
		}
	}

	@Mixin(WolfEntity.class)
	public static class Wolf
	{
		@Inject(at = @At(value = "FIELD", target = "net/minecraft/entity/passive/WolfEntity.world:Lnet/minecraft/world/World;", opcode = Opcodes.GETFIELD), method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V", cancellable = true)
		private void readAdditional(CompoundNBT compound, CallbackInfo callback)
		{
			if (((Entity) (Object) this).world.isRemote && StructureGelConfig.CLIENT.fixSpawners())
				callback.cancel();
		}
	}
}

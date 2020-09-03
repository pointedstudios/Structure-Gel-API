package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.legacy.structure_gel.events.RegisterDimensionsEvent;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.MinecraftForge;

@Mixin(DimensionType.class)
public class DimensionTypeMixin
{
	/**
	 * {@link DimensionType#func_242718_a(Registry, Registry, Registry, long)}
	 * 
	 * @param dimTypeRegistry
	 * @param biomeRegistry
	 * @param dimSettingsRegistry
	 * @param seed
	 * @param callback
	 */
	@Inject(at = @At("RETURN"), method = "func_242718_a(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;Lnet/minecraft/util/registry/Registry;J)Lnet/minecraft/util/registry/SimpleRegistry;", cancellable = true)
	private static void func_242718_a(Registry<DimensionType> dimTypeRegistry, Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimSettingsRegistry, long seed, CallbackInfoReturnable<SimpleRegistry<Dimension>> callback)
	{
		SimpleRegistry<Dimension> simpleRegistry = callback.getReturnValue();
		MinecraftForge.EVENT_BUS.post(new RegisterDimensionsEvent(simpleRegistry, dimTypeRegistry, biomeRegistry, dimSettingsRegistry, seed));
		callback.setReturnValue(simpleRegistry);
	}
}

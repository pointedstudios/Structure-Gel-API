package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.legacy.structure_gel.events.RegisterDimensionEvent;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraftforge.common.MinecraftForge;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
	@Shadow
	protected DynamicRegistries.Impl field_240767_f_;
	@Shadow
	protected IServerConfiguration field_240768_i_;

	@Inject(at = @At("HEAD"), method = "func_240800_l__()V")
	private void initServer(CallbackInfo callback)
	{
		MinecraftForge.EVENT_BUS.post(new RegisterDimensionEvent(this.field_240768_i_.getDimensionGeneratorSettings().func_236224_e_(), this.field_240767_f_.getRegistry(Registry.DIMENSION_TYPE_KEY), this.field_240767_f_.getRegistry(Registry.BIOME_KEY), this.field_240767_f_.getRegistry(Registry.NOISE_SETTINGS_KEY), this.field_240768_i_.getDimensionGeneratorSettings().getSeed()));
	}
}

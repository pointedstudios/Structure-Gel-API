package com.legacy.structure_gel.core.mixin;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.events.RegisterDimensionEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
	@Inject(at = @At("HEAD"), method = "func_240800_l__()V")
	private void initServer(CallbackInfo callback)
	{
		try
		{
			MinecraftForge.EVENT_BUS.post(new RegisterDimensionEvent(this.getServerConfiguration().getDimensionGeneratorSettings().func_236224_e_(), this.func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY), this.func_244267_aX().getRegistry(Registry.BIOME_KEY), this.func_244267_aX().getRegistry(Registry.NOISE_SETTINGS_KEY), this.getServerConfiguration().getDimensionGeneratorSettings().getSeed()));
		}
		catch (Throwable r)
		{
			StructureGelMod.LOGGER.fatal("Huh. Well this is awkward. Looks like you crashed while using custom server software. Some of the stuff that we need doesn't exist because they changed stuff in the code. If you could report it to them, that would be nice. Send them to our repo so they can look at this class and see where we get the values from. Anyway, have a good day :)");
			if (this.getServerConfiguration() == null)
				StructureGelMod.LOGGER.fatal("The server's configuration is null!");
			else
			{
				if (this.getServerConfiguration().getDimensionGeneratorSettings() == null)
					StructureGelMod.LOGGER.fatal("The server's dimension generator settings is null!");
				else
				{
					if (this.getServerConfiguration().getDimensionGeneratorSettings().func_236224_e_() == null)
						StructureGelMod.LOGGER.fatal("The dimension registry is null!");
					if (this.getServerConfiguration().getDimensionGeneratorSettings().getSeed() == (Long) null)
						StructureGelMod.LOGGER.fatal("The dimension generator settings seed is... null. But how though?");
				}
			}
			if (this.func_244267_aX() == null)
				StructureGelMod.LOGGER.fatal("The server's DynamicRegistries is null!");
			else
			{
				if (this.func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY) == null)
					StructureGelMod.LOGGER.fatal("The dimension type registry is null!");
				if (this.func_244267_aX().getRegistry(Registry.BIOME_KEY) == null)
					StructureGelMod.LOGGER.fatal("The biome key registry is null!");
				if (this.func_244267_aX().getRegistry(Registry.NOISE_SETTINGS_KEY) == null)
					StructureGelMod.LOGGER.fatal("The noise settings registry is null!");
			}
			StructureGelMod.LOGGER.fatal(r);
			throw r;
		}
	}

	@Shadow
	public IServerConfiguration getServerConfiguration()
	{
		throw new IllegalStateException("Mixin failed to shadow getServerConfiguration()");
	}

	@Shadow
	public DynamicRegistries func_244267_aX()
	{
		throw new IllegalStateException("Mixin failed to shadow func_244267_aX()");
	}
}

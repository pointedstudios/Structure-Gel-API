package com.legacy.structure_gel.core.mixin;

import com.legacy.structure_gel.SGCrashHandler;
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

import java.util.ArrayList;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
	private static final String REGISTER_DIMENSION_FAILED_MESSAGE = "Huh. Well this is awkward. Looks like you crashed while using custom server software. Some of the stuff that we need doesn't exist because they changed stuff in the code. If you could report it to them, that would be nice. Send them to our repo so they can look at this class and see where we get the values from.";

	@Inject(at = @At("HEAD"), method = "func_240800_l__()V")
	private void initServer(CallbackInfo callback)
	{
		try
		{
			MinecraftForge.EVENT_BUS.post(new RegisterDimensionEvent(this.getServerConfiguration().getDimensionGeneratorSettings().func_236224_e_(), this.func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY), this.func_244267_aX().getRegistry(Registry.BIOME_KEY), this.func_244267_aX().getRegistry(Registry.NOISE_SETTINGS_KEY), this.getServerConfiguration().getDimensionGeneratorSettings().getSeed()));
		}
		catch (Throwable r)
		{
			StructureGelMod.LOGGER.fatal(REGISTER_DIMENSION_FAILED_MESSAGE);
			ArrayList<String> nullItems = new ArrayList<>();
			if (this.getServerConfiguration() == null)
			{
				StructureGelMod.LOGGER.fatal("The server's configuration is null!");
				nullItems.add("configuration");
			}
			else
			{
				if (this.getServerConfiguration().getDimensionGeneratorSettings() == null)
				{
					StructureGelMod.LOGGER.fatal("The server's dimension generator settings is null!");
					nullItems.add("dimension generator");
				}
				else
				{
					if (this.getServerConfiguration().getDimensionGeneratorSettings().func_236224_e_() == null)
					{
						StructureGelMod.LOGGER.fatal("The dimension registry is null!");
						nullItems.add("dimension registry");
					}
					if (this.getServerConfiguration().getDimensionGeneratorSettings().getSeed() == (Long) null)
					{
						StructureGelMod.LOGGER.fatal("The dimension generator settings seed is... null. But how though?");
						nullItems.add("dimension generator settings seed");
					}
				}
			}
			if (this.func_244267_aX() == null)
			{
				StructureGelMod.LOGGER.fatal("The server's DynamicRegistries is null!");
				nullItems.add("DynamicRegistries");
			}
			else
			{
				if (this.func_244267_aX().getRegistry(Registry.DIMENSION_TYPE_KEY) == null)
				{
					StructureGelMod.LOGGER.fatal("The dimension type registry is null!");
					nullItems.add("dimension type registry");
				}
				if (this.func_244267_aX().getRegistry(Registry.BIOME_KEY) == null)
				{
					StructureGelMod.LOGGER.fatal("The biome key registry is null!");
					nullItems.add("biome key registry");
				}
				if (this.func_244267_aX().getRegistry(Registry.NOISE_SETTINGS_KEY) == null)
				{
					StructureGelMod.LOGGER.fatal("The noise settings registry is null!");
					nullItems.add("noise settings registry");
				}
			}
			StructureGelMod.LOGGER.fatal("That should be everything. Anyway, have a good day :)");
			StructureGelMod.LOGGER.fatal(r);
			prepareCrashReport(nullItems);
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

	private static void prepareCrashReport(ArrayList<String> nullItems)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(REGISTER_DIMENSION_FAILED_MESSAGE);
		stringBuilder.append("\n");
		stringBuilder.append("Reason: ");
		if (nullItems.size() > 0)
		{
			stringBuilder.append("The server's ");
			for (int i = 0; i < nullItems.size(); i++)
			{
				if (i == nullItems.size() - 1)
					stringBuilder.append(String.format("%s%s", nullItems.size() == 1 ? "" : "and ", nullItems.get(i)));
				else
					stringBuilder.append(String.format("%s%s ", nullItems.get(i), nullItems.size() == 2 ? "" : ","));
			}
			stringBuilder.append(String.format(" %s null.\n", nullItems.size() > 1 ? "were" : "was"));
		}
		else
			stringBuilder.append("Well, we're not really sure why this crash happened, but make sure nothing in your custom server software is null when it shouldn't be. " +
					"If all else fails, you can talk to us on the Modding Legacy Discord server. This crash might be an interesting rabbit hole to traverse.\n");
		stringBuilder.append("Anyway, here's the rest of the crash report. Have a good day :)");
		SGCrashHandler.prepareAdditionalCrashInfo(stringBuilder.toString());
	}
}

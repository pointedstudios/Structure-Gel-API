package com.legacy.structure_gel;

import com.legacy.structure_gel.SGRegistry.GelBlocks;
import com.legacy.structure_gel.SGRegistry.GelItems;
import com.legacy.structure_gel.SGRegistry.StructureRegistry;
import com.legacy.structure_gel.biome_dictionary.BiomeType;
import com.legacy.structure_gel.util.Internal;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is an API with the purpose of giving access and shortcuts to various
 * aspects of worldgen, such as structures, biomes, and dimensions. Methods and
 * classes are documented with details on how they work and where to use them.
 * Anywhere that you see the {@link Internal} annotation means that you
 * shouldn't need to call the thing annotated.<br>
 * <br>
 * In order to get the Mixins working in your workspace, add<br>
 * arg '-mixin.config=structure_gel.mixins.json'<br>
 * to your run configurations.<br>
 * <br>
 * If you encounter issues or find any bugs, please report them to the issue
 * tracker. https://gitlab.com/modding-legacy/structure-gel-api/-/issues
 *
 * @author David
 */
@Mod(StructureGelMod.MODID)
public class StructureGelMod
{
	public static final String MODID = "structure_gel";
	public static final Logger LOGGER = LogManager.getLogger("ModdingLegacy/" + MODID);

	public StructureGelMod()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureGelConfig.COMMON_SPEC);

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;

		SGEvents.init(modBus, forgeBus);
		modBus.addGenericListener(BiomeType.class, SGRegistry::registerBiomeDictionary);
		modBus.addGenericListener(Block.class, GelBlocks::onRegistry);
		modBus.addGenericListener(Item.class, GelItems::onRegistry);
		modBus.addGenericListener(Structure.class, StructureRegistry::onRegistry);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
		{
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, StructureGelConfig.CLIENT_SPEC);

			com.legacy.structure_gel.SGClientEvents.init(modBus, forgeBus);
		});

		StructureGelCompat.init(modBus);

		// Debugging stuff
		// com.legacy.structure_gel.SGDebug.init(modBus, forgeBus);
	}

	@Internal
	public static ResourceLocation locate(String key)
	{
		return new ResourceLocation(MODID, key);
	}
	
	@Internal
	public static Logger makeLogger(String name)
	{
		return LogManager.getLogger("ModdingLegacy/" + MODID + "/" + name);
	}
}

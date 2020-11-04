package com.legacy.structure_gel.core.mixin;

import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Maps;
import com.legacy.structure_gel.StructureGelConfig;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

// TODO This might not be needed in a future version since it fixes a vanilla bug
public class StructureSaveMixins
{
	@Mixin(ChunkSerializer.class)
	public static class ChunkSerializerMixin
	{
		@Shadow
		private static Logger LOGGER;

		@Inject(at = @At("HEAD"), method = "func_235967_a_(Lnet/minecraft/world/gen/feature/template/TemplateManager;Lnet/minecraft/nbt/CompoundNBT;J)Ljava/util/Map;", cancellable = true)
		private static void consoleSpamFix(TemplateManager templateManager, CompoundNBT nbt, long seed, CallbackInfoReturnable<Map<Structure<?>, StructureStart<?>>> callback)
		{
			if (StructureGelConfig.COMMON.shouldFixMissingStructureSaving())
			{
				Map<Structure<?>, StructureStart<?>> map = Maps.newHashMap();
				CompoundNBT startsNBT = nbt.getCompound("Starts");

				for (String name : startsNBT.keySet())
				{
					name = name.toLowerCase(Locale.ROOT);
					Structure<?> structure = Structure.NAME_STRUCTURE_BIMAP.get(name);
					if (structure != null)
					{
						StructureStart<?> structurestart = Structure.deserializeStructureStart(templateManager, startsNBT.getCompound(name), seed);
						if (structurestart != null)
							map.put(structure, structurestart);
					}
					else if (new ResourceLocation(name).getNamespace().equals("minecraft"))
						LOGGER.error("Unknown vanilla structure start: {}", name);

				}

				callback.setReturnValue(map);
			}
		}
	}

	@Mixin(Chunk.class)
	public static class ChunkMixin
	{
		@Shadow
		private Map<Structure<?>, StructureStart<?>> structureStarts;
		@Shadow
		private Map<Structure<?>, LongSet> structureReferences;

		@Inject(at = @At("HEAD"), method = "getStructureStarts()Ljava/util/Map;")
		private void getStructureStartsFix(CallbackInfoReturnable<Map<Structure<?>, StructureStart<?>>> callback)
		{
			if (StructureGelConfig.COMMON.shouldFixMissingStructureSaving())
				this.structureStarts.entrySet().removeIf(e -> e.getKey() == null || e.getValue() == null || e.getKey().getStructureName() == null);
		}

		@Inject(at = @At("HEAD"), method = "getStructureReferences()Ljava/util/Map;")
		private void getStructureRefsFix(CallbackInfoReturnable<Map<Structure<?>, LongSet>> callback)
		{
			if (StructureGelConfig.COMMON.shouldFixMissingStructureSaving())
				this.structureReferences.entrySet().removeIf(e -> e.getKey() == null || e.getValue() == null || e.getKey().getStructureName() == null);
		}
	}
}
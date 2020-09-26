package com.legacy.structure_gel.util.capability;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.blocks.GelPortalBlock;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.registries.ForgeRegistries;

public class GelCapability
{
	@CapabilityInject(IGelEntity.class)
	public static Capability<IGelEntity> INSTANCE = null;

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IGelEntity.class, new Storage(), GelEntity::new);
	}

	public static void ifPresent(Entity entity, Consumer<IGelEntity> action)
	{
		if (entity.getCapability(INSTANCE).isPresent())
			action.accept(entity.getCapability(INSTANCE).resolve().get());
	}

	public static void ifPresent(Entity entity, Consumer<IGelEntity> action, Consumer<IGelEntity> elseAction)
	{
		if (entity.getCapability(INSTANCE).isPresent())
			action.accept(entity.getCapability(INSTANCE).resolve().get());
		else
			elseAction.accept(entity.getCapability(INSTANCE).resolve().get());
	}
	
	@Nullable
	public static <T> T getIfPresent(Entity entity, Function<IGelEntity, T> action)
	{
		if (entity.getCapability(INSTANCE).isPresent())
			return action.apply(entity.getCapability(INSTANCE).resolve().get());
		return null;
	}
	
	public static <T> T getIfPresent(Entity entity, Function<IGelEntity, T> action, Function<IGelEntity, T> elseAction)
	{
		if (entity.getCapability(INSTANCE).isPresent())
			return action.apply(entity.getCapability(INSTANCE).resolve().get());
		else
			return elseAction.apply(entity.getCapability(INSTANCE).resolve().get());
	}

	public static class Storage implements Capability.IStorage<IGelEntity>
	{
		private static final ResourceLocation EMPTY = StructureGelMod.locate("empty");

		@Nullable
		@Override
		public INBT writeNBT(Capability<IGelEntity> capability, IGelEntity instance, Direction side)
		{
			CompoundNBT tag = new CompoundNBT();
			tag.putString("portal", instance.getPortal() != null ? instance.getPortal().getRegistryName().toString() : EMPTY.toString());
			tag.putString("prev_portal", instance.getPrevPortal() != null ? instance.getPrevPortal().getRegistryName().toString() : EMPTY.toString());
			return tag;
		}

		@Override
		public void readNBT(Capability<IGelEntity> capability, IGelEntity instance, Direction side, INBT nbt)
		{
			ResourceLocation key = new ResourceLocation(((CompoundNBT) nbt).getString("portal"));
			instance.setPortal((!key.equals(EMPTY) && ForgeRegistries.BLOCKS.containsKey(key) && ForgeRegistries.BLOCKS.getValue(key) instanceof GelPortalBlock) ? (GelPortalBlock) ForgeRegistries.BLOCKS.getValue(key) : null);
		
			key = new ResourceLocation(((CompoundNBT) nbt).getString("prev_portal"));
			instance.setPrevPortal((!key.equals(EMPTY) && ForgeRegistries.BLOCKS.containsKey(key) && ForgeRegistries.BLOCKS.getValue(key) instanceof GelPortalBlock) ? (GelPortalBlock) ForgeRegistries.BLOCKS.getValue(key) : null);
		}
	}
}

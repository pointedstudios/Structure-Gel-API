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

	public static <E extends Entity> void ifPresent(E entity, Consumer<IGelEntity> action)
	{
		if (entity != null && entity.getCapability(INSTANCE).isPresent())
			action.accept(entity.getCapability(INSTANCE).resolve().get());
	}

	public static <E extends Entity> void ifPresent(E entity, Consumer<IGelEntity> action, Consumer<E> elseAction)
	{
		if (entity != null)
		{
			if (entity.getCapability(INSTANCE).isPresent())
				action.accept(entity.getCapability(INSTANCE).resolve().get());
			else
				elseAction.accept(entity);
		}
	}

	@Nullable
	public static <E extends Entity, R> R getIfPresent(E entity, Function<IGelEntity, R> action)
	{
		if (entity != null && entity.getCapability(INSTANCE).isPresent())
			return action.apply(entity.getCapability(INSTANCE).resolve().get());
		return null;
	}

	@Nullable
	public static <E extends Entity, R> R getIfPresent(E entity, Function<IGelEntity, R> action, Function<E, R> elseAction)
	{
		if (entity != null)
		{
			if (entity.getCapability(INSTANCE).isPresent())
				return action.apply(entity.getCapability(INSTANCE).resolve().get());
			else
				return elseAction.apply(entity);
		}
		return null;
	}

	public static class Storage implements Capability.IStorage<IGelEntity>
	{
		public static final ResourceLocation EMPTY = StructureGelMod.locate("empty");
		public static final String portal = "portal", portalVisual = "portal_visual", portalAudio = "portal_audio";

		@Nullable
		@Override
		public INBT writeNBT(Capability<IGelEntity> capability, IGelEntity instance, Direction side)
		{
			CompoundNBT nbt = new CompoundNBT();
			putPortal(nbt, portal, instance.getPortal());
			putPortal(nbt, portalVisual, instance.getPortalVisual());
			putPortal(nbt, portalAudio, instance.getPortalAudio());
			return nbt;
		}

		private void putPortal(CompoundNBT nbt, String key, GelPortalBlock portal)
		{
			nbt.putString(key, portal != null ? portal.getRegistryName().toString() : EMPTY.toString());
		}

		@Override
		public void readNBT(Capability<IGelEntity> capability, IGelEntity instance, Direction side, INBT inbt)
		{
			if (inbt instanceof CompoundNBT)
			{
				CompoundNBT nbt = (CompoundNBT) inbt;
				instance.setPortal(getPortal(nbt, portal));
				instance.setPortalVisual(getPortal(nbt, portalVisual));
				instance.setPortalAudio(getPortal(nbt, portalAudio));
			}
		}

		private GelPortalBlock getPortal(CompoundNBT nbt, String key)
		{
			ResourceLocation portal = new ResourceLocation(nbt.getString(key));
			return (!portal.equals(EMPTY) && ForgeRegistries.BLOCKS.containsKey(portal) && ForgeRegistries.BLOCKS.getValue(portal) instanceof GelPortalBlock) ? (GelPortalBlock) ForgeRegistries.BLOCKS.getValue(portal) : null;
		}
	}
}

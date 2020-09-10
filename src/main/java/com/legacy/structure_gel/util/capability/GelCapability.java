package com.legacy.structure_gel.util.capability;

import javax.annotation.Nullable;

import com.legacy.structure_gel.StructureGelMod;
import com.legacy.structure_gel.blocks.GelPortalBlock;

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

	public static class Storage implements Capability.IStorage<IGelEntity>
	{
		private static final ResourceLocation EMPTY = StructureGelMod.locate("empty");
		
		@Nullable
		@Override
		public INBT writeNBT(Capability<IGelEntity> capability, IGelEntity instance, Direction side)
		{
			CompoundNBT tag = new CompoundNBT();
			tag.putString("portal", instance.getPortal() != null ? instance.getPortal().getRegistryName().toString() : EMPTY.toString());
			return tag;
		}

		@Override
		public void readNBT(Capability<IGelEntity> capability, IGelEntity instance, Direction side, INBT nbt)
		{
			ResourceLocation key = new ResourceLocation(((CompoundNBT) nbt).getString("portal"));
			instance.setPortal((!key.equals(EMPTY) && ForgeRegistries.BLOCKS.containsKey(key) && ForgeRegistries.BLOCKS.getValue(key) instanceof GelPortalBlock) ? (GelPortalBlock) ForgeRegistries.BLOCKS.getValue(key) : null);
		}
	}
}

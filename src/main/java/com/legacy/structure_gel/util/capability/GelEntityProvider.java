package com.legacy.structure_gel.util.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class GelEntityProvider implements ICapabilitySerializable<CompoundNBT>
{
	private final GelEntity player = new GelEntity();
	private final LazyOptional<IGelEntity> playerOptional = LazyOptional.of(() -> player);

	public void invalidate()
	{
		playerOptional.invalidate();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		return cap == GelCapability.INSTANCE ? this.playerOptional.cast() : LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT()
	{
		if (GelCapability.INSTANCE == null)
			return new CompoundNBT();
		else
			return (CompoundNBT) GelCapability.INSTANCE.writeNBT(player, null);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		if (GelCapability.INSTANCE != null)
			GelCapability.INSTANCE.readNBT(player, null, nbt);
	}
}

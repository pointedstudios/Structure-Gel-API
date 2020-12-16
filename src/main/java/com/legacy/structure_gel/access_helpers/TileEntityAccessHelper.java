package com.legacy.structure_gel.access_helpers;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import java.util.function.Consumer;

/**
 * Contains methods for modifying tile entity data.
 *
 * @author David
 */
public class TileEntityAccessHelper
{
	public static void modifyNBT(TileEntity tile, Consumer<CompoundNBT> nbtConsumer)
	{
		CompoundNBT nbt = new CompoundNBT();
		tile.write(nbt);
		nbtConsumer.accept(nbt);
		tile.read(tile.getBlockState(), nbt);
	}
}

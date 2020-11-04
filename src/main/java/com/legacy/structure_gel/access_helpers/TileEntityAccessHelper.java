package com.legacy.structure_gel.access_helpers;

import java.util.function.Consumer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

/**
 * Contains methods for modifying tile entity data.
 * 
 * @author David
 *
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

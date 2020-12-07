package com.legacy.structure_gel.core.mixin;

import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.legacy.structure_gel.StructureGelConfig;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

@Mixin(Item.class)
public class ItemMixin
{
	@Inject(at = @At("HEAD"), method = "getDisplayName(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/text/ITextComponent;", cancellable = true)
	private void getDisplayName(ItemStack stack, CallbackInfoReturnable<ITextComponent> callback)
	{
		if (StructureGelConfig.CLIENT.showStructureBlockInfo() && stack.getItem() == Items.STRUCTURE_BLOCK)
		{
			CompoundNBT blockEntityTag = stack.getChildTag("BlockEntityTag");
			if (blockEntityTag != null)
			{
				String mode = blockEntityTag.contains("mode") ? blockEntityTag.getString("mode").toLowerCase(Locale.ENGLISH) : "data";
				String text = mode.equals("data") ? (blockEntityTag.contains("metadata") ? blockEntityTag.getString("metadata") : "null") : (blockEntityTag.contains("name") ? blockEntityTag.getString("name") : "null");
				callback.setReturnValue(new TranslationTextComponent("structure_block.mode." + mode).append(new StringTextComponent(": \"" + text + "\"")));
			}
		}
	}
}

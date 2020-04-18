package com.legacy.structure_gel.items;

import java.util.List;

import com.legacy.structure_gel.StructureGelMod;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StructureGelItem extends BlockItem
{
	public StructureGelItem(Block blockIn)
	{
		super(blockIn, new Item.Properties().group(ItemGroup.MISC));
	}

	@Override
	public ActionResultType tryPlace(BlockItemUseContext context)
	{
		if (!context.getPlayer().isCreative())
			return ActionResultType.FAIL;
		else
			return super.tryPlace(context);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if (!Screen.hasShiftDown())
			tooltip.add(new TranslationTextComponent("item." + StructureGelMod.MODID + ".hold_shift").applyTextStyle(TextFormatting.GRAY));
		else
		{
			tooltip.add(new TranslationTextComponent("item." + StructureGelMod.MODID + ".place").applyTextStyle(TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent("item." + StructureGelMod.MODID + ".gunpowder").applyTextStyle(TextFormatting.GRAY));
			tooltip.add(new TranslationTextComponent(""));
			tooltip.add(new TranslationTextComponent("item." + this.getRegistryName().toString().replace(":", ".") + ".information").applyTextStyle(TextFormatting.GRAY));
		}
	}
}

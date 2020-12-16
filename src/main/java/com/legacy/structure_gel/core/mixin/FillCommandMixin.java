package com.legacy.structure_gel.core.mixin;

import com.legacy.structure_gel.StructureGelConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.FillCommand;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FillCommand.class)
public class FillCommandMixin
{
	@ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "doFill(Lnet/minecraft/command/CommandSource;Lnet/minecraft/util/math/MutableBoundingBox;Lnet/minecraft/command/arguments/BlockStateInput;Lnet/minecraft/command/impl/FillCommand$Mode;Ljava/util/function/Predicate;)I")
	private static int modifySize(int i, CommandSource source)
	{
		int limit = 32768;
		if (i > limit && StructureGelConfig.COMMON.shouldExceedFillLimit())
		{
			source.sendFeedback(new TranslationTextComponent("commands.structure_gel.fill.override", limit, i), true);
			return 0;
		}
		return i;
	}
}

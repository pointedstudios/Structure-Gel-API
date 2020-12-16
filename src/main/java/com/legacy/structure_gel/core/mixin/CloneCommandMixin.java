package com.legacy.structure_gel.core.mixin;

import com.legacy.structure_gel.StructureGelConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.CloneCommand;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CloneCommand.class)
public class CloneCommandMixin
{
	@ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "doClone(Lnet/minecraft/command/CommandSource;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Ljava/util/function/Predicate;Lnet/minecraft/command/impl/CloneCommand$Mode;)I")
	private static int modifySize(int i, CommandSource source)
	{
		int limit = 32768;
		if (i > limit && StructureGelConfig.COMMON.shouldExceedFillLimit())
		{
			source.sendFeedback(new TranslationTextComponent("commands.structure_gel.clone.override", limit, i), true);
			return 0;
		}
		return i;
	}
}

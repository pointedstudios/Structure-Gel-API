package com.legacy.structure_gel.core.mixin;

import com.legacy.structure_gel.StructureGelConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.StructureTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Renders text in the world on top of Structure Blocks, similarly to how it did
 * before 1.13.
 *
 * @author Bailey
 */
@Mixin(StructureTileEntityRenderer.class)
public class StructureTileEntityRendererMixin
{
	@Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/tileentity/StructureBlockTileEntity;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;II)V")
	private void render(StructureBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, CallbackInfo callback)
	{
		if (StructureGelConfig.CLIENT.showStructureBlockInfo())
		{
			Minecraft mc = Minecraft.getInstance();
			if (tileEntityIn.getWorld() != null && mc.player != null && ((BlockRayTraceResult) rayTrace(tileEntityIn.getWorld(), mc.player)).getPos().equals(tileEntityIn.getPos()))
			{
				String name = tileEntityIn.getMode().getString();
				IFormattableTextComponent mode = new TranslationTextComponent("structure_block.mode." + name).setStyle(Style.EMPTY.setBold(true).setUnderlined(true));
				IFormattableTextComponent text = new StringTextComponent(": " + (tileEntityIn.getMode() == StructureMode.DATA ? tileEntityIn.getMetadata() : tileEntityIn.getName())).setStyle(Style.EMPTY.setBold(false).setUnderlined(false));
				renderName(mode.append(text), matrixStackIn, bufferIn, 220);
			}
		}
	}

	/**
	 * Ray trace the block in front of the player passed in.
	 *
	 * @param world
	 * @param player
	 * @return {@link RayTraceResult}
	 */
	private static RayTraceResult rayTrace(World world, PlayerEntity player)
	{
		Vector3d eyePos = player.getEyePosition(1.0F);
		float pi = (float) Math.PI;
		float radian = pi / 180F;
		float pitch = player.rotationPitch * radian;
		float yaw = player.rotationYaw * radian;
		float cosYaw = MathHelper.cos(-yaw - pi);
		float sinYaw = MathHelper.sin(-yaw - pi);
		float cosPitch = -MathHelper.cos(-pitch);
		float sinPitch = MathHelper.sin(-pitch);
		double playerReach = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
		Vector3d endPos = eyePos.add(sinYaw * cosPitch * playerReach, sinPitch * playerReach, cosYaw * cosPitch * playerReach);
		return world.rayTraceBlocks(new RayTraceContext(eyePos, endPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
	}

	/**
	 * Renders a nameplate at the given buffer position.
	 *
	 * @param displayName
	 * @param matrixStack
	 * @param buffer
	 * @param packedLight
	 */
	private static void renderName(ITextComponent displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.player == null || mc.world == null)
			return;

		if (mc.getRenderManager().squareDistanceTo(mc.player) <= 4096.0D)
		{
			matrixStack.push();
			matrixStack.translate(0.5, 1.5, 0.5);
			matrixStack.rotate(mc.getRenderManager().getCameraOrientation());
			matrixStack.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f lastMatrix = matrixStack.getLast().getMatrix();
			FontRenderer fontRenderer = mc.fontRenderer;
			float centerString = (float) (-fontRenderer.getStringPropertyWidth(displayName) / 2);
			fontRenderer.func_243247_a(displayName, centerString, (float) 0, 553648127, false, lastMatrix, buffer, true, (int) (mc.gameSettings.getTextBackgroundOpacity(0.25F) * 255.0F) << 24, packedLight);
			fontRenderer.func_243247_a(displayName, centerString, (float) 0, -1, false, lastMatrix, buffer, false, 0, packedLight);
			matrixStack.pop();
		}
	}
}

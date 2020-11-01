package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

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
			if (tileEntityIn.getWorld() != null && mc.player != null)
			{
				BlockRayTraceResult traceResult = (BlockRayTraceResult) rayTrace(tileEntityIn.getWorld(), mc.player);

				if (traceResult.getPos().equals(tileEntityIn.getPos()))
				{
					IFormattableTextComponent blockName = new StringTextComponent(tileEntityIn.getMode().name().substring(0, 1).toUpperCase() + tileEntityIn.getMode().name().substring(1).toLowerCase()).setStyle(Style.EMPTY.setBold(true).setUnderlined(true));
					IFormattableTextComponent metadataName = new StringTextComponent(": " + (tileEntityIn.getMode() == StructureMode.DATA ? tileEntityIn.getMetadata() : tileEntityIn.getName())).setStyle(Style.EMPTY.setBold(false).setUnderlined(false));

					renderName(blockName.append(metadataName), matrixStackIn, bufferIn, 220);
				}
			}
		}
	}

	/**
	 * Ray trace the block in front of the player passed in.
	 * 
	 * @param worldIn
	 * @param player
	 * @return {@link RayTraceResult}
	 */
	private static RayTraceResult rayTrace(World worldIn, PlayerEntity player)
	{
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		Vector3d vec3d = player.getEyePosition(1.0F);
		float f2 = MathHelper.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * ((float) Math.PI / 180F));
		float f5 = MathHelper.sin(-f * ((float) Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
		Vector3d vec3d1 = vec3d.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
		return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.SOURCE_ONLY, player));
	}

	/**
	 * Renders a nameplate at the given buffer position.
	 * 
	 * @param displayNameIn
	 * @param matrixStackIn
	 * @param bufferIn
	 * @param packedLightIn
	 */
	private static void renderName(ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.player == null || mc.world == null)
			return;

		double d0 = mc.getRenderManager().squareDistanceTo(mc.player);
		if (!(d0 > 4096.0D))
		{
			boolean flag = true;
			float f = 1 + 0.5F;
			matrixStackIn.push();
			matrixStackIn.translate(0.5D, (double) f, 0.5D);
			matrixStackIn.rotate(mc.getRenderManager().getCameraOrientation());
			matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
			float f1 = mc.gameSettings.getTextBackgroundOpacity(0.25F);
			int j = (int) (f1 * 255.0F) << 24;
			FontRenderer fontrenderer = mc.fontRenderer;
			float f2 = (float) (-fontrenderer.getStringPropertyWidth(displayNameIn) / 2);
			fontrenderer.func_243247_a(displayNameIn, f2, (float) 0, 553648127, false, matrix4f, bufferIn, flag, j, packedLightIn);
			if (flag)
			{
				fontrenderer.func_243247_a(displayNameIn, f2, (float) 0, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
			}

			matrixStackIn.pop();
		}
	}
}
package com.legacy.structure_gel.blocks;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.legacy.structure_gel.util.GelPortalSize;
import com.legacy.structure_gel.util.GelTeleporter;
import com.legacy.structure_gel.util.capability.GelCapability;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A portal block designed for easy mod compatibility.
 * 
 * @author David
 *
 */
public class GelPortalBlock extends NetherPortalBlock
{
	/**
	 * The teleporter to take you from one dimension to the other.
	 */
	private final Function<ServerWorld, GelTeleporter> teleporter;

	public GelPortalBlock(AbstractBlock.Properties properties, Function<ServerWorld, GelTeleporter> teleporter)
	{
		super(properties);
		this.teleporter = teleporter;
	}

	/**
	 * Code to execute when rendering portal texture on the player's screen. Mimics
	 * vanilla rendering by default.
	 * 
	 * @param timeInPortal
	 * @param scaledHeight
	 * @param scaledWidth
	 */
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public void renderPortal(float timeInPortal, int scaledHeight, int scaledWidth)
	{
		Minecraft mc = Minecraft.getInstance();
		if (timeInPortal < 1.0F)
		{
			timeInPortal = timeInPortal * timeInPortal;
			timeInPortal = timeInPortal * timeInPortal;
			timeInPortal = timeInPortal * 0.8F + 0.2F;
		}

		RenderSystem.disableAlphaTest();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, timeInPortal);
		mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		TextureAtlasSprite sprite = this.getPortalTexture();
		float f = sprite.getMinU();
		float f1 = sprite.getMinV();
		float f2 = sprite.getMaxU();
		float f3 = sprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffBuilder = tessellator.getBuffer();
		buffBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffBuilder.pos(0.0D, (double) scaledHeight, -90.0D).tex(f, f3).endVertex();
		buffBuilder.pos((double) scaledWidth, (double) scaledHeight, -90.0D).tex(f2, f3).endVertex();
		buffBuilder.pos((double) scaledWidth, 0.0D, -90.0D).tex(f2, f1).endVertex();
		buffBuilder.pos(0.0D, 0.0D, -90.0D).tex(f, f1).endVertex();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Gets the texture for the portal overlay to render with. Returns the texture
	 * of this block by default.
	 * 
	 * @return {@link TextureAtlasSprite}
	 */
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public TextureAtlasSprite getPortalTexture()
	{
		Minecraft mc = Minecraft.getInstance();
		return mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(this.getDefaultState());
	}

	/**
	 * Gets the sound that plays when the player steps in the portal. Nether by
	 * default.
	 * 
	 * @return {@link ISound}
	 */
	@OnlyIn(Dist.CLIENT)
	public ISound getTriggerSound()
	{
		return SimpleSound.ambientWithoutAttenuation(SoundEvents.BLOCK_PORTAL_TRIGGER, new Random().nextFloat() * 0.4F + 0.8F, 0.25F);
	}

	/**
	 * Gets the sound that plays when the player goes through the portal. Nether by
	 * default.
	 * 
	 * @return {@link ISound}
	 */
	@OnlyIn(Dist.CLIENT)
	public ISound getTravelSound()
	{
		return SimpleSound.ambientWithoutAttenuation(SoundEvents.BLOCK_PORTAL_TRAVEL, new Random().nextFloat() * 0.4F + 0.8F, 0.25F);
	}

	/**
	 * Call this to fill the portal when whatever condition you need to fill it
	 * occurs.
	 * 
	 * @param world
	 * @param pos
	 * @param portal
	 * @param allowedBlocks A list of blocks that are allowed to exist within the
	 *            portal frame when trying to light it. IE: A nether portal should
	 *            allow fire and an Aether portal should allow water.
	 * @return {@link Boolean}
	 */
	public static boolean fillPortal(World world, BlockPos pos, GelPortalBlock portal, List<Block> allowedBlocks)
	{
		GelTeleporter teleporter = portal.getTeleporter(null);
		if (world.getDimensionKey() == teleporter.getDimension1().get() || world.getDimensionKey() == teleporter.getDimension2().get())
			if (GelPortalSize.trySpawnPortal(world, pos, portal, allowedBlocks))
				return true;
		return false;
	}

	/**
	 * Returns the the teleporter instance for the world passed. You can pass null
	 * if you just need the dimensions, just make sure not to actually use it.
	 * 
	 * @param world
	 * @return {@link GelTeleporter}
	 */
	public final GelTeleporter getTeleporter(ServerWorld world)
	{
		return this.teleporter.apply(world);
	}

	/**
	 * Gets the state of the frame block for this portal.
	 * 
	 * @return {@link Supplier}
	 */
	public Supplier<BlockState> getFrameBlock()
	{
		return this.teleporter.apply(null).getFrameBlock();
	}

	/**
	 * The amount of time that an entity can sit in the portal before teleporting.
	 * 
	 * @param entityIn
	 * @return {@link Integer}
	 */
	public int getMaxTimeInside(Entity entityIn)
	{
		return entityIn.getMaxInPortalTime();
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
	{
		return;
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		Direction.Axis facingAxis = facing.getAxis();
		Direction.Axis portalAxis = stateIn.get(AXIS);
		boolean flag = portalAxis != facingAxis && facingAxis.isHorizontal();
		return !flag && !facingState.isIn(this) && !(new GelPortalSize(worldIn, currentPos, portalAxis, this.getFrameBlock().get().getBlock(), this, ImmutableList.of())).isPortalComplete() ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
	{
		GelCapability.ifPresent(entityIn, (gelEntity) ->
		{
			if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss())
			{
				entityIn.setPortal(pos);
				gelEntity.setPortal(this);
				gelEntity.setPortalVisual(this);
				gelEntity.setPortalAudio(this);
			}
		});
	}
}

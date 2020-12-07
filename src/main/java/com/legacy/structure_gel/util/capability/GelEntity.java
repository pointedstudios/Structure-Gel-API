package com.legacy.structure_gel.util.capability;

import javax.annotation.Nullable;

import com.legacy.structure_gel.blocks.GelPortalBlock;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.entity.Entity;

public class GelEntity implements IGelEntity
{
	@Nullable
	private GelPortalBlock portal;
	@Internal
	@Nullable
	private static GelPortalBlock portalClient;

	@Nullable
	@Override
	public GelPortalBlock getPortal()
	{
		return this.portal;
	}

	@Override
	public void setPortal(GelPortalBlock portal)
	{
		this.portal = portal;
	}

	@Nullable
	@Internal
	public static GelPortalBlock getPortalClient()
	{
		return GelEntity.portalClient;
	}

	@Internal
	public static void setPortalClient(Entity entity, GelPortalBlock portalClient)
	{
		if (entity.world.isRemote)
			if (net.minecraft.client.Minecraft.getInstance().player == entity)
				GelEntity.portalClient = portalClient;
	}
}

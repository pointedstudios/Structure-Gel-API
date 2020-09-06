package com.legacy.structure_gel.util.capability;

import javax.annotation.Nullable;

import com.legacy.structure_gel.blocks.GelPortalBlock;

public class GelEntity implements IGelEntity
{
	@Nullable
	private GelPortalBlock portal;

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

}
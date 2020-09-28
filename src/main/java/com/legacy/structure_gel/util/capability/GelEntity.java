package com.legacy.structure_gel.util.capability;

import javax.annotation.Nullable;

import com.legacy.structure_gel.blocks.GelPortalBlock;

public class GelEntity implements IGelEntity
{
	@Nullable
	private GelPortalBlock portal;
	@Nullable
	private GelPortalBlock portalVisual;
	@Nullable
	private GelPortalBlock portalAudio;

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
	@Override
	public GelPortalBlock getPortalVisual()
	{
		return this.portalVisual;
	}

	@Override
	public void setPortalVisual(GelPortalBlock prevPortal)
	{
		this.portalVisual = prevPortal;
	}
	
	@Nullable
	@Override
	public GelPortalBlock getPortalAudio()
	{
		return this.portalAudio;
	}

	@Override
	public void setPortalAudio(GelPortalBlock portalAudio)
	{
		this.portalAudio = portalAudio;
	}
}

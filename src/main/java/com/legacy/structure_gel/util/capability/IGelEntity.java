package com.legacy.structure_gel.util.capability;

import javax.annotation.Nullable;

import com.legacy.structure_gel.blocks.GelPortalBlock;

public interface IGelEntity
{
	@Nullable
	GelPortalBlock getPortal();

	void setPortal(GelPortalBlock portal);

}

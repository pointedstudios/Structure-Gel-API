package com.legacy.structure_gel.util.capability;

import com.legacy.structure_gel.blocks.GelPortalBlock;

import javax.annotation.Nullable;

/**
 * Data for all entities allowing for custom portal logic.
 *
 * @author David
 */
public interface IGelEntity
{
	/**
	 * The portal that the entity last stepped into. Used for handling teleportation
	 * logic.
	 *
	 * @return {@link GelPortalBlock}
	 */
	@Nullable
	GelPortalBlock getPortal();

	/**
	 * @param portal
	 * @see #getPortal()
	 */
	void setPortal(GelPortalBlock portal);
}

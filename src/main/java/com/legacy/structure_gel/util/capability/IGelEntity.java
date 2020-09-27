package com.legacy.structure_gel.util.capability;

import javax.annotation.Nullable;

import com.legacy.structure_gel.blocks.GelPortalBlock;

/**
 * Data for all entities allowing for custom portal logic.
 * 
 * @author David
 *
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
	 * @see #getPortal()
	 * @param portal
	 */
	void setPortal(GelPortalBlock portal);

	/**
	 * The portal that the entity last stepped into. Used for rendering gui
	 * overlays.
	 * 
	 * @return {@link GelPortalBlock}
	 */
	@Nullable
	GelPortalBlock getPrevPortal();

	/**
	 * @see #getPrevPortal()
	 * @param portal
	 */
	void setPrevPortal(GelPortalBlock portal);
}

package com.legacy.structure_gel.structures;

import net.minecraft.world.gen.feature.template.PlacementSettings;

/**
 * Literally just exists to let you determine how waterloggable blocks should
 * generate when placed in water.
 * 
 * @author David
 *
 */
public class GelPlacementSettings extends PlacementSettings
{
	/**
	 * Determines if waterloggable blocks placed in water should waterlog. The
	 * vanilla method is obfuscated and the field is private, so here you go.
	 * 
	 * @param maintainWater: default=true
	 * @return {@link GelPlacementSettings}
	 */
	public GelPlacementSettings setMaintainWater(boolean maintainWater)
	{
		this.field_204765_h = maintainWater;
		return this;
	}
}

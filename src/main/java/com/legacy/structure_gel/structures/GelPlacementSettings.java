package com.legacy.structure_gel.structures;

import net.minecraft.world.gen.feature.template.PlacementSettings;

public class GelPlacementSettings extends PlacementSettings
{
	public GelPlacementSettings()
	{
		
	}

	/**
	 * Determines if waterloggable blocks placed in water should waterlog.
	 * @param maintainWater: default=true
	 * @return
	 */
	public GelPlacementSettings setMaintainWater(boolean maintainWater)
	{
		this.field_204765_h = maintainWater;
		return this;
	}
}

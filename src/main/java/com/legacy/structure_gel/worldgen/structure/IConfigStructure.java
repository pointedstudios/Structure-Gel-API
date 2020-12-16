package com.legacy.structure_gel.worldgen.structure;

import com.legacy.structure_gel.util.ConfigTemplates;
import com.legacy.structure_gel.util.ConfigTemplates.StructureConfig;

/**
 * Used for structures that implement configuration files from
 * {@link ConfigTemplates}.
 *
 * @author David
 */
public interface IConfigStructure
{
	/**
	 * Returns the config file for this structure.
	 *
	 * @return {@link StructureConfig}
	 */
	StructureConfig getConfig();
}

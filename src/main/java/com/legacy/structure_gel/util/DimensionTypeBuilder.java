package com.legacy.structure_gel.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;

import java.util.OptionalLong;

/**
 * A way of creating a {@link DimensionType} in a builder format. All values are
 * defaulted to what they would be for the overworld.
 *
 * @author David
 */
public class DimensionTypeBuilder
{
	private OptionalLong fixedTime = OptionalLong.empty();
	private boolean hasSkyLight = true;
	private boolean hasCeiling = false;
	private boolean ultrawarm = false;
	private boolean natural = true;
	private double coordinateScale = 1.0;
	private boolean hasDragonFight = false;
	private boolean piglinSafe = false;
	private boolean bedWorks = true;
	private boolean respawnAnchorWorks = false;
	private boolean hasRaids = true;
	private int logicalHeight = 256;
	private IBiomeMagnifier magnifier = ColumnFuzzedBiomeMagnifier.INSTANCE;
	private ResourceLocation infiniburn = BlockTags.INFINIBURN_OVERWORLD.getName();
	private ResourceLocation effects = DimensionType.OVERWORLD_ID;
	private float ambientLight = 0.0F;

	@Internal
	private DimensionTypeBuilder()
	{

	}

	/**
	 * Creates a new instance of the builder.
	 *
	 * @return {@link DimensionTypeBuilder}
	 */
	public static DimensionTypeBuilder of()
	{
		return new DimensionTypeBuilder();
	}

	/**
	 * Locks the sun at a specific time.
	 *
	 * @param fixedTime
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder fixedTime(OptionalLong fixedTime)
	{
		this.fixedTime = fixedTime;
		return this;
	}

	/**
	 * Determines if things that require the sky can occur, such as daylight
	 * detectors and phantom spawning.
	 *
	 * @param hasSkyLight
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder hasSkyLight(boolean hasSkyLight)
	{
		this.hasSkyLight = hasSkyLight;
		return this;
	}

	/**
	 * Determines a few things in mob spawning for if your world has a ceiling.
	 *
	 * @param hasCeiling
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder hasCeiling(boolean hasCeiling)
	{
		this.hasCeiling = hasCeiling;
		return this;
	}

	/**
	 * Causes water to evaporate and lava to flow further.
	 *
	 * @param ultrawarm
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder ultrawarm(boolean ultrawarm)
	{
		this.ultrawarm = ultrawarm;
		return this;
	}

	/**
	 * When false, compasses and clocks don't function.<br>
	 * When true, nether portals spawn zombified piglins.
	 *
	 * @param natural
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder natural(boolean natural)
	{
		this.natural = natural;
		return this;
	}

	/**
	 * A multiplier applied to coordinates when traveling between dimensions.
	 *
	 * @param coordinateScale
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder coordinateScale(double coordinateScale)
	{
		this.coordinateScale = coordinateScale;
		return this;
	}

	/**
	 * Allows the dragon fight to function properly when set to true.
	 *
	 * @param hasDragonFight
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder hasDragonFight(boolean hasDragonFight)
	{
		this.hasDragonFight = hasDragonFight;
		return this;
	}

	/**
	 * When set to false, piglins will zombify in the dimension.
	 *
	 * @param piglinSafe
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder piglinSafe(boolean piglinSafe)
	{
		this.piglinSafe = piglinSafe;
		return this;
	}

	/**
	 * Determines if you're allowed to use a bed.
	 *
	 * @param bedWorks
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder bedWorks(boolean bedWorks)
	{
		this.bedWorks = bedWorks;
		return this;
	}

	/**
	 * Determines if you're allowed to use a respawn anchor.
	 *
	 * @param respawnAnchorWorks
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder respawnAnchorWorks(boolean respawnAnchorWorks)
	{
		this.respawnAnchorWorks = respawnAnchorWorks;
		return this;
	}

	/**
	 * Determines if players with bad omen can trigger a raid.
	 *
	 * @param hasRaids
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder hasRaids(boolean hasRaids)
	{
		this.hasRaids = hasRaids;
		return this;
	}

	/**
	 * The maximum height that various mechanics are allowed to send the player.
	 * This is to prevent players from getting stuck on the nether roof.
	 *
	 * @param logicalHeight
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder logicalHeight(int logicalHeight)
	{
		this.logicalHeight = logicalHeight;
		return this;
	}

	/**
	 * Used in world gen.
	 *
	 * @param magnifier
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder magnifier(IBiomeMagnifier magnifier)
	{
		this.magnifier = magnifier;
		return this;
	}

	/**
	 * A tag of blocks that are allowed to burn forever in the dimension.
	 *
	 * @param infiniburn
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder infiniburn(ResourceLocation infiniburn)
	{
		this.infiniburn = infiniburn;
		return this;
	}

	/**
	 * Sets what effects the dimension has such as fog. "effects" is a registry id
	 * in {@link net.minecraft.client.world.DimensionRenderInfo}.
	 *
	 * @param effects
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder effects(ResourceLocation effects)
	{
		this.effects = effects;
		return this;
	}

	/**
	 * Determins how blocks should render. It's what makes the underside of blocks
	 * brighter in the nether.
	 *
	 * @param ambientLight
	 * @return {@link DimensionTypeBuilder}
	 */
	public DimensionTypeBuilder ambientLight(float ambientLight)
	{
		this.ambientLight = ambientLight;
		return this;
	}

	/**
	 * Creates the {@link DimensionType} with the provided settings.
	 *
	 * @return {@link DimensionType}
	 */
	public DimensionType build()
	{
		return new DimensionType(fixedTime, hasSkyLight, hasCeiling, ultrawarm, natural, coordinateScale, hasDragonFight, piglinSafe, bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, magnifier, infiniburn, effects, ambientLight);
	}
}

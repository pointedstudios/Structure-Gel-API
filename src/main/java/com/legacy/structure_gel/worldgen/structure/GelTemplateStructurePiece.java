package com.legacy.structure_gel.worldgen.structure;

import com.legacy.structure_gel.worldgen.GelPlacementSettings;
import com.legacy.structure_gel.worldgen.processors.RemoveGelStructureProcessor;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * An extension of {@link TemplateStructurePiece} with more extensible methods
 * and compatibility with gel blocks.
 * 
 * @author David
 *
 */
public abstract class GelTemplateStructurePiece extends TemplateStructurePiece
{
	/**
	 * The location of your structure in the data folder.
	 */
	public final ResourceLocation name;
	public Rotation rotation = Rotation.NONE;
	public Mirror mirror = Mirror.NONE;

	/**
	 * This constructor is called when creating a new instance of a piece for the
	 * first time. Your constructor should look something like this.<br>
	 * <br>
	 * public Piece(TemplateManager templateManager, ResourceLocation name)<br>
	 * {<br>
	 * super(YOUR_PIECE_TYPE, name);<br>
	 * SET_YOUR_FIELDS_HERE<br>
	 * this.setupTemplate(templateManager);<br>
	 * }<br>
	 * 
	 * @param structurePieceTypeIn
	 * @param componentTypeIn A marker that allows for different behavior in the
	 *            constructor. You may not need to use this.
	 */
	public GelTemplateStructurePiece(IStructurePieceType structurePieceTypeIn, ResourceLocation name, int componentTypeIn)
	{
		super(structurePieceTypeIn, componentTypeIn);
		this.name = name;
	}

	/**
	 * This consturctor is used to read existing template data from the world's save
	 * data. All values should be stored in nbt.
	 * 
	 * @see #readAdditional(CompoundNBT)
	 * 
	 * @param structurePieceType
	 * @param nbt
	 */
	public GelTemplateStructurePiece(IStructurePieceType structurePieceType, CompoundNBT nbt)
	{
		super(structurePieceType, nbt);
		this.name = new ResourceLocation(nbt.getString("Template"));
		this.rotation = Rotation.valueOf(nbt.getString("Rot"));
		this.mirror = Mirror.valueOf(nbt.getString("Mirror"));
	}

	/**
	 * Sets up the template data and placement settings.
	 * 
	 * @param templateManager
	 */
	public void setupTemplate(TemplateManager templateManager)
	{
		this.setup(templateManager.getTemplateDefaulted(this.name), this.templatePosition, getPlacementSettings(templateManager));
	}

	/**
	 * Sets up the placement settings for this piece instance.
	 * 
	 * @param templateManager
	 * @return {@link PlacementSettings}
	 */
	public PlacementSettings getPlacementSettings(TemplateManager templateManager)
	{
		PlacementSettings placementSettings = createPlacementSettings(templateManager);
		addProcessors(templateManager, placementSettings);
		return placementSettings;
	}

	/**
	 * Creates a new {@link PlacementSettings} instance.
	 * 
	 * @see #getPlacementSettings(TemplateManager)
	 * @param templateManager
	 * @return {@link PlacementSettings}
	 */
	public PlacementSettings createPlacementSettings(TemplateManager templateManager)
	{
		return new GelPlacementSettings().setMaintainWater(true).setRotation(this.rotation).setMirror(this.mirror);
	}

	/**
	 * Override this to add processors to placementSettings.
	 * 
	 * @see #getPlacementSettings(TemplateManager)
	 * @param templateManager
	 * @param placementSettings
	 */
	public void addProcessors(TemplateManager templateManager, PlacementSettings placementSettings)
	{
		placementSettings.addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
		placementSettings.addProcessor(RemoveGelStructureProcessor.INSTANCE);
	}

	/**
	 * Use this to store data about this piece instance into nbt. This will be
	 * loaded later when the world needs it.
	 * 
	 * @see #GelTemplateStructurePiece(IStructurePieceType, ResourceLocation,
	 *      CompoundNBT)
	 * 
	 * @param nbt
	 */
	@Override
	protected void readAdditional(CompoundNBT nbt)
	{
		super.readAdditional(nbt);
		nbt.putString("Template", this.name.toString());
		nbt.putString("Rot", this.rotation.name());
		nbt.putString("Mirror", this.mirror.name());
	}
}

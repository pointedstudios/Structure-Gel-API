package com.legacy.structure_gel.worldgen.structure;

import com.legacy.structure_gel.worldgen.GelPlacementSettings;
import com.legacy.structure_gel.worldgen.GelTemplate;
import com.legacy.structure_gel.worldgen.IModifyState;
import com.legacy.structure_gel.worldgen.processors.RemoveGelStructureProcessor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * An extension of {@link TemplateStructurePiece} with more extensible methods
 * and compatibility with gel blocks.<br>
 * <br>
 * Includes the following:<br>
 * - Built in variables for name (structure file location), rotation, and
 * mirroring. Includes NBT handling.<br>
 * - Methods to simplify adding processors and setting PlacementSettings.<br>
 * - Block placement overrides, separate from processors. See
 * {@link IModifyState}<br>
 * - Fixes for entity rotation within the structure.
 *
 * @author David
 */
public abstract class GelTemplateStructurePiece extends TemplateStructurePiece implements IModifyState
{
	private static final Logger LOGGER = LogManager.getLogger("ModdingLegacy/StructureGel/GelTemplateStructurePiece");

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
	 * @param componentTypeIn      A marker that allows for different behavior in the
	 *                             constructor. You may not need to use this.
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
	 * @param structurePieceType
	 * @param nbt
	 * @see #readAdditional(CompoundNBT)
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
	 * @param templateManager
	 * @return {@link PlacementSettings}
	 * @see #getPlacementSettings(TemplateManager)
	 */
	public PlacementSettings createPlacementSettings(TemplateManager templateManager)
	{
		return new GelPlacementSettings().setMaintainWater(true).setRotation(this.rotation).setMirror(this.mirror);
	}

	/**
	 * Override this to add processors to placementSettings.
	 *
	 * @param templateManager
	 * @param placementSettings
	 * @see #getPlacementSettings(TemplateManager)
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
	 * @param nbt
	 * @see #GelTemplateStructurePiece(IStructurePieceType, CompoundNBT)
	 */
	@Override
	protected void readAdditional(CompoundNBT nbt)
	{
		super.readAdditional(nbt);
		nbt.putString("Template", this.name.toString());
		nbt.putString("Rot", this.rotation.name());
		nbt.putString("Mirror", this.mirror.name());
	}

	/**
	 * Override of the vanilla method that places blocks from a structure into the
	 * world with added hooks for special conditions.
	 *
	 * @see TemplateStructurePiece#func_230383_a_(ISeedReader, StructureManager, ChunkGenerator, Random, MutableBoundingBox, ChunkPos, BlockPos)
	 */
	@Override
	public boolean func_230383_a_(ISeedReader world, StructureManager structureManager, ChunkGenerator chunkGenerator, Random rand, MutableBoundingBox bounds, ChunkPos chunkPos, BlockPos pos)
	{
		this.placeSettings.setBoundingBox(bounds);
		this.boundingBox = this.template.getMutableBoundingBox(this.placeSettings, this.templatePosition);
		if (new GelTemplate(this.template).func_237146_a_(world, this.templatePosition, pos, this.placeSettings, rand, 2, this::modifyState))
		{
			for (Template.BlockInfo blockInfo : this.template.func_215381_a(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK))
				if (blockInfo.nbt != null)
					if (StructureMode.valueOf(blockInfo.nbt.getString("mode")) == StructureMode.DATA)
						this.handleDataMarker(blockInfo.nbt.getString("metadata"), blockInfo.pos, world, rand, bounds);

			for (Template.BlockInfo blockInfo : this.template.func_215381_a(this.templatePosition, this.placeSettings, Blocks.JIGSAW))
			{
				if (blockInfo.nbt != null)
				{
					String stateString = blockInfo.nbt.getString("final_state");
					BlockStateParser stateParser = new BlockStateParser(new StringReader(stateString), false);
					BlockState state = Blocks.AIR.getDefaultState();

					try
					{
						stateParser.parse(true);
						BlockState parsedState = stateParser.getState();
						if (parsedState != null)
							state = parsedState;
						else
							LOGGER.error("Error while parsing blockstate {} in structure block @ {}", stateString, blockInfo.pos);
					}
					catch (CommandSyntaxException ex)
					{
						LOGGER.error("Error while parsing blockstate {} in structure block @ {}", stateString, blockInfo.pos);
					}

					world.setBlockState(blockInfo.pos, state, 3);
				}
			}
		}

		return true;
	}

	/**
	 * Modifies the state passed in based on the structure's rules. This method is
	 * called after processors are applied.<br>
	 * <br>
	 * Return null to prevent placement.
	 *
	 * @param world
	 * @param rand
	 * @param pos
	 * @param originalState
	 * @return {@link BlockState}
	 */
	@Override
	@Nullable
	public BlockState modifyState(IServerWorld world, Random rand, BlockPos pos, BlockState originalState)
	{
		return originalState;
	}
}

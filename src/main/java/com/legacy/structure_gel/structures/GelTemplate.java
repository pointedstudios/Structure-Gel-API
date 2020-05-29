package com.legacy.structure_gel.structures;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;

/**
 * Lardly copying from the vanilla Template class to fix entity rotations. Used
 * internally.
 * 
 * @author David
 *
 */
public class GelTemplate extends Template
{
	Template template;
	
	public GelTemplate(Template template)
	{
		this.template = template;
	}
	
	public boolean addBlocksToWorld(IWorld worldIn, BlockPos pos, PlacementSettings placementIn, int flags)
	{
		if (this.template.blocks.isEmpty())
		{
			return false;
		}
		else
		{
			List<Template.BlockInfo> list = placementIn.func_227459_a_(this.template.blocks, pos);
			if ((!list.isEmpty() || !placementIn.getIgnoreEntities() && !this.template.entities.isEmpty()) && this.template.getSize().getX() >= 1 && this.template.getSize().getY() >= 1 && this.template.getSize().getZ() >= 1)
			{
				MutableBoundingBox mutableboundingbox = placementIn.getBoundingBox();
				List<BlockPos> waterLoggablePoses = Lists.newArrayListWithCapacity(placementIn.func_204763_l() ? list.size() : 0);
				List<Pair<BlockPos, CompoundNBT>> blockInfos = Lists.newArrayListWithCapacity(list.size());
				int i = Integer.MAX_VALUE;
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MIN_VALUE;
				int i1 = Integer.MIN_VALUE;
				int j1 = Integer.MIN_VALUE;

				for (Template.BlockInfo blockInfo : processBlockInfos(this.template, worldIn, pos, placementIn, list))
				{
					BlockPos blockPos = blockInfo.pos;
					if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockPos))
					{
						IFluidState iFluidState = placementIn.func_204763_l() ? worldIn.getFluidState(blockPos) : null;
						BlockState blockState = blockInfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
						if (blockInfo.nbt != null)
						{
							TileEntity tileEntity = worldIn.getTileEntity(blockPos);
							IClearable.clearObj(tileEntity);
							worldIn.setBlockState(blockPos, Blocks.BARRIER.getDefaultState(), 20);
						}

						if (worldIn.setBlockState(blockPos, blockState, flags))
						{
							i = Math.min(i, blockPos.getX());
							j = Math.min(j, blockPos.getY());
							k = Math.min(k, blockPos.getZ());
							l = Math.max(l, blockPos.getX());
							i1 = Math.max(i1, blockPos.getY());
							j1 = Math.max(j1, blockPos.getZ());
							blockInfos.add(Pair.of(blockPos, blockInfo.nbt));
							if (blockInfo.nbt != null)
							{
								TileEntity tileEntity1 = worldIn.getTileEntity(blockPos);
								if (tileEntity1 != null)
								{
									blockInfo.nbt.putInt("x", blockPos.getX());
									blockInfo.nbt.putInt("y", blockPos.getY());
									blockInfo.nbt.putInt("z", blockPos.getZ());
									tileEntity1.read(blockInfo.nbt);
									tileEntity1.mirror(placementIn.getMirror());
									tileEntity1.rotate(placementIn.getRotation());
								}
							}

							if (iFluidState != null && blockState.getBlock() instanceof ILiquidContainer)
							{
								((ILiquidContainer) blockState.getBlock()).receiveFluid(worldIn, blockPos, blockState, iFluidState);
								if (!iFluidState.isSource())
								{
									waterLoggablePoses.add(blockPos);
								}
							}
						}
					}
				}

				boolean flag = true;
				Direction[] directions = new Direction[] { Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

				while (flag && !waterLoggablePoses.isEmpty())
				{
					flag = false;
					Iterator<BlockPos> iterator = waterLoggablePoses.iterator();

					while (iterator.hasNext())
					{
						BlockPos blockPos = iterator.next();
						BlockPos blockPosCopy = blockPos;
						IFluidState iFluidState = worldIn.getFluidState(blockPos);

						for (int k1 = 0; k1 < directions.length && !iFluidState.isSource(); ++k1)
						{
							BlockPos offsetPos = blockPosCopy.offset(directions[k1]);
							IFluidState offsetFluidState = worldIn.getFluidState(offsetPos);
							if (offsetFluidState.getActualHeight(worldIn, offsetPos) > iFluidState.getActualHeight(worldIn, blockPosCopy) || offsetFluidState.isSource() && !iFluidState.isSource())
							{
								iFluidState = offsetFluidState;
								blockPosCopy = offsetPos;
							}
						}

						if (iFluidState.isSource())
						{
							BlockState state = worldIn.getBlockState(blockPos);
							Block block = state.getBlock();
							if (block instanceof ILiquidContainer)
							{
								((ILiquidContainer) block).receiveFluid(worldIn, blockPos, state, iFluidState);
								flag = true;
								iterator.remove();
							}
						}
					}
				}

				if (i <= l)
				{
					if (!placementIn.func_215218_i())
					{
						VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(l - i + 1, i1 - j + 1, j1 - k + 1);
						int l1 = i;
						int i2 = j;
						int j2 = k;

						for (Pair<BlockPos, CompoundNBT> pair1 : blockInfos)
						{
							BlockPos blockpos5 = pair1.getFirst();
							voxelshapepart.setFilled(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2, true, true);
						}

						func_222857_a(worldIn, flags, voxelshapepart, l1, i2, j2);
					}

					for (Pair<BlockPos, CompoundNBT> pair : blockInfos)
					{
						BlockPos blockpos4 = pair.getFirst();
						if (!placementIn.func_215218_i())
						{
							BlockState blockstate1 = worldIn.getBlockState(blockpos4);
							BlockState blockstate3 = Block.getValidBlockForPosition(blockstate1, worldIn, blockpos4);
							if (blockstate1 != blockstate3)
							{
								worldIn.setBlockState(blockpos4, blockstate3, flags & -2 | 16);
							}

							worldIn.notifyNeighbors(blockpos4, blockstate3.getBlock());
						}

						if (pair.getSecond() != null)
						{
							TileEntity tileentity2 = worldIn.getTileEntity(blockpos4);
							if (tileentity2 != null)
							{
								tileentity2.markDirty();
							}
						}
					}
				}

				if (!placementIn.getIgnoreEntities())
				{
					this.addEntitiesToWorld(worldIn, pos, placementIn, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset(), placementIn.getBoundingBox());
				}

				return true;
			}
			else
			{
				return false;
			}
		}
	}

	public void addEntitiesToWorld(IWorld worldIn, BlockPos offsetPos, PlacementSettings placementIn, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset, @Nullable MutableBoundingBox boundsIn)
	{
		for (Template.EntityInfo entityInfo : processEntityInfos(this, worldIn, offsetPos, placementIn, this.template.entities))
		{
			BlockPos blockpos = getTransformedPos(entityInfo.blockPos, mirrorIn, rotationIn, centerOffset).add(offsetPos);
			blockpos = entityInfo.blockPos;
			if (boundsIn == null || boundsIn.isVecInside(blockpos))
			{
				CompoundNBT compoundnbt = entityInfo.nbt;
				Vec3d vec3d = getTransformedPos(entityInfo.pos, mirrorIn, rotationIn, centerOffset);
				vec3d = vec3d.add((double) offsetPos.getX(), (double) offsetPos.getY(), (double) offsetPos.getZ());
				Vec3d vec3d1 = entityInfo.pos;
				ListNBT listnbt = new ListNBT();
				listnbt.add(DoubleNBT.valueOf(vec3d1.x));
				listnbt.add(DoubleNBT.valueOf(vec3d1.y));
				listnbt.add(DoubleNBT.valueOf(vec3d1.z));
				compoundnbt.put("Pos", listnbt);
				compoundnbt.remove("UUIDMost");
				compoundnbt.remove("UUIDLeast");
				loadEntity(worldIn, compoundnbt).ifPresent((entity) ->
				{
					// entity.getMirroredYaw(Mirror) has it's mirroring flipped, so I flip it here to correct that.
					//float yaw = entity.getMirroredYaw(mirrorIn);
					Mirror oppositeMirror = mirrorIn == Mirror.FRONT_BACK ? Mirror.LEFT_RIGHT : (mirrorIn == Mirror.LEFT_RIGHT ? Mirror.FRONT_BACK : Mirror.NONE);
					float yaw = entity.getMirroredYaw(oppositeMirror);
					
					// The rotation part of this rotates in the wrong direction, so I correct that here.
					// yaw = yaw + (entity.rotationYaw - entity.getRotatedYaw(rotationIn));
					float rotation = 0;
					switch (rotationIn)
					{
					case CLOCKWISE_90:
						rotation = 90;
						break;
					case CLOCKWISE_180:
						rotation = 180;
						break;
					case COUNTERCLOCKWISE_90:
						rotation = -90;
						break;
					default:
						rotation = 0;
						break;
					}
					yaw = yaw + rotation;
					entity.setLocationAndAngles(vec3d1.x, vec3d1.y, vec3d1.z, yaw, entity.rotationPitch);
					worldIn.addEntity(entity);
				});
			}
		}
	}
}

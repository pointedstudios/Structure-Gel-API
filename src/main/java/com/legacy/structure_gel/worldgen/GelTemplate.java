package com.legacy.structure_gel.worldgen;

import com.google.common.collect.Lists;
import com.legacy.structure_gel.util.Internal;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This class largly exists to fix a silly bug with how entities rotate when
 * placed in a structure. You won't need to mess with it, but it only works for
 * structures.
 *
 * @author David
 */
@Internal
public class GelTemplate extends Template
{
	public final Template template;

	@Internal
	public GelTemplate(Template template)
	{
		this.template = template;
	}

	/**
	 * Cloned vanilla method to fix entity rotations
	 */
	// addBlocksToWorld
	public boolean func_237146_a_(IServerWorld worldIn, BlockPos pos, BlockPos pos2, PlacementSettings placementSettings, Random rand, int flags, IModifyState shouldPlace)
	{
		if (this.template.blocks.isEmpty())
		{
			return false;
		}
		else
		{
			List<Template.BlockInfo> list = placementSettings.func_237132_a_(this.template.blocks, pos).func_237157_a_();
			if ((!list.isEmpty() || !placementSettings.getIgnoreEntities() && !this.template.entities.isEmpty()) && this.template.getSize().getX() >= 1 && this.template.getSize().getY() >= 1 && this.template.getSize().getZ() >= 1)
			{
				MutableBoundingBox mutableboundingbox = placementSettings.getBoundingBox();
				List<BlockPos> list1 = Lists.newArrayListWithCapacity(placementSettings.func_204763_l() ? list.size() : 0);
				List<Pair<BlockPos, CompoundNBT>> list2 = Lists.newArrayListWithCapacity(list.size());
				int i = Integer.MAX_VALUE;
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MIN_VALUE;
				int i1 = Integer.MIN_VALUE;
				int j1 = Integer.MIN_VALUE;

				for (Template.BlockInfo template$blockinfo : processBlockInfos(worldIn, pos, pos2, placementSettings, list, this.template))
				{
					BlockPos blockpos = template$blockinfo.pos;
					if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos))
					{
						FluidState fluidstate = placementSettings.func_204763_l() ? worldIn.getFluidState(blockpos) : null;
						// Hook here with shouldPlace
						BlockState blockstate = shouldPlace.modifyState(worldIn, rand, blockpos, template$blockinfo.state.mirror(placementSettings.getMirror()).rotate(worldIn, pos, placementSettings.getRotation()));
						if (blockstate != null)
						{
							if (template$blockinfo.nbt != null)
							{
								TileEntity tileentity = worldIn.getTileEntity(blockpos);
								IClearable.clearObj(tileentity);
								worldIn.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 20);
							}

							if (worldIn.setBlockState(blockpos, blockstate, flags))
							{
								i = Math.min(i, blockpos.getX());
								j = Math.min(j, blockpos.getY());
								k = Math.min(k, blockpos.getZ());
								l = Math.max(l, blockpos.getX());
								i1 = Math.max(i1, blockpos.getY());
								j1 = Math.max(j1, blockpos.getZ());
								list2.add(Pair.of(blockpos, template$blockinfo.nbt));
								if (template$blockinfo.nbt != null)
								{
									TileEntity tileentity1 = worldIn.getTileEntity(blockpos);
									if (tileentity1 != null)
									{
										template$blockinfo.nbt.putInt("x", blockpos.getX());
										template$blockinfo.nbt.putInt("y", blockpos.getY());
										template$blockinfo.nbt.putInt("z", blockpos.getZ());
										if (tileentity1 instanceof LockableLootTileEntity)
										{
											template$blockinfo.nbt.putLong("LootTableSeed", rand.nextLong());
										}

										tileentity1.read(template$blockinfo.state, template$blockinfo.nbt);
										tileentity1.mirror(placementSettings.getMirror());
										tileentity1.rotate(placementSettings.getRotation());
									}
								}

								if (fluidstate != null && blockstate.getBlock() instanceof ILiquidContainer)
								{
									((ILiquidContainer) blockstate.getBlock()).receiveFluid(worldIn, blockpos, blockstate, fluidstate);
									if (!fluidstate.isSource())
									{
										list1.add(blockpos);
									}
								}
							}
						}
					}
				}

				boolean flag = true;
				Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

				while (flag && !list1.isEmpty())
				{
					flag = false;
					Iterator<BlockPos> iterator = list1.iterator();

					while (iterator.hasNext())
					{
						BlockPos blockpos2 = iterator.next();
						BlockPos blockpos3 = blockpos2;
						FluidState fluidstate2 = worldIn.getFluidState(blockpos2);

						for (int k1 = 0; k1 < adirection.length && !fluidstate2.isSource(); ++k1)
						{
							BlockPos blockpos1 = blockpos3.offset(adirection[k1]);
							FluidState fluidstate1 = worldIn.getFluidState(blockpos1);
							if (fluidstate1.getActualHeight(worldIn, blockpos1) > fluidstate2.getActualHeight(worldIn, blockpos3) || fluidstate1.isSource() && !fluidstate2.isSource())
							{
								fluidstate2 = fluidstate1;
								blockpos3 = blockpos1;
							}
						}

						if (fluidstate2.isSource())
						{
							BlockState blockstate2 = worldIn.getBlockState(blockpos2);
							Block block = blockstate2.getBlock();
							if (block instanceof ILiquidContainer)
							{
								((ILiquidContainer) block).receiveFluid(worldIn, blockpos2, blockstate2, fluidstate2);
								flag = true;
								iterator.remove();
							}
						}
					}
				}

				if (i <= l)
				{
					if (!placementSettings.func_215218_i())
					{
						VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(l - i + 1, i1 - j + 1, j1 - k + 1);
						int l1 = i;
						int i2 = j;
						int j2 = k;

						for (Pair<BlockPos, CompoundNBT> pair1 : list2)
						{
							BlockPos blockpos5 = pair1.getFirst();
							voxelshapepart.setFilled(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2, true, true);
						}

						func_222857_a(worldIn, flags, voxelshapepart, l1, i2, j2);
					}

					for (Pair<BlockPos, CompoundNBT> pair : list2)
					{
						BlockPos blockpos4 = pair.getFirst();
						if (!placementSettings.func_215218_i())
						{
							BlockState blockstate1 = worldIn.getBlockState(blockpos4);
							BlockState blockstate3 = Block.getValidBlockForPosition(blockstate1, worldIn, blockpos4);
							if (blockstate1 != blockstate3)
							{
								worldIn.setBlockState(blockpos4, blockstate3, flags & -2 | 16);
							}

							worldIn.func_230547_a_(blockpos4, blockstate3.getBlock());
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

				if (!placementSettings.getIgnoreEntities())
				{
					this.addEntitiesToWorld(worldIn, pos, placementSettings);
				}

				return true;
			}
			else
			{
				return false;
			}
		}
	}

	/**
	 * Modified vanilla method to fix rotations
	 *
	 * @param worldIn
	 * @param pos
	 * @param placementIn
	 */
	private void addEntitiesToWorld(IServerWorld worldIn, BlockPos pos, PlacementSettings placementIn)
	{
		for (Template.EntityInfo entityInfo : processEntityInfos(this.template, worldIn, pos, placementIn, this.template.entities))
		{
			BlockPos blockpos = getTransformedPos(entityInfo.blockPos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset()).add(pos);
			blockpos = entityInfo.blockPos;
			if (placementIn.getBoundingBox() == null || placementIn.getBoundingBox().isVecInside(blockpos))
			{
				CompoundNBT compoundnbt = entityInfo.nbt.copy();
				Vector3d vec3d = entityInfo.pos;
				ListNBT listnbt = new ListNBT();
				listnbt.add(DoubleNBT.valueOf(vec3d.x));
				listnbt.add(DoubleNBT.valueOf(vec3d.y));
				listnbt.add(DoubleNBT.valueOf(vec3d.z));
				compoundnbt.put("Pos", listnbt);
				compoundnbt.remove("UUID");
				Template.loadEntity(worldIn, compoundnbt).ifPresent((entity) ->
				{
					// entity.getMirroredYaw(Mirror) has it's mirroring flipped, so I flip it here
					// to correct that.
					// float yaw = entity.getMirroredYaw(mirrorIn);
					Mirror oppositeMirror = placementIn.getMirror() == Mirror.FRONT_BACK ? Mirror.LEFT_RIGHT : (placementIn.getMirror() == Mirror.LEFT_RIGHT ? Mirror.FRONT_BACK : Mirror.NONE);
					float yaw = entity.getMirroredYaw(oppositeMirror);

					// The rotation part of this rotates in the wrong direction, so I correct that
					// here.
					// yaw = yaw + (entity.rotationYaw - entity.getRotatedYaw(rotationIn));
					float rotation = 0;
					switch (placementIn.getRotation())
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
					entity.setLocationAndAngles(vec3d.x, vec3d.y, vec3d.z, yaw, entity.rotationPitch);
					if (placementIn.func_237134_m_() && entity instanceof MobEntity)
					{
						((MobEntity) entity).onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(new BlockPos(vec3d)), SpawnReason.STRUCTURE, null, compoundnbt);
					}

					worldIn.addEntity(entity);
				});
			}
		}

	}
}

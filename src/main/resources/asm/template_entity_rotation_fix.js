function initializeCoreMod() {
	return {
		'template-entity-hook': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.world.gen.feature.template.Template',
				'methodName': 'func_207668_a', // addEntitiesToWorld
				'methodDesc': '(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/template/PlacementSettings;Lnet/minecraft/util/Mirror;Lnet/minecraft/util/Rotation;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/MutableBoundingBox;)V'
			},
			'transformer': function(methodNode) {
				var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				
				var target = methodNode.instructions.getFirst();
				var index = methodNode.instructions.indexOf(target);
				
				// Find first ALOAD 11 and ALOAD 1 if they're next to each other, operate around it, and exit the loop
				while (target !== null && index < methodNode.instructions.size()) {
					// Find an ALOAD 11 and operate if it has ALOAD 1 before it
					if (target.opcode === Opcodes.ALOAD && target.var === 11 && parseInt(index) > 0) {
						
						var prevInsn = methodNode.instructions.get(index - 1);
						if (prevInsn.opcode === Opcodes.ALOAD && prevInsn.var === 1) {
							
							var index = methodNode.instructions.indexOf(target);
							var inst = new InsnList();
							
							// + INVOKESTATIC com/legacy/structure_gel/asm/StructureGelHooks.lakeCheckForStructures(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/ChunkPos;)Z
							inst.add(new VarInsnNode(Opcodes.ALOAD, 13)); // Vec3d
							inst.add(new VarInsnNode(Opcodes.ALOAD, 4)); // Mirror
							inst.add(new VarInsnNode(Opcodes.ALOAD, 5)); // Rotation
							inst.add(ASM.buildMethodCall(
								'com/legacy/structure_gel/asm/StructureGelHooks',
								'templateEntityRotationFix',
								'(Lnet/minecraft/world/IWorld;Lnet/minecraft/nbt/CompoundNBT;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/Mirror;Lnet/minecraft/util/Rotation;)V',
								ASM.MethodType.STATIC
							));
							
							// Remove old
							var endIndex = methodNode.instructions.indexOf(ASM.findFirstInstructionAfter(methodNode, Opcodes.INVOKEVIRTUAL, index));
							for (i = index + 1; i <= endIndex; i++) {
								methodNode.instructions.remove(methodNode.instructions.get(index + 1));
							}
							
							// Insert the instructions
							methodNode.instructions.insert(target, inst);

							// Return the modified methodNode
							return methodNode;
						}
						
					}
					
					// Go to the next instruction
					index++;
					target = methodNode.instructions.get(index);
				}
				
				return methodNode;
			}
		}
	}
}
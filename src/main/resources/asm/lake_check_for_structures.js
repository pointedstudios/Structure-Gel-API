function initializeCoreMod() {
	return {
		'lake-hook': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.world.gen.feature.LakesFeature',
				'methodName': 'func_212245_a', // place
				'methodDesc': '(Lnet/minecraft/world/IWorld;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/LakesConfig;)Z'
			},
			'transformer': function(methodNode) {
				var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
				
				var target = methodNode.instructions.getFirst();
				var index = methodNode.instructions.indexOf(target);
				// Find first ALOAD 6, operate around it, and exit the loop
				while (target !== null && index < methodNode.instructions.size()) {
					// Find first ALOAD 6
					if (target.opcode === Opcodes.ALOAD && target.var === 6) {						
						var index = methodNode.instructions.indexOf(target);
						var inst = new InsnList();
						
						// + INVOKESTATIC com/legacy/structure_gel/asm/StructureGelHooks.lakeCheckForStructures(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/ChunkPos;)Z
						inst.add(ASM.buildMethodCall(
							'com/legacy/structure_gel/asm/StructureGelHooks',
							'lakeCheckForStructures',
							'(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/ChunkPos;)Z',
							ASM.MethodType.STATIC
						));
						
						// Remove old
						var endIndex = methodNode.instructions.indexOf(ASM.findFirstInstructionAfter(methodNode, Opcodes.IFNE, index));
						for (i = index + 1; i < endIndex; i++) {
							methodNode.instructions.remove(methodNode.instructions.get(index + 1));
						}
						
						// Insert the instructions
						methodNode.instructions.insert(target, inst);

						// Return the modified methodNode
						return methodNode;
						
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
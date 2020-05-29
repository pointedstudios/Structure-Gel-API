function initializeCoreMod() {
	return {
		'lake-hook': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.world.gen.feature.LakesFeature',
				'methodName': 'func_212245_a', // place
				'methodDesc': '(Lnet/minecraft/world/IWorld;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/BlockStateFeatureConfig;)Z'
			},
			'transformer': function(method) {
				var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
				
				var target = method.instructions.getFirst();
				var index = method.instructions.indexOf(target);
				// Find first ALOAD 6, operate around it, and exit the loop
				while (target !== null && index < method.instructions.size()) {
					// Find first ALOAD 6
					if (target.opcode === Opcodes.ALOAD && target.var === 6) {						
						var index = method.instructions.indexOf(target);
						var inst = new InsnList();
						
						// + INVOKESTATIC com/legacy/structure_gel/asm/StructureGelHooks.lakeCheckForStructures(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/ChunkPos;)Z
						inst.add(ASM.buildMethodCall(
							'com/legacy/structure_gel/asm/StructureGelHooks',
							'lakeCheckForStructures',
							'(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/ChunkPos;)Z',
							ASM.MethodType.STATIC
						));
						
						// Remove old
						var endIndex = method.instructions.indexOf(ASM.findFirstInstructionAfter(method, Opcodes.IFNE, index));
						for (i = index + 1; i < endIndex; i++) {
							method.instructions.remove(method.instructions.get(index + 1));
						}
						
						// Insert the instructions
						method.instructions.insert(target, inst);

						// Return the modified method
						return method;
						
					}
					
					// Go to the next instruction
					index++;
					target = method.instructions.get(index);
				}
				
				return method;
			}
		}
	}
}
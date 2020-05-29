function log(message) {
	var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
	ASM.log("INFO", message);
}

function logMethod(method) {
	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
	
	for (i = 0; i < method.instructions.size(); i++) {
		var ins = method.instructions.get(i);
		if (ins.opcode === Opcodes.NOP)
			log("NOP");
		else if (ins.opcode === Opcodes.ACONST_NULL)
			log("ACONST_NULL");
		else if (ins.opcode === Opcodes.ICONST_M1)
			log("ICONST_M1");
		else if (ins.opcode === Opcodes.ICONST_0)
			log("ICONST_0");
		else if (ins.opcode === Opcodes.ICONST_1)
			log("ICONST_1");
		else if (ins.opcode === Opcodes.ICONST_2)
			log("ICONST_2");
		else if (ins.opcode === Opcodes.ICONST_3)
			log("ICONST_3");
		else if (ins.opcode === Opcodes.ICONST_4)
			log("ICONST_4");
		else if (ins.opcode === Opcodes.ICONST_5)
			log("ICONST_5");
		else if (ins.opcode === Opcodes.LCONST_0)
			log("LCONST_0");
		else if (ins.opcode === Opcodes.LCONST_1)
			log("LCONST_1");
		else if (ins.opcode === Opcodes.FCONST_0)
			log("FCONST_0");
		else if (ins.opcode === Opcodes.FCONST_1)
			log("FCONST_1");
		else if (ins.opcode === Opcodes.FCONST_2)
			log("FCONST_2");
		else if (ins.opcode === Opcodes.DCONST_0)
			log("DCONST_0");
		else if (ins.opcode === Opcodes.DCONST_1)
			log("DCONST_1");
		else if (ins.opcode === Opcodes.BIPUSH)
			log("BIPUSH");
		else if (ins.opcode === Opcodes.SIPUSH)
			log("SIPUSH");
		else if (ins.opcode === Opcodes.LDC)
			log("LDC");
		else if (ins.opcode === Opcodes.ILOAD)
			log("ILOAD" + ins.var);
		else if (ins.opcode === Opcodes.LLOAD)
			log("LLOAD" + ins.var);
		else if (ins.opcode === Opcodes.FLOAD)
			log("FLOAD" + ins.var);
		else if (ins.opcode === Opcodes.DLOAD)
			log("DLOAD" + ins.var);
		else if (ins.opcode === Opcodes.ALOAD)
			log("ALOAD" + ins.var);
		else if (ins.opcode === Opcodes.IALOAD)
			log("IALOAD");
		else if (ins.opcode === Opcodes.LALOAD)
			log("LALOAD");
		else if (ins.opcode === Opcodes.FALOAD)
			log("FALOAD");
		else if (ins.opcode === Opcodes.DALOAD)
			log("DALOAD");
		else if (ins.opcode === Opcodes.AALOAD)
			log("AALOAD");
		else if (ins.opcode === Opcodes.BALOAD)
			log("BALOAD");
		else if (ins.opcode === Opcodes.CALOAD)
			log("CALOAD");
		else if (ins.opcode === Opcodes.SALOAD)
			log("SALOAD");
		else if (ins.opcode === Opcodes.ISTORE)
			log("ISTORE" + ins.var);
		else if (ins.opcode === Opcodes.LSTORE)
			log("LSTORE" + ins.var);
		else if (ins.opcode === Opcodes.FSTORE)
			log("FSTORE" + ins.var);
		else if (ins.opcode === Opcodes.DSTORE)
			log("DSTORE" + ins.var);
		else if (ins.opcode === Opcodes.ASTORE)
			log("ASTORE" + ins.var);
		else if (ins.opcode === Opcodes.IASTORE)
			log("IASTORE");
		else if (ins.opcode === Opcodes.LASTORE)
			log("LASTORE");
		else if (ins.opcode === Opcodes.FASTORE)
			log("FASTORE");
		else if (ins.opcode === Opcodes.DASTORE)
			log("DASTORE");
		else if (ins.opcode === Opcodes.AASTORE)
			log("AASTORE");
		else if (ins.opcode === Opcodes.BASTORE)
			log("BASTORE");
		else if (ins.opcode === Opcodes.CASTORE)
			log("CASTORE");
		else if (ins.opcode === Opcodes.SASTORE)
			log("SASTORE");
		else if (ins.opcode === Opcodes.POP)
			log("POP");
		else if (ins.opcode === Opcodes.POP2)
			log("POP2");
		else if (ins.opcode === Opcodes.DUP)
			log("DUP");
		else if (ins.opcode === Opcodes.DUP_X1)
			log("DUP_X1");
		else if (ins.opcode === Opcodes.DUP_X2)
			log("DUP_X2");
		else if (ins.opcode === Opcodes.DUP2)
			log("DUP2");
		else if (ins.opcode === Opcodes.DUP2_X1)
			log("DUP2_X1");
		else if (ins.opcode === Opcodes.DUP2_X2)
			log("DUP2_X2");
		else if (ins.opcode === Opcodes.SWAP)
			log("SWAP");
		else if (ins.opcode === Opcodes.IADD)
			log("IADD");
		else if (ins.opcode === Opcodes.LADD)
			log("LADD");
		else if (ins.opcode === Opcodes.FADD)
			log("FADD");
		else if (ins.opcode === Opcodes.DADD)
			log("DADD");
		else if (ins.opcode === Opcodes.ISUB)
			log("ISUB");
		else if (ins.opcode === Opcodes.LSUB)
			log("LSUB");
		else if (ins.opcode === Opcodes.FSUB)
			log("FSUB");
		else if (ins.opcode === Opcodes.DSUB)
			log("DSUB");
		else if (ins.opcode === Opcodes.IMUL)
			log("IMUL");
		else if (ins.opcode === Opcodes.LMUL)
			log("LMUL");
		else if (ins.opcode === Opcodes.FMUL)
			log("FMUL");
		else if (ins.opcode === Opcodes.DMUL)
			log("DMUL");
		else if (ins.opcode === Opcodes.IDIV)
			log("IDIV");
		else if (ins.opcode === Opcodes.LDIV)
			log("LDIV");
		else if (ins.opcode === Opcodes.FDIV)
			log("FDIV");
		else if (ins.opcode === Opcodes.DDIV)
			log("DDIV");
		else if (ins.opcode === Opcodes.IREM)
			log("IREM");
		else if (ins.opcode === Opcodes.LREM)
			log("LREM");
		else if (ins.opcode === Opcodes.FREM)
			log("FREM");
		else if (ins.opcode === Opcodes.DREM)
			log("DREM");
		else if (ins.opcode === Opcodes.INEG)
			log("INEG");
		else if (ins.opcode === Opcodes.LNEG)
			log("LNEG");
		else if (ins.opcode === Opcodes.FNEG)
			log("FNEG");
		else if (ins.opcode === Opcodes.DNEG)
			log("DNEG");
		else if (ins.opcode === Opcodes.ISHL)
			log("ISHL");
		else if (ins.opcode === Opcodes.LSHL)
			log("LSHL");
		else if (ins.opcode === Opcodes.ISHR)
			log("ISHR");
		else if (ins.opcode === Opcodes.LSHR)
			log("LSHR");
		else if (ins.opcode === Opcodes.IUSHR)
			log("IUSHR");
		else if (ins.opcode === Opcodes.LUSHR)
			log("LUSHR");
		else if (ins.opcode === Opcodes.IAND)
			log("IAND");
		else if (ins.opcode === Opcodes.LAND)
			log("LAND");
		else if (ins.opcode === Opcodes.IOR)
			log("IOR");
		else if (ins.opcode === Opcodes.LOR)
			log("LOR");
		else if (ins.opcode === Opcodes.IXOR)
			log("IXOR");
		else if (ins.opcode === Opcodes.LXOR)
			log("LXOR");
		else if (ins.opcode === Opcodes.IINC)
			log("IINC");
		else if (ins.opcode === Opcodes.I2L)
			log("I2L");
		else if (ins.opcode === Opcodes.I2F)
			log("I2F");
		else if (ins.opcode === Opcodes.I2D)
			log("I2D");
		else if (ins.opcode === Opcodes.L2I)
			log("L2I");
		else if (ins.opcode === Opcodes.L2F)
			log("L2F");
		else if (ins.opcode === Opcodes.L2D)
			log("L2D");
		else if (ins.opcode === Opcodes.F2I)
			log("F2I");
		else if (ins.opcode === Opcodes.F2L)
			log("F2L");
		else if (ins.opcode === Opcodes.F2D)
			log("F2D");
		else if (ins.opcode === Opcodes.D2I)
			log("D2I");
		else if (ins.opcode === Opcodes.D2L)
			log("D2L");
		else if (ins.opcode === Opcodes.D2F)
			log("D2F");
		else if (ins.opcode === Opcodes.I2B)
			log("I2B");
		else if (ins.opcode === Opcodes.I2C)
			log("I2C");
		else if (ins.opcode === Opcodes.I2S)
			log("I2S");
		else if (ins.opcode === Opcodes.LCMP)
			log("LCMP");
		else if (ins.opcode === Opcodes.FCMPL)
			log("FCMPL");
		else if (ins.opcode === Opcodes.FCMPG)
			log("FCMPG");
		else if (ins.opcode === Opcodes.DCMPL)
			log("DCMPL");
		else if (ins.opcode === Opcodes.DCMPG)
			log("DCMPG");
		else if (ins.opcode === Opcodes.IFEQ)
			log("IFEQ");
		else if (ins.opcode === Opcodes.IFNE)
			log("IFNE");
		else if (ins.opcode === Opcodes.IFLT)
			log("IFLT");
		else if (ins.opcode === Opcodes.IFGE)
			log("IFGE");
		else if (ins.opcode === Opcodes.IFGT)
			log("IFGT");
		else if (ins.opcode === Opcodes.IFLE)
			log("IFLE");
		else if (ins.opcode === Opcodes.IF_ICMPEQ)
			log("IF_ICMPEQ");
		else if (ins.opcode === Opcodes.IF_ICMPNE)
			log("IF_ICMPNE");
		else if (ins.opcode === Opcodes.IF_ICMPLT)
			log("IF_ICMPLT");
		else if (ins.opcode === Opcodes.IF_ICMPGE)
			log("IF_ICMPGE");
		else if (ins.opcode === Opcodes.IF_ICMPGT)
			log("IF_ICMPGT");
		else if (ins.opcode === Opcodes.IF_ICMPLE)
			log("IF_ICMPLE");
		else if (ins.opcode === Opcodes.IF_ACMPEQ)
			log("IF_ACMPEQ");
		else if (ins.opcode === Opcodes.IF_ACMPNE)
			log("IF_ACMPNE");
		else if (ins.opcode === Opcodes.GOTO)
			log("GOTO");
		else if (ins.opcode === Opcodes.JSR)
			log("JSR");
		else if (ins.opcode === Opcodes.RET)
			log("RET" + ins.var);
		else if (ins.opcode === Opcodes.TABLESWITCH)
			log("TABLESWITCH");
		else if (ins.opcode === Opcodes.LOOKUPSWITCH)
			log("LOOKUPSWITCH");
		else if (ins.opcode === Opcodes.IRETURN)
			log("IRETURN");
		else if (ins.opcode === Opcodes.LRETURN)
			log("LRETURN");
		else if (ins.opcode === Opcodes.FRETURN)
			log("FRETURN");
		else if (ins.opcode === Opcodes.DRETURN)
			log("DRETURN");
		else if (ins.opcode === Opcodes.ARETURN)
			log("ARETURN");
		else if (ins.opcode === Opcodes.RETURN)
			log("RETURN");
		else if (ins.opcode === Opcodes.GETSTATIC)
			log("GETSTATIC");
		else if (ins.opcode === Opcodes.PUTSTATIC)
			log("PUTSTATIC");
		else if (ins.opcode === Opcodes.GETFIELD)
			log("GETFIELD");
		else if (ins.opcode === Opcodes.PUTFIELD)
			log("PUTFIELD");
		else if (ins.opcode === Opcodes.INVOKEVIRTUAL)
			log("INVOKEVIRTUAL");
		else if (ins.opcode === Opcodes.INVOKESPECIAL)
			log("INVOKESPECIAL");
		else if (ins.opcode === Opcodes.INVOKESTATIC)
			log("INVOKESTATIC");
		else if (ins.opcode === Opcodes.INVOKEINTERFACE)
			log("INVOKEINTERFACE");
		else if (ins.opcode === Opcodes.INVOKEDYNAMIC)
			log("INVOKEDYNAMIC");
		else if (ins.opcode === Opcodes.NEW)
			log("NEW");
		else if (ins.opcode === Opcodes.NEWARRAY)
			log("NEWARRAY");
		else if (ins.opcode === Opcodes.ANEWARRAY)
			log("ANEWARRAY");
		else if (ins.opcode === Opcodes.ARRAYLENGTH)
			log("ARRAYLENGTH");
		else if (ins.opcode === Opcodes.ATHROW)
			log("ATHROW");
		else if (ins.opcode === Opcodes.CHECKCAST)
			log("CHECKCAST");
		else if (ins.opcode === Opcodes.INSTANCEOF)
			log("INSTANCEOF");
		else if (ins.opcode === Opcodes.MONITORENTER)
			log("MONITORENTER");
		else if (ins.opcode === Opcodes.MONITOREXIT)
			log("MONITOREXIT");
		else if (ins.opcode === Opcodes.MULTIANEWARRAY)
			log("MULTIANEWARRAY");
		else if (ins.opcode === Opcodes.IFNULL)
			log("IFNULL");
		else if (ins.opcode === Opcodes.IFNONNULL)
			log("IFNONNULL");
		else
			log(ins.opcode);
	}
}
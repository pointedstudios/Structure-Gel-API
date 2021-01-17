package com.legacy.structure_gel.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.legacy.structure_gel.SGCrashHandler;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.Util;

@Mixin(CrashReport.class)
public class CrashReportMixin
{
	@ModifyVariable(at = @At(value = "LOAD", ordinal = 10), method = "getCompleteReport()Ljava/lang/String;", index = 1)
	private StringBuilder modify$stringbuilder(StringBuilder stringbuilder)
	{
//		dummyCrashReport(new ArrayList<>());
		if (SGCrashHandler.shouldModifyCrashReport())
		{
			stringbuilder.append("\n\n");
			stringbuilder.append(String.format("%s:\n", getWittySGComment()));
			stringbuilder.append(SGCrashHandler.getAdditionalInfo());
		}
		return stringbuilder;
	}

	private static String getWittySGComment()
	{
		String[] wittyComment = new String[]
				{
						"More insight on this crash from Structure Gel API",
						"And now, a quick word from Structure Gel API",
						"We interrupt this program for a message from Structure Gel API",
						"Structure Gel API here to reveal the man behind the slaughter",
						"Minecraft has crashed. Maybe Structure Gel API knows why",
						"Darn it, not again! Time for Structure Gel API to give more insight",
						"Reporter Structure Gel API from Modding Legacy here to give you this brief message"
				};

		try
		{
			return wittyComment[(int) (Util.nanoTime() % (long) wittyComment.length)];
		}
		catch (Throwable throwable)
		{
			return wittyComment[0];
		}
	}

//	private static void dummyCrashReport(ArrayList<String> nullItems)
//	{
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append("Huh. Well this is awkward. Looks like you crashed while using custom server software. Some of the stuff that we need doesn't exist because they changed stuff in the code. If you could report it to them, that would be nice. Send them to our repo so they can look at this class and see where we get the values from.");
//		stringBuilder.append("\n");
//		if (nullItems.size() > 0)
//		{
//			stringBuilder.append("Reason: The server's ");
//			for (int i = 0; i < nullItems.size(); i++)
//			{
//				if (i == nullItems.size() - 1)
//					stringBuilder.append(String.format("%s%s", nullItems.size() == 1 ? "" : "and ", nullItems.get(i)));
//				else
//					stringBuilder.append(String.format("%s%s ", nullItems.get(i), nullItems.size() == 2 ? "" : ","));
//			}
//			stringBuilder.append(String.format(" %s null.\n", nullItems.size() > 1 ? "were" : "was"));
//		}
//		else
//			stringBuilder.append("Well, we're not really sure why this crash happened, but make sure nothing in your custom server software is null when it shouldn't be. " +
//					"If all else fails, you can talk to us on the Modding Legacy Discord server. This crash might be an interesting rabbit hole to traverse.\n");
//		stringBuilder.append("Anyway, here's the rest of the crash report. Have a good day :)");
//		SGCrashHandler.prepareAdditionalCrashInfo(stringBuilder.toString());
//	}
}

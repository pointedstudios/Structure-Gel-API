package com.legacy.structure_gel;

public class SGCrashHandler
{
	private static String ADDITIONAL_INFORMATION;

	public static void prepareAdditionalCrashInfo(String additionalInfo)
	{
		ADDITIONAL_INFORMATION = additionalInfo;
	}

	public static boolean shouldModifyCrashReport()
	{
		return ADDITIONAL_INFORMATION != null && !ADDITIONAL_INFORMATION.isEmpty();
	}

	public static String getAdditionalInfo()
	{
		return ADDITIONAL_INFORMATION;
	}
}

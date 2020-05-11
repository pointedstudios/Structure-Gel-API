package com.legacy.structure_gel.util;

import java.util.HashMap;
import java.util.Map;

public class Collectors
{
	public static <T, V> Map<T, V> mapOf(Class<T> t, Class<V> v, Object... objects)
	{
		Map<T, V> map = new HashMap<>();
		for (int i = 0; i < objects.length; i += 2)
		{
			try
			{
				map.put(t.cast(objects[i]), v.cast(objects[i + 1]));
			}
			catch (ClassCastException | ArrayIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
		}

		return map;
	}
}

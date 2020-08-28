package com.legacy.structure_gel.util;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

public class GelCollectors
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
	
	public static <T> List<T> addToList(List<T> list, T obj)
	{
		return Streams.concat(list.stream(), Arrays.asList(obj).stream()).collect(Collectors.toList());
	}
	
	public static <T> List<T> addToList(List<T> list, List<T> objs)
	{
		return Streams.concat(list.stream(), objs.stream()).collect(Collectors.toList());
	}
	
	public static <T> List<T> makeListMutable(List<T> list)
	{
		return list.stream().collect(Collectors.toList());
	}
	
	public static <K, V> Map<K, V> addToMap(Map<K, V> map, K key, V value, Function<Entry<K, V>, K> keyFunction, Function<Entry<K, V>, V> valueFunction)
	{
		return Streams.concat(map.entrySet().stream(), Arrays.asList(new AbstractMap.SimpleEntry<K, V>(key, value)).stream()).collect(Collectors.toMap(keyFunction, valueFunction));
	}
	
	public static <K, V> Map<K, V> addToMap(Map<K, V> map, K key, V value)
	{
		return addToMap(map, key, value, Entry::getKey, Entry::getValue);
	}
	
	public static <K, V> Map<K, V> makeMapMutable(Map<K, V> map, Function<Entry<K, V>, K> keyFunction, Function<Entry<K, V>, V> valueFunction)
	{
		return map.entrySet().stream().collect(Collectors.toMap(keyFunction, valueFunction));
	}
	
	public static <K, V> Map<K, V> makeMapMutable(Map<K, V> map)
	{
		return makeMapMutable(map, Entry::getKey, Entry::getValue);
	}
}

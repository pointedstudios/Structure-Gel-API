package com.legacy.structure_gel.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;

/**
 * Contains helper methods for various types of {@link Collection}, including an
 * alternative to creating a {@link Map} and various ways to make immutable
 * collections mutable.
 * 
 * @author David
 *
 */
public class GelCollectors
{
	/**
	 * A method to create a map with any amount of arguments. Note that this does
	 * have issues with generic types.
	 * 
	 * @param keyClass
	 * @param valueClass
	 * @param objects
	 * @return Map
	 * @throws ClassCastException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public static <T, V> Map<T, V> mapOf(Class<T> keyClass, Class<V> valueClass, Object... objects) throws ClassCastException, ArrayIndexOutOfBoundsException
	{
		Map<T, V> map = new HashMap<>();
		for (int i = 0; i < objects.length; i += 2)
			map.put(keyClass.cast(objects[i]), valueClass.cast(objects[i + 1]));

		return map;
	}

	/**
	 * Adds the object passed to the list and returns the result.
	 * 
	 * @param list
	 * @param obj
	 * @return List
	 */
	public static <T> List<T> addToList(List<T> list, T obj)
	{
		return Streams.concat(list.stream(), ImmutableList.of(obj).stream()).collect(Collectors.toList());
	}

	/**
	 * Merges the entries from the two lists and returns the result.
	 * 
	 * @param list
	 * @param list2
	 * @return List
	 */
	public static <T> List<T> addToList(List<T> list, List<T> list2)
	{
		return Streams.concat(list.stream(), list2.stream()).collect(Collectors.toList());
	}

	/**
	 * Creates a mutable version of the list passed in and returns it.
	 * 
	 * @param list
	 * @return List
	 */
	public static <T> List<T> makeListMutable(List<T> list)
	{
		return list.stream().collect(Collectors.toList());
	}

	/**
	 * Creates a mutable version of the list passed in, applies the passed function
	 * to all entries, and returns it.
	 * 
	 * @param list
	 * @param listFunction
	 * @return List
	 */
	public static <T> List<T> makeListMutable(List<T> list, Function<T, T> listFunction)
	{
		return list.stream().map(listFunction).collect(Collectors.toList());
	}

	/**
	 * Adds the object passed to the set and returns the result.
	 * 
	 * @param set
	 * @param obj
	 * @return Set
	 */
	public static <T> Set<T> addToSet(Set<T> set, T obj)
	{
		return addToSet(set, ImmutableSet.of(obj));
	}

	/**
	 * Merges the entries from the two sets and returns the result.
	 * 
	 * @param set
	 * @param set2
	 * @return Set
	 */
	public static <T> Set<T> addToSet(Set<T> set, Set<T> set2)
	{
		return Streams.concat(set.stream(), set2.stream()).collect(Collectors.toSet());
	}

	/**
	 * Creates a mutable version of the set passed in and returns it.
	 * 
	 * @param set
	 * @return Set
	 */
	public static <T> Set<T> makeSetMutable(Set<T> set)
	{
		return set.stream().collect(Collectors.toSet());
	}

	/**
	 * Creates a mutable version of the set passed in, applies the passed function
	 * to all entries, and returns it.
	 * 
	 * @param set
	 * @param setFunction
	 * @return Set
	 */
	public static <T> Set<T> makeSetMutable(Set<T> set, Function<T, T> setFunction)
	{
		return set.stream().map(setFunction).collect(Collectors.toSet());
	}

	/**
	 * Merges the entries from the two maps, applies the passed functions to all
	 * entries, and returns the result.
	 * 
	 * @param map
	 * @param map2
	 * @param keyFunction
	 * @param valueFunction
	 * @return Map
	 */
	public static <K, V> Map<K, V> addToMap(Map<K, V> map, Map<K, V> map2, Function<Entry<K, V>, K> keyFunction, Function<Entry<K, V>, V> valueFunction)
	{
		return Streams.concat(map.entrySet().stream(), map2.entrySet().stream()).collect(Collectors.toMap(keyFunction, valueFunction));
	}

	/**
	 * Merges the entries from the two maps and returns the result.
	 * 
	 * @param map
	 * @param map2
	 * @return Map
	 */
	public static <K, V> Map<K, V> addToMap(Map<K, V> map, Map<K, V> map2)
	{
		return addToMap(map, map2, Entry::getKey, Entry::getValue);
	}

	/**
	 * Adds the key and value passed to the map, applies the passed functions to all
	 * entries, and returns the result.
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @param keyFunction
	 * @param valueFunction
	 * @return Map
	 */
	public static <K, V> Map<K, V> addToMap(Map<K, V> map, K key, V value, Function<Entry<K, V>, K> keyFunction, Function<Entry<K, V>, V> valueFunction)
	{
		return Streams.concat(map.entrySet().stream(), ImmutableList.of(new AbstractMap.SimpleEntry<K, V>(key, value)).stream()).collect(Collectors.toMap(keyFunction, valueFunction));
	}

	/**
	 * Adds the key and value passed to the map and returns the result.
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @return Map
	 */
	public static <K, V> Map<K, V> addToMap(Map<K, V> map, K key, V value)
	{
		return addToMap(map, key, value, Entry::getKey, Entry::getValue);
	}

	/**
	 * Creates a mutable version of the map passed in, applies the passed functions
	 * to all entries, and returns it.
	 * 
	 * @param map
	 * @param keyFunction
	 * @param valueFunction
	 * @return Map
	 */
	public static <K, V> Map<K, V> makeMapMutable(Map<K, V> map, Function<Entry<K, V>, K> keyFunction, Function<Entry<K, V>, V> valueFunction)
	{
		return map.entrySet().stream().collect(Collectors.toMap(keyFunction, valueFunction));
	}

	/**
	 * Creates a mutable version of the map passed in and returns it.
	 * 
	 * @param map
	 * @return Map
	 */
	public static <K, V> Map<K, V> makeMapMutable(Map<K, V> map)
	{
		return makeMapMutable(map, Entry::getKey, Entry::getValue);
	}
}

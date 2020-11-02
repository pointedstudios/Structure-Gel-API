package com.legacy.structure_gel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.legacy.structure_gel.biome_dictionary.BiomeDictionary;
import com.legacy.structure_gel.biome_dictionary.BiomeType;
import com.legacy.structure_gel.util.Internal;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

/**
 * Introuces compatibility with other mods without them needing to use the API
 * as a dependency.<br>
 * <br>
 * To add compatibility to your mod, check the
 * com.legacy.structure_gel.structure_gel_compat package. In your own mod,
 * create a package named "structure_gel_compat" in the same package as your
 * main @Mod class. Within that package, you need to make classes that mimic the
 * example classes found in com.legacy.structure_gel.structure_gel_compat that
 * apply to your needs.<br>
 * <br>
 * If you encounter issues or find any bugs, please report them to the issue
 * tracker. https://gitlab.com/modding-legacy/structure-gel-api/-/issues
 * 
 * @author David
 *
 */
@Internal
public class StructureGelCompat
{
	private static final Logger LOGGER = LogManager.getLogger();

	protected static void init(IEventBus modBus)
	{
		modBus.addGenericListener(BiomeType.class, StructureGelCompat::registerBiomeDictionary);
	}

	protected static void registerBiomeDictionary(final RegistryEvent.Register<BiomeType> event)
	{
		LOGGER.info("Checking for biome dictionary registry methods in other mods.");

		// Initializing the biome dictionary here just in case this runs first
		BiomeDictionary.init();
		// TODO remove this in 1.17
		registerBiomeDictionaryOld(event);

		compatForEachMod("BiomeDictionaryCompat", (compatClass, modID, mod) ->
		{
			getMethod(compatClass, "register", String.class).ifPresent(method ->
			{
				List<Triple<ResourceLocation, Set<ResourceLocation>, Set<ResourceLocation>>> returnType = null;
				// TODO use BiomeType constructor in 1.17
				invokeMethod(method, returnType, modID).ifPresent(result -> result.forEach(t ->
				{
					BiomeType type = BiomeType.create(t.getLeft()).setParents(t.getMiddle()).setBiomesSafe(t.getRight());
					LOGGER.info(modID + " registered a biome type: " + type.toString());
					BiomeDictionary.register(type);
				}));
			});
		});
	}

	private static void compatForEachMod(String compatClassName, TriConsumer<Class<?>, String, FMLModContainer> consumer)
	{
		Set<String> skippedMods = Sets.newHashSet("minecraft", "forge");
		ModList.get().forEachModContainer((modID, modContainer) ->
		{
			if (modContainer instanceof FMLModContainer && !skippedMods.contains(modID))
			{
				FMLModContainer fmlContainer = (FMLModContainer) modContainer;
				try
				{
					String compatClassFullName = modContainer.getMod().getClass().getPackage().getName() + ".structure_gel_compat." + compatClassName;
					try
					{
						Class<?> compatClass = Class.forName(compatClassFullName);
						consumer.accept(compatClass, modID, fmlContainer);
					}
					catch (ClassNotFoundException e)
					{
						// Class wasn't found. This is expected for mods that don't add support.
					}
					catch (ExceptionInInitializerError e)
					{
						LOGGER.error(String.format("Found a compatible class %s in the mod %s but it could not be initialized.", compatClassFullName, modID));
						e.printStackTrace();
					}
					catch (LinkageError e)
					{
						LOGGER.error(String.format("Something went horribly wrong when trying to load %s from %s.", compatClassFullName, modID));
						e.printStackTrace();
					}
				}
				catch (Throwable throwable)
				{
					LOGGER.warn(String.format("Encountered an unhandled exception while trying to handle %s in %s.", compatClassName, modID));
					throwable.printStackTrace();
				}
			}
		});
	}

	private static Optional<Method> getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
	{
		try
		{
			Method method = clazz.getMethod(methodName, String.class);
			if (Modifier.isStatic(method.getModifiers()))
			{
				return Optional.of(clazz.getMethod(methodName, String.class));
			}
			else
			{
				LOGGER.error(String.format("The %s method in %s is not static and it should be.", methodName, clazz.getName()));
				return Optional.empty();
			}
		}
		catch (NoSuchMethodException e)
		{
			LOGGER.error(String.format("Could not find the %s method in %s.", methodName, clazz.getName()));
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			LOGGER.error(String.format("The %s method in %s can not be accessed. It may be private.", methodName, clazz.getName()));
			e.printStackTrace();
		}
		return Optional.empty();
	}

	/**
	 * Invokes the provided method and returns an {@link Optional} storing the
	 * result.
	 * 
	 * @param method The method to invoke.
	 * @param returnTypeInstance This doesn't need to be initialized since it exists
	 *            for casting types.
	 * @param args The arguments for the invoked method.
	 * @return An {@link Optional} containing the object returned by invoking the
	 *         method passed. May be empty if nothing was returned.
	 */
	@SuppressWarnings("unchecked")
	private static <T> Optional<T> invokeMethod(Method method, T returnTypeInstance, Object... args)
	{
		try
		{
			return Optional.ofNullable((T) method.invoke(null, args));
		}
		catch (IllegalAccessException e)
		{
			LOGGER.error(String.format("The method %s can not be accessed. It or the class it's in may be private.", method.getName()));
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.error(String.format("The arguments passed do not match the arguments required for %s.", method.getName()));
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			LOGGER.error(String.format("The method %s threw an exception.", method.getName()));
			e.printStackTrace();
		}
		return Optional.empty();
	}

	// TODO remove in 1.17 favor of the new system
	@Deprecated
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void registerBiomeDictionaryOld(final RegistryEvent.Register<BiomeType> event)
	{
		try
		{
			ModList.get().forEachModContainer((s, mc) ->
			{
				if (mc instanceof FMLModContainer)
				{
					FMLModContainer fmlContainer = (FMLModContainer) mc;
					Method[] methods = new Method[0];
					try
					{
						methods = fmlContainer.getMod().getClass().getMethods();
					}
					catch (Throwable e)
					{
						// If it fails, just silently move on without it. Likely caused by trying to
						// access client classes on server. This feature is deprecated anyway and
						// there's a better way to do it.
					}

					for (Method method : methods)
					{
						List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>> result = Lists.newArrayList();

						// As of 1.16.2-v1.3.0
						if (method.getName().equals("getBiomesSG"))
						{
							try
							{
								result = (List<Triple<ResourceLocation, Set<ResourceLocation>, Set<RegistryKey<Biome>>>>) method.invoke(fmlContainer.getMod());
							}
							catch (Throwable e)
							{
								LOGGER.info(String.format("Failed to invoke getBiomesSG from %s. Proceeding to the next mod.", s));
								e.printStackTrace();
							}
						}

						// Register
						if (result != null && !result.isEmpty())
						{
							result.forEach(t ->
							{
								BiomeType type = BiomeType.create(t.getLeft()).setParents(t.getMiddle()).setBiomes(t.getRight());
								LOGGER.info(s + " registered a biome type: " + type.toString());
								BiomeDictionary.register(type);
							});
						}
					}
				}
			});
		}
		catch (Throwable e)
		{
			LOGGER.info("Encountered a critical error while trying to load other mods' biome dictionary methods. Skipping this step.");
			e.printStackTrace();
		}
	}
}
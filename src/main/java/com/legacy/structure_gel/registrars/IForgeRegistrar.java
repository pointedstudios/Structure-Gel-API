package com.legacy.structure_gel.registrars;

import com.legacy.structure_gel.util.RegistryHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * An interface that registrars using forge registries use.
 *
 * @param <T>
 * @author David
 * @see RegistryHelper#handleRegistrar(IForgeRegistrar, IForgeRegistry)
 */
public interface IForgeRegistrar<T extends IForgeRegistrar<T, R>, R extends IForgeRegistryEntry<R>> extends IRegistrar<T>
{
	/**
	 * Registers the data inside and returns the registrar.
	 *
	 * @return {@link IForgeRegistrar}
	 */
	T handleForge(IForgeRegistry<R> registry);

	/**
	 * Runs {@link #handle()} and {@link #handleForge(IForgeRegistry)} at the same
	 * time
	 *
	 * @param registry
	 * @return {@link IForgeRegistrar}
	 */
	default T handleAll(IForgeRegistry<R> registry)
	{
		return handle().handleForge(registry);
	}
}

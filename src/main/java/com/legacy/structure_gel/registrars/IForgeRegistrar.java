package com.legacy.structure_gel.registrars;

import com.legacy.structure_gel.util.RegistryHelper;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * An interface that registrars using forge registries use.
 * 
 * @see RegistryHelper#handleRegistrar(IForgeRegistrar, IForgeRegistry)
 * @author David
 *
 * @param <T>
 */
public interface IForgeRegistrar<T extends IForgeRegistrar<T, R>, R extends IForgeRegistryEntry<R>> extends IRegistrar<T>
{
	/**
	 * Registers the data inside and returns the registrar.
	 * 
	 * @return {@link IForgeRegistrar}
	 */
	public T handleForge(IForgeRegistry<R> registry);
}

package com.legacy.structure_gel.registrars;

import com.legacy.structure_gel.util.RegistryHelper;

/**
 * An interface that all registrars use.
 *
 * @param <T>
 * @author David
 * @see RegistryHelper#handleRegistrar(IRegistrar)
 */
public interface IRegistrar<T extends IRegistrar<T>>
{
	/**
	 * Registers the data inside and returns the registrar.
	 *
	 * @return {@link IRegistrar}
	 */
	T handle();
}

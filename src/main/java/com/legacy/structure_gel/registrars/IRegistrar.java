package com.legacy.structure_gel.registrars;

import com.legacy.structure_gel.util.RegistryHelper;

/**
 * An interface that all registrars use.
 * 
 * @see RegistryHelper#handleRegistrar(IRegistrar)
 * @author David
 *
 * @param <T>
 */
public interface IRegistrar<T extends IRegistrar<T>>
{
	/**
	 * Registers the data inside and returns the registrar.
	 * 
	 * @return {@link IRegistrar}
	 */
	public T handle();
}

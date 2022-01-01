/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Rethrow;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * {@code Supplier} that can throw checked exceptions.
 *
 * @param <T>   return type
 * @param <E>   exception type
 *
 * @see Supplier
 */
@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable>
{
	/**
	 * Converts a {@code CheckedSupplier} into a regular {@code Supplier}.
	 * Any caught exceptions will be rethrown with:
	 * {@link Rethrow#caught(Throwable)}
	 *
	 * @param supplier  the checked supplier
	 * @param <T>       return type
	 *
	 * @return  the CheckedSupplier wrapped by an unchecked Supplier
	 */
	static <T> Supplier<T> unchecked(CheckedSupplier<T, ? extends Exception> supplier)
	{
		Objects.requireNonNull(supplier, "supplier");
		
		return () ->
		{
			try { return supplier.get(); }
			catch (Exception e) { throw Rethrow.caught(e); }
		};
	}
	
	/**
	 * Gets a result.
	 *
	 * @return      a result
	 * @throws E    a checked exception
	 */
	T get() throws E;
}

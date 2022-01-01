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
import java.util.function.BiConsumer;

/**
 * {@code BiConsumer} that can throw checked exceptions.
 *
 * @param <T>   first argument type
 * @param <U>   second argument type
 * @param <E>   exception type
 *
 * @see BiConsumer
 */
@FunctionalInterface
public interface CheckedBiConsumer<T, U, E extends Throwable>
{
	/**
	 * Converts a {@code CheckedBiConsumer} into a regular {@code BiConsumer}.
	 * Any caught exceptions will be rethrown with:
	 * {@link Rethrow#caught(Throwable)}
	 *
	 * @param consumer  the checked biconsumer
	 * @param <T>       first argument type
	 * @param <U>       second argument type
	 *
	 * @return  the CheckedBiConsumer wrapped by an unchecked BiConsumer
	 */
	static <T, U> BiConsumer<T, U> unchecked(CheckedBiConsumer<T, U, ? extends Exception> consumer)
	{
		Objects.requireNonNull(consumer, "consumer");
		
		return (t, u) ->
		{
			try { consumer.accept(t, u); }
			catch (Exception e) { throw Rethrow.caught(e); }
		};
	}
	
	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t     the first input argument
	 * @param u     the second input argument
	 * @throws E    a checked exception
	 */
	void accept(T t, U u) throws E;
}

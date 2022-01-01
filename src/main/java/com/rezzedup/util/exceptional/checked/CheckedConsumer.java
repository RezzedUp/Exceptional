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
import java.util.function.Consumer;

/**
 * {@code Consumer} that can throw checked exceptions.
 *
 * @param <T>   argument type
 * @param <E>   exception type
 *
 * @see Consumer
 */
@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable>
{
	/**
	 * Converts a {@code CheckedConsumer} into a regular {@code Consumer}.
	 * Any caught exceptions will be rethrown with:
	 * {@link Rethrow#caught(Throwable)}
	 *
	 * @param consumer  the checked consumer
	 * @param <T>       argument type
	 *
	 * @return  the CheckedConsumer wrapped by an unchecked Consumer
	 */
	static <T> Consumer<T> unchecked(CheckedConsumer<T, ? extends Exception> consumer)
	{
		Objects.requireNonNull(consumer, "consumer");
		
		return t ->
		{
			try { consumer.accept(t); }
			catch (Exception e) { throw Rethrow.caught(e); }
		};
	}
	
	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t     the argument
	 * @throws E    a checked exception
	 */
	void accept(T t) throws E;
}

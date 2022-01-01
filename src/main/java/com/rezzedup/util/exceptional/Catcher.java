/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import java.util.function.Consumer;

/**
 * Handles exceptions.
 *
 * @param <E>   exception type
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface Catcher<E extends Throwable> extends Consumer<E>
{
	/**
	 * Does nothing, thus ignoring any consumed exception.
	 *
	 * @param exception     the exception
	 * @param <E>           exception type
	 */
	static <E extends Throwable> void ignore(E exception) {}
	
	/**
	 * Prints exceptions with {@link Throwable#printStackTrace()}.
	 *
	 * @param exception     the exception
	 * @param <E>           exception type
	 */
	static <E extends Throwable> void print(E exception)
	{
		exception.printStackTrace();
	}
	
	/**
	 * Rethrows exceptions with {@link Rethrow}
	 * (a runtime exception).
	 *
	 * @param exception     the exception
	 * @param <E>           exception type
	 */
	static <E extends Throwable> void rethrow(E exception)
	{
		throw Rethrow.caught(exception);
	}
}

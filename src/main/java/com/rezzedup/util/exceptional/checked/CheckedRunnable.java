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

/**
 * {@code Runnable} that can throw checked exceptions.
 *
 * @param <E>   exception type
 *
 * @see Runnable
 */
@FunctionalInterface
public interface CheckedRunnable<E extends Throwable>
{
	/**
	 * Converts a {@code CheckedRunnable} into a regular {@code Runnable}.
	 * Any caught exceptions will be rethrown with:
	 * {@link Rethrow#caught(Throwable)}
	 *
	 * @param runnable  the checked runnable
	 *
	 * @return  the CheckedRunnable wrapped by an unchecked Runnable
	 */
	static Runnable unchecked(CheckedRunnable<? extends Exception> runnable)
	{
		Objects.requireNonNull(runnable, "runnable");
		
		return () ->
		{
			try { runnable.run(); }
			catch (Exception e) { throw Rethrow.caught(e); }
		};
	}
	
	/**
	 * Runs.
	 *
	 * @throws E    a checked exception
	 */
	void run() throws E;
}

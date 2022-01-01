/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import java.util.Objects;

/**
 * Represents an existing exception thrown again.
 * It also guarantees that the causal exception
 * cannot be {@code null}.
 */
public class Rethrow extends RuntimeException
{
	private static final Sneaky<?> SNEAKY = new Sneaky<>();
	
	/**
	 * Wraps the provided throwable then rethrows it.
	 * If the {@code cause} is itself an instance of
	 * {@code Rethrow}, it will simply be rethrown again.
	 *
	 * @param cause     the caught exception to rethrow
	 *
	 * @return  nothing, this method always throws
	 * @throws Rethrow  the rethrown exception
	 */
	public static Rethrow caught(Throwable cause)
	{
		throw (cause instanceof Rethrow) ? (Rethrow) cause : new Rethrow(cause);
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends Throwable> Sneaky<E> sneaky()
	{
		return (Sneaky<E>) SNEAKY;
	}
	
	/**
	 * Constructs.
	 *
	 * @param cause     an existing exception
	 *
	 * @throws NullPointerException if cause is {@code null}
	 */
	public Rethrow(Throwable cause)
	{
		super(Objects.requireNonNull(cause, "cause"));
	}
	
	/**
	 * Gets the non-{@code null} exception represented
	 * by this rethrow.
	 *
	 * @return  the existing exception that
	 *          this rethrow originates from
	 */
	@Override // overridden to assert non-null return value
	public synchronized Throwable getCause()
	{
		return super.getCause();
	}
	
	public static class Sneaky<E extends Throwable> implements Catcher<E>
	{
		private Sneaky() {}
		
		@SuppressWarnings("unchecked")
		private static <E extends Throwable> void rethrow(Throwable exception) throws E { throw (E) exception; }
		
		public RuntimeException smuggle(Throwable throwable)
		{
			rethrow(throwable);
			throw new AssertionError();
		}
		
		@Override
		public void accept(E exception) { throw smuggle(exception); }
	}
}

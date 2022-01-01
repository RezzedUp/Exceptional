/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import com.rezzedup.util.exceptional.checked.CheckedSupplier;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Optional-like immutable wrapper for a possible value
 * or an exception. Unlike Optional, however, it is
 * intended to be stored long-term. That way, rather than
 * performing the same potentially exceptional operation
 * multiple times, it can be ran once and the result can
 * be queried as needed.
 *
 * <p>Use this where the result of a particular operation
 * is unlikely to change upon calling it with the same
 * arguments, especially regarding its exceptionality.</p>
 *
 * @param <V>   value type
 */
@SuppressWarnings("unused")
public class ThrowsOr<V>
{
	private static final ThrowsOr<?> EMPTY = new ThrowsOr<>();
	
	/**
	 * Gets an empty {@code ThrowsOr} that contains neither
	 * value nor exception. It is completely empty.
	 *
	 * @param <V>   value type
	 * @return      the empty instance
	 */
	@SuppressWarnings("unchecked")
	public static <V> ThrowsOr<V> empty()
	{
		return (ThrowsOr<V>) EMPTY;
	}
	
	/**
	 * Creates a new {@code ThrowsOr} containing the
	 * provided non-null value.
	 *
	 * @param value     non-null value
	 * @param <V>       value type
	 *
	 * @return  a new instance containing the value
	 *
	 * @throws NullPointerException     if value is {@code null}
	 */
	public static <V> ThrowsOr<V> value(V value)
	{
		return new ThrowsOr<>(value);
	}
	
	/**
	 * Creates a new {@code ThrowsOr} containing the
	 * provided value unless it's {@code null}, in which
	 * case it contains the supplied exception instead.
	 *
	 * @param value                 possible null value
	 * @param exceptionSupplier     exception if value is null
	 * @param <V>                   value type
	 *
	 * @return  a new instance containing the value
	 *          or the supplied exception if it's null
	 */
	public static <V> ThrowsOr<V> value(@NullOr V value, Supplier<Throwable> exceptionSupplier)
	{
		Objects.requireNonNull(exceptionSupplier, "exceptionSupplier");
		return (value != null) ? value(value) : raise(exceptionSupplier);
	}
	
	/**
	 * Creates a new {@code ThrowsOr} containing the
	 * provided value otherwise {@link #empty()} if
	 * it's {@code null}.
	 *
	 * @param value     possible null value
	 * @param <V>       value type
	 *
	 * @return  a new instance containing the value
	 *          or the empty instance if it's null
	 */
	public static <V> ThrowsOr<V> maybe(@NullOr V value)
	{
		return (value == null) ? empty() : value(value);
	}
	
	/**
	 * Creates a new {@code ThrowsOr} containing the
	 * provided non-{@code null} exception.
	 *
	 * @param exception     non-null exception
	 * @param <V>           value type
	 *
	 * @return  a new instance containing the exception
	 *
	 * @throws NullPointerException     if exception is {@code null}
	 */
	public static <V> ThrowsOr<V> raise(Throwable exception)
	{
		return new ThrowsOr<>(exception);
	}
	
	/**
	 * Creates a new {@code ThrowsOr} containing the
	 * supplied exception.
	 *
	 * @param exceptionSupplier     exception supplier
	 * @param <V>                   value type
	 *
	 * @return  a new instance containing the supplied exception
	 *
	 * @throws NullPointerException     if either exception supplier itself or
	 *                                  the supplied exception are {@code null}
	 */
	public static <V> ThrowsOr<V> raise(Supplier<Throwable> exceptionSupplier)
	{
		Objects.requireNonNull(exceptionSupplier, "exceptionSupplier");
		return raise(exceptionSupplier.get());
	}
	
	/**
	 * Creates a new {@code ThrowsOr} containing the
	 * supplied value or the exception thrown
	 * when attempting to get it.
	 *
	 * <p>If the value is successfully supplied without
	 * interference from any exceptions, the result
	 * is created by passing the value to
	 * {@link #maybe(Object)}.</p>
	 *
	 * <p>Likewise, any thrown exceptions are
	 * passed to {@link #raise(Throwable)}.</p>
	 *
	 * @param supplier  possibly exceptional value supplier
	 * @param <V>       value type
	 *
	 * @return  a new instance containing the supplied
	 *          value or an exception
	 *
	 * @throws NullPointerException     if supplier is {@code null}
	 */
	public static <V> ThrowsOr<V> result(CheckedSupplier<@NullOr V, ? extends Exception> supplier)
	{
		Objects.requireNonNull(supplier, "supplier");
		try { return maybe(supplier.get()); }
		catch (Exception e) { return raise(e); }
	}
	
	private final @NullOr V value;
	private final @NullOr Throwable exception;
	
	private ThrowsOr()
	{
		this.value = null;
		this.exception = null;
	}
	
	private ThrowsOr(V value)
	{
		this.value = Objects.requireNonNull(value, "value");
		this.exception = null;
	}
	
	private ThrowsOr(Throwable exception)
	{
		this.value = null;
		this.exception = Objects.requireNonNull(exception, "exception");
	}
	
	/**
	 * Returns {@code true} if and only if <b>both</b>
	 * value and exception are not present, otherwise
	 * {@code false}. Useful for checking for the
	 * empty instance.
	 *
	 * @return  {@code true} if neither value nor
	 *          exception are present
	 *
	 * @see #empty()
	 * @see #isPresent()
	 * @see #isExceptional()
	 */
	public boolean isEmpty() { return value == null && exception == null; }
	
	/**
	 * Returns {@code true} if a value is present,
	 * otherwise {@code false}. <b>Only</b> the value
	 * is relevant to this check.
	 *
	 * @return  {@code true} if value is present
	 */
	public boolean isPresent() { return value != null; }
	
	/**
	 * Returns the value wrapped by an {@code Optional}.
	 *
	 * @return  an optional containing the value or empty
	 */
	public Optional<V> value() { return Optional.ofNullable(value); }
	
	/**
	 * Gets the value or rethrows the exception contained
	 * within this instance. If this is empty, a
	 * {@code NoSuchElementException} is thrown instead.
	 *
	 * @return  the value, if it exists
	 *
	 * @throws Rethrow                  the rethrown exception, if it exists
	 * @throws NoSuchElementException   if no value nor exception exist
	 */
	public V getOrThrow()
	{
		if (exception != null) { throw new Rethrow(exception); }
		if (value != null) { return value; }
		throw new NoSuchElementException();
	}
	
	/**
	 * Returns {@code true} is an exception is present,
	 * otherwise {@code false}. <b>Only</b> the exception
	 * is relevant to this check.
	 *
	 * @return  {@code true} if exception is present
	 */
	public boolean isExceptional() { return exception != null; }
	
	/**
	 * Returns the exception wrapped by an {@code Optional}.
	 *
	 * @return  an optional containing the exception or empty
	 */
	public Optional<Throwable> exception() { return Optional.ofNullable(exception); }
	
	/**
	 * Propagates the exception contained within this
	 * instance by simply adopting the required generic
	 * type parameters. If there is no exception, an
	 * {@code IllegalStateException} is thrown.
	 *
	 * <p>Propagation is possible because all
	 * {@code ThrowsOr} instances are immutable, and if
	 * an instance already contains an exception, the
	 * specific "value" type is irrelevant because it
	 * inherently cannot contain a value.</p>
	 *
	 * @param <T>   value type
	 *
	 * @return  the same instance with new
	 *          generic type parameters
	 *
	 * @throws IllegalStateException    if no exception is present
	 * @see #isExceptional()
	 */
	@SuppressWarnings("unchecked")
	public <T> ThrowsOr<T> propagate()
	{
		if (exception != null) { return (ThrowsOr<T>) this; }
		throw new IllegalStateException("Cannot propagate: not exceptional");
	}
}

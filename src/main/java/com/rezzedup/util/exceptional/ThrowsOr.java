/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ThrowsOr<V>
{
    private static final ThrowsOr<?> EMPTY = new ThrowsOr<>();
    
    @SuppressWarnings("unchecked")
    public static <V> ThrowsOr<V> empty()
    {
        return (ThrowsOr<V>) EMPTY;
    }
    
    public static <V> ThrowsOr<V> value(V value)
    {
        return new ThrowsOr<>(value);
    }
    
    public static <V> ThrowsOr<V> maybe(@NullOr V value)
    {
        return (value == null) ? empty() : value(value);
    }
    
    public static <V> ThrowsOr<V> raise(Throwable exception)
    {
        return new ThrowsOr<>(exception);
    }
    
    public static <V> ThrowsOr<V> result(Supplier<V> supplier)
    {
        Objects.requireNonNull(supplier, "supplier");
        try { return value(supplier.get()); }
        catch (RuntimeException e) { return raise(e); }
    }
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <V> ThrowsOr<V> present(Optional<V> optional, Supplier<Throwable> exceptionSupplier)
    {
        Objects.requireNonNull(optional, "optional");
        Objects.requireNonNull(exceptionSupplier, "exceptionSupplier");
        return optional.map(ThrowsOr::value).orElseGet(() -> raise(exceptionSupplier.get()));
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
    
    public boolean isEmpty() { return value == null && exception == null; }
    
    public boolean isPresent() { return value != null; }
    
    public Optional<V> value() { return Optional.ofNullable(value); }
    
    public V getOrThrow() throws Rethrow, NoSuchElementException
    {
        if (exception != null) { throw new Rethrow(exception); }
        if (value != null) { return value; }
        throw new NoSuchElementException();
    }
    
    public boolean isExceptional() { return exception != null; }
    
    public Optional<Throwable> exception() { return Optional.ofNullable(exception); }
    
    @SuppressWarnings("unchecked")
    public <T> ThrowsOr<T> propagate()
    {
        if (exception != null) { return (ThrowsOr<T>) this; }
        throw new IllegalStateException("Cannot propagate: not exceptional");
    }
    
    public Stream<V> stream() { return (value != null) ? Stream.of(value) : Stream.empty(); }
}

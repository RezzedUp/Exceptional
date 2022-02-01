/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import com.rezzedup.util.exceptional.checked.CheckedDoubleSupplier;
import com.rezzedup.util.exceptional.checked.CheckedIntSupplier;
import com.rezzedup.util.exceptional.checked.CheckedLongSupplier;
import com.rezzedup.util.exceptional.checked.CheckedRunnable;
import com.rezzedup.util.exceptional.checked.CheckedSupplier;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Attempts to perform potentially exceptional actions, automatically handling any thrown exception.
 */
@FunctionalInterface
public interface Attempt extends Catcher.Source<Exception>
{
    /**
     * Creates a new Attempt with the provided catcher.
     *
     * @param catcher   exception catcher
     * @return a new attempt
     */
    @SuppressWarnings("unchecked")
    static Attempt with(Catcher<? super Exception> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        return () -> (Catcher<Exception>) catcher;
    }
    
    /**
     * Attempts performing potentially exception actions by ignoring any thrown exception.
     *
     * @return an attempt which ignores exceptions
     * @see Catcher#ignore(Throwable)
     */
    static Attempt ignoring() { return () -> Catcher::ignore; }
    
    /**
     * Attempts performing potentially exception actions by printing any thrown exception.
     *
     * @return an attempt which prints exceptions
     * @see Catcher#print(Throwable)
     */
    static Attempt printing() { return () -> Catcher::print; }
    
    /**
     * Attempts performing potentially exception actions by rethrowing any thrown exception.
     *
     * @return an attempt which rethrows exceptions
     * @see Catcher#rethrow(Throwable)
     */
    static Attempt rethrowing() { return () -> Catcher::rethrow; }
    
    /**
     * Runs the potentially exceptional runnable, automatically handling any thrown exception
     * with {@link #catcher()}.
     *
     * @param runnable  potentially exceptional runnable
     */
    default void run(CheckedRunnable<? extends Exception> runnable)
    {
        try { runnable.runOrThrow(); }
        catch (Exception e) { catcher().handleOrRethrowError(e); }
    }
    
    /**
     * Gets the value from the potentially exceptional supplier, automatically handling any thrown
     * exception with {@link #catcher()}.
     *
     * @param supplier  potentially exceptional supplier
     * @param <T>       value type
     *
     * @return the result from the supplier, or empty if an exception is thrown
     */
    @SuppressWarnings("ConstantConditions")
    default <T> Optional<T> get(CheckedSupplier<@NullOr T, ? extends Exception> supplier)
    {
        try { return Optional.ofNullable(supplier.getOrThrow()); }
        catch (Exception e) { catcher().handleOrRethrowError(e); }
        return Optional.empty();
    }
    
    /**
     * Gets the value from the potentially exceptional int supplier, automatically handling any thrown
     * exception with {@link #catcher()}.
     *
     * @param supplier  potentially exceptional int supplier
     *
     * @return the result from the supplier, or empty if an exception is thrown
     */
    default OptionalInt getAsInt(CheckedIntSupplier<? extends Exception> supplier)
    {
        try { return OptionalInt.of(supplier.getAsIntOrThrow()); }
        catch (Exception e) { catcher().handleOrRethrowError(e); }
        return OptionalInt.empty();
    }
    
    /**
     * Gets the value from the potentially exceptional long supplier, automatically handling any thrown
     * exception with {@link #catcher()}.
     *
     * @param supplier  potentially exceptional long supplier
     *
     * @return the result from the supplier, or empty if an exception is thrown
     */
    default OptionalLong getAsLong(CheckedLongSupplier<? extends Exception> supplier)
    {
        try { return OptionalLong.of(supplier.getAsLongOrThrow()); }
        catch (Exception e) { catcher().handleOrRethrowError(e); }
        return OptionalLong.empty();
    }
    
    /**
     * Gets the value from the potentially exceptional double supplier, automatically handling any thrown
     * exception with {@link #catcher()}.
     *
     * @param supplier  potentially exceptional double supplier
     *
     * @return the result from the supplier, or empty if an exception is thrown
     */
    default OptionalDouble getAsDouble(CheckedDoubleSupplier<? extends Exception> supplier)
    {
        try { return OptionalDouble.of(supplier.getAsDoubleOrThrow()); }
        catch (Exception e) { catcher().handleOrRethrowError(e); }
        return OptionalDouble.empty();
    }
}

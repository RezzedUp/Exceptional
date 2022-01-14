/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import com.rezzedup.util.exceptional.checked.CheckedRunnable;
import com.rezzedup.util.exceptional.checked.CheckedSupplier;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.Optional;

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
        catch (Exception e) { catcher().handleSafely(e); }
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
        catch (Exception e) { catcher().handleSafely(e); }
        return Optional.empty();
    }
}

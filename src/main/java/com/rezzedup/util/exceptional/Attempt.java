/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
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
 * Utilities for handling exceptions.
 */
@SuppressWarnings("unused")
public class Attempt
{
    private Attempt() { throw new UnsupportedOperationException(); }
    
    /**
     * Attempts to return the supplied value by handling
     * any exceptions with the provided catcher.
     *
     * @param catcher   exception handler
     * @param supplier  value supplier
     * @param <T>       type of supplied value
     *
     * @return  the supplied value wrapped in an optional
     *          otherwise empty, unless the catcher itself
     *          throws an exception
     */
    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    public static <T> Optional<T> with(Catcher<? super Exception> catcher, CheckedSupplier<@NullOr T, ? extends Exception> supplier)
    {
        Objects.requireNonNull(catcher, "catcher");
        Objects.requireNonNull(supplier, "supplier");
        
        try { return Optional.ofNullable(supplier.get()); }
        catch (Exception e) { catcher.accept(e); }
        
        return Optional.empty();
    }
    
    /**
     * Attempts to run the action by passing any
     * exceptions to the provided catcher.
     *
     * @param catcher   exception handler
     * @param action    action runnable
     */
    @SuppressWarnings("NullableProblems")
    public static void with(Catcher<? super Exception> catcher, CheckedRunnable<? extends Exception> action)
    {
        Objects.requireNonNull(catcher, "catcher");
        Objects.requireNonNull(action, "action");
        
        try { action.run(); }
        catch (Exception e) { catcher.accept(e); }
    }
    
    /**
     * Attempts to return the supplied value by ignoring
     * any exceptions that occur.
     *
     * @param supplier  value supplier
     * @param <T>       type of supplied value
     *
     * @return  the supplied value wrapped in an optional
     *          or empty if an exception occurs
     *
     * @see Catcher#ignore(Throwable)
     */
    public static <T> Optional<T> ignoring(CheckedSupplier<@NullOr T, ? extends Exception> supplier)
    {
        return with(Catcher::ignore, supplier);
    }
    
    /**
     * Attempts to run the action by ignoring any
     * exceptions that occur.
     *
     * @param action    action runnable
     *
     * @see Catcher#ignore(Throwable)
     */
    public static void ignoring(CheckedRunnable<? extends Exception> action)
    {
        with(Catcher::ignore, action);
    }
    
    /**
     * Attempts to return the supplied value by printing
     * any exceptions that occur.
     *
     * @param supplier  value supplier
     * @param <T>       type of supplied value
     *
     * @return  the supplied value wrapped in an optional
     *          or empty if an exception occurs
     *
     * @see Catcher#print(Throwable)
     */
    public static <T> Optional<T> printing(CheckedSupplier<@NullOr T, ? extends Exception> supplier)
    {
        return with(Catcher::print, supplier);
    }
    
    /**
     * Attempts to run the action by printing any
     * exceptions that occur.
     *
     * @param action    action runnable
     *
     * @see Catcher#print(Throwable)
     */
    public static void printing(CheckedRunnable<? extends Exception> action)
    {
        with(Catcher::print, action);
    }
    
    /**
     * Attempts to return the supplied value by rethrowing
     * any exceptions that occur.
     *
     * @param supplier  value supplier
     *
     * @param <T>       type of supplied value
     *
     * @return  the supplied value wrapped in an optional
     *          or empty if an exception occurs
     *
     * @see Catcher#rethrow(Throwable)
     */
    public static <T> Optional<T> rethrowing(CheckedSupplier<@NullOr T, ? extends Exception> supplier)
    {
        return with(Catcher::rethrow, supplier);
    }
    
    /**
     * Attempts to run the action by rethrowing any
     * exceptions that occur.
     *
     * @param action    action runnable
     *
     * @see Catcher#rethrow(Throwable)
     */
    public static void rethrowing(CheckedRunnable<? extends Exception> action)
    {
        with(Catcher::rethrow, action);
    }
}

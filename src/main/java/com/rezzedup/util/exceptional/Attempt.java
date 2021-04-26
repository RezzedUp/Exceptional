/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utilities for handling runtime exceptions.
 */
@SuppressWarnings("unused")
public class Attempt
{
    private Attempt() { throw new UnsupportedOperationException(); }
    
    /**
     * Attempts to return the supplied value by handling all
     * runtime exceptions with the provided catcher.
     *
     * @param catcher   runtime exception handler
     * @param supplier  value supplier
     * @param <T>       type of supplied value
     *
     * @return  the supplied value wrapped in an optional
     *          otherwise empty, unless the catcher itself
     *          throws an exception
     *
     * @throws NullPointerException     if any arguments are {@code null}
     */
    public static <T> Optional<T> with(Catcher<RuntimeException> catcher, Supplier<@NullOr T> supplier)
    {
        Objects.requireNonNull(catcher, "catcher");
        Objects.requireNonNull(supplier, "supplier");
        
        try { return Optional.ofNullable(supplier.get()); }
        catch (RuntimeException e) { catcher.accept(e); }
        
        return Optional.empty();
    }
    
    /**
     * Attempts to run the action by passing any runtime
     * exceptions to the provided catcher.
     *
     * @param catcher   runtime exception handler
     * @param action    action runnable
     *
     * @throws NullPointerException     if any arguments are {@code null}
     */
    public static void with(Catcher<RuntimeException> catcher, Runnable action)
    {
        Objects.requireNonNull(catcher, "catcher");
        Objects.requireNonNull(action, "action");
        
        try { action.run(); }
        catch (RuntimeException e) { catcher.accept(e); }
    }
    
    /**
     * Attempts to return the supplied value by ignoring any
     * runtime exceptions that occur.
     *
     * @param supplier  value supplier
     * @param <T>       type of supplied value
     *
     * @return  the supplied value wrapped in an optional
     *          or empty if a runtime exception occurs
     *
     * @throws NullPointerException     if any arguments are {@code null}
     *
     * @see Catcher#ignore(Throwable)
     */
    public static <T> Optional<T> ignoring(Supplier<@NullOr T> supplier)
    {
        return with(Catcher::ignore, supplier);
    }
    
    /**
     * Attempts to run the action by ignoring any runtime
     * exceptions that occur.
     *
     * @param action    action runnable
     *
     * @throws NullPointerException     if any arguments are {@code null}
     *
     * @see Catcher#ignore(Throwable)
     */
    public static void ignoring(Runnable action)
    {
        with(Catcher::ignore, action);
    }
    
    /**
     * Attempts to return the supplied value by printing any
     * runtime exceptions that occur.
     *
     * @param supplier  value supplier
     * @param <T>       type of supplied value
     *
     * @return  the supplied value wrapped in an optional
     *          or empty if a runtime exception occurs
     *
     * @throws NullPointerException     if any arguments are {@code null}
     *
     * @see Catcher#print(Throwable)
     */
    public static <T> Optional<T> printing(Supplier<@NullOr T> supplier)
    {
        return with(Catcher::print, supplier);
    }
    
    /**
     * Attempts to run the action by printing any runtime
     * exceptions that occur.
     *
     * @param action    action runnable
     *
     * @throws NullPointerException     if any arguments are {@code null}
     *
     * @see Catcher#print(Throwable)
     */
    public static void printing(Runnable action)
    {
        with(Catcher::print, action);
    }
}

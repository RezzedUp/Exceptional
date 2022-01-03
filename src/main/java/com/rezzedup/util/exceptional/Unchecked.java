/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional;

import com.rezzedup.util.exceptional.checked.CheckedBiConsumer;
import com.rezzedup.util.exceptional.checked.CheckedBiFunction;
import com.rezzedup.util.exceptional.checked.CheckedConsumer;
import com.rezzedup.util.exceptional.checked.CheckedFunction;
import com.rezzedup.util.exceptional.checked.CheckedRunnable;
import com.rezzedup.util.exceptional.checked.CheckedSupplier;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Unchecked
{
    private Unchecked() { throw new UnsupportedOperationException(); }
    
    /**
     * Converts a {@code CheckedBiConsumer} into a regular {@code BiConsumer}.
     * Any caught exceptions will be rethrown with: {@link Rethrow#caught(Throwable)}
     *
     * @param consumer  the checked biconsumer
     * @param <T>       first argument type
     * @param <U>       second argument type
     *
     * @return the CheckedBiConsumer wrapped by an unchecked BiConsumer
     */
    public static <T, U> BiConsumer<T, U> biConsumer(CheckedBiConsumer<T, U, ? extends Exception> consumer)
    {
        Objects.requireNonNull(consumer, "consumer");
        
        return (t, u) ->
        {
            try { consumer.accept(t, u); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    /**
     * Converts a {@code CheckedBiFunction} into a regular {@code BiFunction}.
     * Any caught exceptions will be rethrown with: {@link Rethrow#caught(Throwable)}
     *
     * @param function  the checked bifunction
     * @param <T>       first argument type
     * @param <U>       second argument type
     * @param <R>       return type
     *
     * @return the CheckedBiFunction wrapped by an unchecked BiFunction
     */
    public static <T, U, R> BiFunction<T, U, R> biFunction(CheckedBiFunction<T, U, R, ? extends Exception> function)
    {
        Objects.requireNonNull(function, "function");
        
        return (t, u) ->
        {
            try { return function.accept(t, u); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    /**
     * Converts a {@code CheckedConsumer} into a regular {@code Consumer}.
     * Any caught exceptions will be rethrown with: {@link Rethrow#caught(Throwable)}
     *
     * @param consumer  the checked consumer
     * @param <T>       argument type
     *
     * @return the CheckedConsumer wrapped by an unchecked Consumer
     */
    public static <T> Consumer<T> consumer(CheckedConsumer<T, ? extends Exception> consumer)
    {
        Objects.requireNonNull(consumer, "consumer");
        
        return t ->
        {
            try { consumer.accept(t); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    /**
     * Converts a {@code CheckedFunction} into a regular {@code Function}.
     * Any caught exceptions will be rethrown with: {@link Rethrow#caught(Throwable)}
     *
     * @param function  the checked function
     * @param <T>       argument type
     * @param <R>       return type
     *
     * @return the CheckedFunction wrapped by an unchecked Function
     */
    public static <T, R> Function<T, R> function(CheckedFunction<T, R, ? extends Exception> function)
    {
        Objects.requireNonNull(function, "function");
        
        return t ->
        {
            try { return function.apply(t); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    /**
     * Converts a {@code CheckedRunnable} into a regular {@code Runnable}.
     * Any caught exceptions will be rethrown with: {@link Rethrow#caught(Throwable)}
     *
     * @param runnable  the checked runnable
     *
     * @return the CheckedRunnable wrapped by an unchecked Runnable
     */
    public static Runnable runnable(CheckedRunnable<? extends Exception> runnable)
    {
        Objects.requireNonNull(runnable, "runnable");
        
        return () ->
        {
            try { runnable.run(); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
    
    /**
     * Converts a {@code CheckedSupplier} into a regular {@code Supplier}.
     * Any caught exceptions will be rethrown with: {@link Rethrow#caught(Throwable)}
     *
     * @param supplier  the checked supplier
     * @param <T>       return type
     *
     * @return the CheckedSupplier wrapped by an unchecked Supplier
     */
    public static <T> Supplier<T> supplier(CheckedSupplier<T, ? extends Exception> supplier)
    {
        Objects.requireNonNull(supplier, "supplier");
        
        return () ->
        {
            try { return supplier.get(); }
            catch (Exception e) { throw Rethrow.caught(e); }
        };
    }
}

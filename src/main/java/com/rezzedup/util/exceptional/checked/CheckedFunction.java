/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Exceptional>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.exceptional.checked;

import com.rezzedup.util.exceptional.Catcher;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.function.Function;

/**
 * {@code Function} that can throw checked exceptions.
 *
 * @param <T>   argument type
 * @param <R>   return type
 * @param <E>   exception type
 *
 * @see Function
 */
@FunctionalInterface
public interface CheckedFunction<T, R, E extends Throwable> extends Catcher.Swap<Throwable, CheckedFunction<T, R, E>>, Function<T, R>
{
    static <T, R, E extends Throwable> CheckedFunction<T, R, E> of(CheckedFunction<T, R, E> function)
    {
        return function;
    }
    
    static <T, R, E extends Throwable> CheckedFunction<T, R, E> of(Catcher<Throwable> catcher, CheckedFunction<T, R, E> function)
    {
        return function.catcher(catcher);
    }
    
    /**
     * Applies this function to the given argument.
     *
     * @param t     the function argument
     *
     * @return the function result
     * @throws E a checked exception
     */
    R applyOrThrow(T t) throws E;
    
    @Override
    default @NullOr R apply(T t)
    {
        try { return applyOrThrow(t); }
        catch (Throwable e)
        {
            catcher().handleSafely(e);
            return null;
        }
    }
    
    @Override
    default Catcher<Throwable> catcher() { return Catcher::rethrow; }
    
    @Override
    default CheckedFunction<T, R, E> catcher(Catcher<Throwable> catcher)
    {
        Objects.requireNonNull(catcher, "catcher");
        if (catcher == catcher()) { return this; }
        
        class Impl<_T, _R, _E> implements CheckedFunction<T, R, E>
        {
            CheckedFunction<T, R, E> origin() { return CheckedFunction.this; }
            
            @Override
            public R applyOrThrow(T t) throws E { return origin().applyOrThrow(t); }
    
            @Override
            public Catcher<Throwable> catcher() { return catcher; }
            
            @Override
            public String toString() { return Checked.implToString(getClass(), origin(), catcher()); }
        }
        
        return (this instanceof Impl) ? ((Impl<T, R, E>) this).origin().catcher(catcher) : new Impl<>();
    }
}
